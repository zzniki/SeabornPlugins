package com.seabornmc.seabornWeapons;

import com.seabornmc.seabornWeapons.commands.WeaponCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class SeabornWeapons extends JavaPlugin {

    private static WeaponManager weaponManager;
    private static SeabornWeapons instance;

    public static WeaponManager getWeaponManager() {
        return weaponManager;
    }

    public static SeabornWeapons getInstance() { return instance; }

    @Override
    public void onEnable() {

        instance = this;

        weaponManager = new WeaponManager();
        weaponManager.loadFromAPI();

        getServer().getPluginManager().registerEvents(new Listeners(), this);

        this.registerCommands();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerCommands() {
        new WeaponCommand().getCommand().register(this);
    }
}
