package com.seabornmc.seabornDialogs.commands;

import com.seabornmc.seabornDialogs.Display;
import com.seabornmc.seabornDialogs.SeabornDialogs;
import com.seabornmc.seabornDialogs.dialogs.Dialog;
import com.seabornmc.seabornDialogs.dialogs.OptionDialog;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class SpawnTestCommand {

    private final CommandAPICommand command;

    public SpawnTestCommand() {
        this.command = new CommandAPICommand("dialogspawn")
                .withPermission("seaborn.dialogspawn")
                .executes((sender, args) -> {
                        Player player = ((Player) sender);
                        Location loc = player.getEyeLocation()
                                .add(player.getEyeLocation().getDirection().normalize().multiply(2))
                                .addRotation(180, 0);
                        OptionDialog dialog = new OptionDialog(loc, player, List.of("I'd like to sell this", "I'd like to sell my inventory"));
                        SeabornDialogs.getDialogManager().registerDialog(dialog);
                        dialog.onSelect((index) -> {

                            ((Player) sender).sendMessage(String.valueOf(index));
                            SeabornDialogs.getDialogManager().deleteDialog(dialog);

                            return null;
                        });
                        dialog.spawn();
                    }
                );
    }

    public CommandAPICommand getCommand() {
        return command;
    }
}
