package com.seabornmc.seabornDuel.commands;

import com.seabornmc.seabornDuel.SeabornDuel;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.PlayerArgument;
import org.bukkit.entity.Player;

public class DuelCommand {

    private CommandAPICommand command;

    public DuelCommand() {
        this.command = new CommandAPICommand("duel")
                .withSubcommands(
                        getAccept(),
                        getReject()
                )
                .withArguments(
                        new PlayerArgument("player")
                )
                .executes((sender, args) -> {
                    Player inviter = (Player) sender;
                    Player target = (Player) args.get("player");
                    SeabornDuel.getInviteManager().createInvite(inviter, target);
                });
    }

    public CommandAPICommand getAccept() {
        return new CommandAPICommand("accept")
                .executes((sender, args) -> {
                    SeabornDuel.getInviteManager().accept((Player) sender);
                });
    }

    public CommandAPICommand getReject() {
        return new CommandAPICommand("reject");
    }

    public CommandAPICommand getCommand() {
        return command;
    }
}
