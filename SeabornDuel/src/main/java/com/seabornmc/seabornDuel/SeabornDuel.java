package com.seabornmc.seabornDuel;

import com.seabornmc.seabornDuel.commands.DuelCommand;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class SeabornDuel extends JavaPlugin {

    private static SeabornDuel instance;

    private static DuelManager duelManager;
    private static InviteManager inviteManager;

    @Override
    public void onEnable() {

        instance = this;

        duelManager = new DuelManager(1);
        inviteManager = new InviteManager();

        new DuelCommand().getCommand().register(this);

    }

    @Override
    public void onDisable() {

        CommandAPI.unregister("duel");
        duelManager.endAllDuels();

    }

    public static SeabornDuel getInstance() { return instance; }
    public static InviteManager getInviteManager() { return inviteManager; }
    public static DuelManager getDuelManager() { return duelManager; }
}
