package com.seaborn.seabornCrews;

import com.infernalsuite.asp.api.AdvancedSlimePaperAPI;
import com.infernalsuite.asp.api.loaders.SlimeLoader;
import com.infernalsuite.asp.api.world.SlimeWorldInstance;
import com.infernalsuite.asp.loaders.mysql.MysqlLoader;
import com.seaborn.seabornCrews.commands.CrewAdminCommand;
import com.seaborn.seabornCrews.commands.CrewCommand;
import com.seaborn.seabornCrews.dao.CrewDAO;
import com.seaborn.seabornCrews.listeners.ChatListener;
import com.seaborn.seabornCrews.listeners.IslandListener;
import com.seaborn.seabornCrews.listeners.PortalListener;
import com.seaborn.seabornCrews.placeholders.CrewExpansion;
import com.seaborn.seabornCrews.service.CrewService;
import com.seaborn.seabornCrews.service.IslandService;
import dev.jorel.commandapi.CommandAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class SeabornCrews extends JavaPlugin {

    private static SeabornCrews instance;

    private static CrewService crewService;
    private static IslandService islandService;
    private static CrewManager crewManager;

    private static CrewDAO crewDAO;
    private static AdvancedSlimePaperAPI aspApi;
    private static SlimeLoader loader;

    private static DatabaseManager dbManager;

    @Override
    public void onEnable() {

        saveDefaultConfig();

        instance = this;

        String host = getConfig().getString("database.host");
        int port = getConfig().getInt("database.port");
        String dbName = getConfig().getString("database.name");
        String user = getConfig().getString("database.user");
        String pass = getConfig().getString("database.password");

        dbManager = new DatabaseManager(
                host, port, dbName, user, pass
        );

        DatabaseInitializer.runMigrations(dbManager);

        crewDAO = new CrewDAO(dbManager.getDataSource());
        crewService = new CrewService(crewDAO);
        crewManager = new CrewManager();

        try {
            aspApi = AdvancedSlimePaperAPI.instance();
            loader = new MysqlLoader(
                    "jdbc:mysql://{host}:{port}/{database}?autoReconnect=true&allowMultiQueries=true&useSSL={usessl}",
                    host,
                    port,
                    dbName,
                    false,
                    user,
                    pass
            );
        } catch (Throwable t) {
            getLogger().severe("AdvancedSlimePaper API not found, disabling plugin...");
            getServer().getPluginManager().disablePlugin(this);
        }

        islandService = new IslandService(aspApi, loader);

        getServer().getPluginManager().registerEvents(new IslandListener(), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(new PortalListener(), this);

        this.setupCommands();

        new CrewExpansion().register();

    }

    @Override
    public void onDisable() {

        this.saveIslands();

        dbManager.close();

        CommandAPI.unregister("crew");
        CommandAPI.unregister("crewadmin");

    }

    public void saveIslands() {
        getLogger().info("Saving all islands...");
        for (SlimeWorldInstance instance : aspApi.getLoadedWorlds()) {
            try {
                aspApi.saveWorld(instance);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        getLogger().info("Saved all islands...");
    }

    private void setupCommands() {
        new CrewCommand().getCommand().register(this);
        new CrewAdminCommand().getCommand().register(this);
    }

    private void registerAutoSaving() {

    }

    public static SeabornCrews getInstance() { return instance; }

    public static CrewService getCrewService() { return crewService; }
    public static IslandService getIslandService() { return islandService; }
    public static CrewManager getCrewManager() { return crewManager; }

}
