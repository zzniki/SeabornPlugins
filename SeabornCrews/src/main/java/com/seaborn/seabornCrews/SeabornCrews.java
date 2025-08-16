package com.seaborn.seabornCrews;

import com.infernalsuite.asp.api.loaders.SlimeLoader;
import com.infernalsuite.asp.api.world.SlimeWorldInstance;
import com.infernalsuite.asp.loaders.mysql.MysqlLoader;
import com.seaborn.seabornCrews.commands.CrewCommand;
import com.seaborn.seabornCrews.CrewService;
import com.seaborn.seabornCrews.listeners.CrewChatListener;
import com.seaborn.seabornCrews.model.Crew;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.infernalsuite.asp.api.AdvancedSlimePaperAPI;
import dev.jorel.commandapi.CommandAPI;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;

import javax.sql.DataSource;
import java.util.logging.Level;

public final class SeabornCrews extends JavaPlugin {
    private static SeabornCrews instance;
    private HikariDataSource dataSource;
    private CrewService crewService;
    private AdvancedSlimePaperAPI aspApi;
    private SlimeLoader loader;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        try {
            setupDatabase();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "Failed to setup database", e);
            setEnabled(false);
            return;
        }

        // Grab the AdvancedSlimePaper API instance (SWP)
        try {
            aspApi = AdvancedSlimePaperAPI.instance();
            loader = new MysqlLoader(
                    "jdbc:mysql://{host}:{port}/{database}?autoReconnect=true&allowMultiQueries=true&useSSL={usessl}",
                    "127.0.0.1",
                    3306,
                    "islands",
                    false,
                    "seabornislands",
                    "isl.la4n@seab_or"
            );
        } catch (Throwable t) {
            getLogger().warning("AdvancedSlimePaper API not found - islands will not be available.");
            aspApi = null;
        }

        crewService = new CrewService(this, dataSource, aspApi);
        crewService.init();

        new CrewCommand().register(crewService).register(this);
        Bukkit.getPluginManager().registerEvents(new CrewChatListener(crewService), this);
        Bukkit.getPluginManager().registerEvents(new org.bukkit.event.Listener() {
            @org.bukkit.event.EventHandler
            public void onPlayerQuit(org.bukkit.event.player.PlayerQuitEvent e) {
                String worldName = e.getPlayer().getWorld().getName();
                crewService.checkAndUnloadIslandIfEmpty(worldName);
            }
            @org.bukkit.event.EventHandler
            public void onPlayerTeleport(org.bukkit.event.player.PlayerTeleportEvent e) {
                String fromWorld = e.getFrom().getWorld().getName();
                String toWorld = e.getTo().getWorld().getName();
                // Only check if leaving a crew island world
                if (!fromWorld.equals(toWorld)) {
                    crewService.checkAndUnloadIslandIfEmpty(fromWorld);
                }
            }
            @org.bukkit.event.EventHandler
            public void onPlayerJoin(PlayerLoginEvent e) {
                crewService.handlePlayerJoin(e.getPlayer());
            }
        }, this);

        // Save all loaded islands every 10 minutes
        if (aspApi != null) {
            Bukkit.getScheduler().runTaskTimer(this, () -> {
                for (org.bukkit.World world : Bukkit.getWorlds()) {
                    if (world.getName().startsWith("crew_island_")) {
                        SlimeWorldInstance instance = aspApi.getLoadedWorld(world.getName());
                        if (instance != null) {
                            try {
                                aspApi.saveWorld(instance);
                            } catch (Exception e) {
                                getLogger().severe("Failed to autosave island: " + e.getMessage());
                            }
                        }
                    }
                }
            }, 20L * 60 * 10, 20L * 60 * 10); // every 10 minutes
        }

        getLogger().info("SeabornCrews enabled");
    }

    @Override
    public void onDisable() {
        if (crewService != null) crewService.shutdown();
        if (dataSource != null && !dataSource.isClosed()) dataSource.close();
        getLogger().info("SeabornCrews disabled");
    }

    private void setupDatabase() throws Exception {
        // config keys: db.host, db.port, db.database, db.user, db.password
        HikariConfig cfg = new HikariConfig();
        String host = getConfig().getString("db.host", "localhost");
        int port = getConfig().getInt("db.port", 3306);
        String database = getConfig().getString("db.database", "seaborncrews");
        String user = getConfig().getString("db.user", "root");
        String pass = getConfig().getString("db.password", "");

        String jdbc = String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true", host, port, database);
        cfg.setJdbcUrl(jdbc);
        cfg.setUsername(user);
        cfg.setPassword(pass);
        cfg.setMaximumPoolSize(getConfig().getInt("db.poolSize", 10));
        cfg.addDataSourceProperty("cachePrepStmts", "true");
        cfg.addDataSourceProperty("prepStmtCacheSize", "250");
        cfg.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(cfg);
    }

    public static SeabornCrews getInstance() {
        return instance;
    }

    public CrewService getCrewService() {
        return crewService;
    }

    public SlimeLoader getLoader() { return this.loader; }

    // API for external plugins to add reputation
    public void addReputation(String crewName, int amount) {
        if (crewService != null) crewService.addReputation(crewName, amount);
    }
}
