package com.seaborn.seabornCore;

import com.seaborn.seabornCore.db.DatabaseInitializer;
import com.seaborn.seabornCore.db.DatabaseManager;
import com.seaborn.seabornCore.db.dao.PlayerDataDAO;
import com.seaborn.seabornCore.db.model.PlayerData;
import com.seaborn.seabornCore.events.PeriodicSaveEvent;
import com.seaborn.seabornCore.scoreboard.ScoreboardManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public final class SeabornCore extends JavaPlugin {

    private static SeabornCore instance;

    public Bossbars bossbars;
    private static ScoreboardManager scoreboardManager;
    private static Economy econ;
    private static PlayerDataDAO playerDataDAO;
    private static DatabaseManager dbManager;

    int sched;

    @Override
    public void onEnable() {

        instance = this;

        saveDefaultConfig();

        this.setupDatabase();

        scoreboardManager = new ScoreboardManager();
        this.bossbars = new Bossbars();
        this.setupEconomy();

        getServer().getPluginManager().registerEvents(new Listeners(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
        getServer().getPluginManager().registerEvents(scoreboardManager, this);

        if(!Bukkit.getScheduler().isCurrentlyRunning(sched)) {
            sched = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                this.bossbars.updateGlobalBar();
                scoreboardManager.update();
            }, 20, 20);
        }

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            getLogger().info("Saving all player data...");

            List<PlayerData> dataToSave = new ArrayList<>();

            for (Player player : Bukkit.getOnlinePlayers()) {
                dataToSave.add(new PlayerData(player));
            }

            CompletableFuture.runAsync(() -> {
                for (PlayerData data : dataToSave) {
                    getPlayerDataDAO().save(data);
                }

                Bukkit.getScheduler().runTask(this, () -> {
                    getLogger().info("Periodic save completed!");
                });

            });

            getServer().getPluginManager().callEvent(new PeriodicSaveEvent());

        }, 5 * 60 * 20, 5 * 60 * 20); // 5 minutes

    }

    @Override
    public void onDisable() {

        getLogger().info("Saving all player data...");

        for (Player player : Bukkit.getOnlinePlayers()) {
            PlayerData data = new PlayerData(player);
            getPlayerDataDAO().save(data);
        }

        dbManager.close();

    }

    private void setupDatabase() {

        String host = getConfig().getString("database.host");
        int port = getConfig().getInt("database.port");
        String dbName = getConfig().getString("database.name");
        String user = getConfig().getString("database.user");
        String pass = getConfig().getString("database.password");

        dbManager = new DatabaseManager(
                host, port, dbName, user, pass
        );

        DatabaseInitializer.runMigrations(dbManager);

        playerDataDAO = new PlayerDataDAO(dbManager.getDataSource());
    }

    private void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null) return;

        econ = rsp.getProvider();
    }

    public static Economy getEconomy() { return econ; }
    public static PlayerDataDAO getPlayerDataDAO() { return playerDataDAO; }
    public static SeabornCore getInstance() { return instance; }
}
