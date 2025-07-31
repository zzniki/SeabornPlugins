package com.seaborn.seabornCore;

import org.bukkit.Bukkit;
import org.bukkit.boss.BossBar;
import org.bukkit.plugin.java.JavaPlugin;

public final class SeabornCore extends JavaPlugin {

    public Bossbars bossbars;

    int sched;

    @Override
    public void onEnable() {

        this.bossbars = new Bossbars();

        getServer().getPluginManager().registerEvents(new Listeners(this), this);

        if(!Bukkit.getScheduler().isCurrentlyRunning(sched)) {
            sched = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
                this.bossbars.updateGlobalBar();
            }, 20, 20);
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
