package com.seabornmc.seabornDialogs;

import com.seabornmc.seabornDialogs.commands.SpawnTestCommand;
import com.seabornmc.seabornDialogs.dialogs.DialogManager;
import com.seabornmc.seabornDialogs.interaction.InteractionListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class SeabornDialogs extends JavaPlugin {

    private static DisplayManager displayManager = null;
    private static DialogManager dialogManager = null;
    private static SeabornDialogs pluginInstance = null;

    public static DisplayManager getDisplayManager() {
        return displayManager;
    }

    public static DialogManager getDialogManager() {
        return dialogManager;
    }

    public static SeabornDialogs getPlugin() {
        return pluginInstance;
    }

    @Override
    public void onEnable() {

        pluginInstance = this;
        displayManager = new DisplayManager(this);
        dialogManager = new DialogManager();

        this.registerCommands();

        getServer().getPluginManager().registerEvents(new InteractionListener(), this);

    }

    @Override
    public void onDisable() {

        dialogManager.cleanup();
        displayManager.cleanupDisplays();

    }

    private void registerCommands() {
        new SpawnTestCommand().getCommand().register(this);
    }
}
