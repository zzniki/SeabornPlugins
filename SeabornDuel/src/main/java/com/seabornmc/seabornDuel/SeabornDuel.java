package com.seabornmc.seabornDuel;

import org.bukkit.plugin.java.JavaPlugin;

public final class SeabornDuel extends JavaPlugin {

    private static SeabornDuel instance;

    private DuelManager duelManager;

    @Override
    public void onEnable() {

        instance = this;

        this.duelManager = new DuelManager(1);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static SeabornDuel getInstance() { return instance; }
}
