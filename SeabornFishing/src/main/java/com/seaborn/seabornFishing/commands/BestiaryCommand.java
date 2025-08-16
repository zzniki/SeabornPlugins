package com.seaborn.seabornFishing.commands;

import com.seaborn.seabornFishing.SeabornFishing;
import com.seaborn.seabornFishing.dialogs.BestiaryDialog;
import com.seabornmc.seabornDialogs.SeabornDialogs;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.entity.Player;

public class BestiaryCommand {

    private final CommandAPICommand command;

    public BestiaryCommand() {
        this.command = new CommandAPICommand("bestiary")
                //.withPermission("seaborn.bestiary")
                .executes((sender, args) -> {

                    BestiaryDialog dialog = new BestiaryDialog((Player) sender);
                    SeabornDialogs.getDialogManager().registerDialog(dialog);
                    dialog.spawn();

                    sender.sendMessage("Shown!");
                });
    }

    public CommandAPICommand getCommand() { return this.command; }
}
