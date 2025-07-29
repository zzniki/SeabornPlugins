package com.seaborn.seabornMap;

import org.bukkit.plugin.java.JavaPlugin;

public final class SeabornMap extends JavaPlugin {

    private static SeabornMap instance;
    public static SeabornMap getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {

        instance = this;
        getServer().getPluginManager().registerEvents(new Listeners(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
