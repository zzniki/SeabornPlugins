package com.seabornmc.seabornChairs;

import com.seabornmc.seabornChairs.commands.ChairCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class SeabornChairs extends JavaPlugin {

    private static SeabornChairs instance;
    private static ChairManager chairManager;

    public static ChairManager getChairManager() { return chairManager; }
    public static SeabornChairs getInstance() { return instance; }

    @Override
    public void onEnable() {

        instance = this;
        chairManager = new ChairManager();

        getServer().getPluginManager().registerEvents(new Listeners(), this);

        new ChairCommand().getCommand().register(this);

    }

    @Override
    public void onDisable() {
        chairManager.removeChairs();
    }
}
