package com.seabornmc.seabornCosmetics;

import com.seabornmc.seabornCosmetics.commands.HatsCommand;
import dev.jorel.commandapi.CommandAPI;
import org.bukkit.plugin.java.JavaPlugin;

public final class SeabornCosmetics extends JavaPlugin {

    private static CosmeticManager cosmeticManager;
    private static SeabornCosmetics instance;

    public static CosmeticManager getCosmeticManager() { return cosmeticManager; }
    public static SeabornCosmetics getInstance() { return instance; }

    @Override
    public void onEnable() {

        instance = this;

        cosmeticManager = new CosmeticManager();
        cosmeticManager.loadFromAPI();

        new HatsCommand().getCommand().register(this);

        getServer().getPluginManager().registerEvents(new Listeners(), this);

    }

    @Override
    public void onDisable() {

        CommandAPI.unregister("hats");

    }
}
