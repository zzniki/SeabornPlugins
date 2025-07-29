package com.oheers.fish.plugin;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.config.GuiConfig;
import com.oheers.fish.config.GuiFillerConfig;
import com.oheers.fish.config.MainConfig;
import com.oheers.fish.config.MessageConfig;

import java.util.logging.Level;

public class ConfigurationManager {
    private final EvenMoreFish plugin;

    public ConfigurationManager(EvenMoreFish plugin) {
        this.plugin = plugin;
    }

    public void loadConfigurations() {
        try {
            new MainConfig();
            new MessageConfig();
            new GuiConfig();
            new GuiFillerConfig();

            plugin.getLogger().info("Successfully loaded all configurations");
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load configurations", e);
            plugin.getServer().getPluginManager().disablePlugin(plugin);
        }
    }

    public void reloadConfigurations() {
        try {
            plugin.reloadConfig();
            plugin.saveDefaultConfig();

            MainConfig.getInstance().reload();
            MessageConfig.getInstance().reload();
            GuiConfig.getInstance().reload();
            GuiFillerConfig.getInstance().reload();

            plugin.getLogger().info("Successfully reloaded all configurations");
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to reload configurations", e);
        }
    }
}