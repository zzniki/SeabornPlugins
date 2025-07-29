package com.seaborn.seabornCore;

import org.bukkit.plugin.java.JavaPlugin;

public final class SeabornCore extends JavaPlugin {

    @Override
    public void onEnable() {

        getServer().getPluginManager().registerEvents(new Listeners(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
