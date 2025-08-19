package com.seabornmc.seabornChairs.commands;

import com.seabornmc.seabornChairs.SeabornChairs;
import dev.jorel.commandapi.CommandAPICommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

import java.awt.*;

public class ChairCommand {

    private CommandAPICommand command;

    public ChairCommand() {
        this.command = new CommandAPICommand("chair")
                .executes((sender, args) -> {
                    SeabornChairs.getChairManager().toggleChair((Player) sender);
                    sender.sendMessage(MiniMessage.miniMessage().deserialize("<green>Toggled chair!"));
                });
    }

    public CommandAPICommand getCommand() { return this.command; }

}
