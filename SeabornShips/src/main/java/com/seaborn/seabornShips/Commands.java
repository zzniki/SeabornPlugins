package com.seaborn.seabornShips;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player player = (Player) sender;

        if (args.length >= 1) {
            switch (args[0]) {
                case "give":
                    if (args.length < 2) return true;
                    String id = args[1];
                    if (!SeabornShips.boatTypes.containsKey(id)) {
                        player.sendMessage(ChatColor.RED + "Boat type not found.");
                    }

                    player.getInventory().addItem(BoatItems.getItemFromType(SeabornShips.boatTypes.get(id)));
                    player.sendMessage(ChatColor.GREEN + "Boat given!");
                    break;
                case "list":
                    for (String key : SeabornShips.boatTypes.keySet()) {
                        player.sendMessage(ChatColor.YELLOW + key);
                    }
                    break;
                case "info":
                    if (args.length < 2) return true;
                    String boatId = args[1];
                    if (!SeabornShips.boatTypes.containsKey(boatId)) {
                        player.sendMessage(ChatColor.RED + "Boat type not found.");
                    }

                    BoatType type = SeabornShips.boatTypes.get(boatId);

                    player.sendMessage("Name: " + type.getName());
                    player.sendMessage("Speed: " + type.getSpeed());
                    player.sendMessage("Model: " + type.getEntityModel().toString());
                    player.sendMessage("Lore:");
                    for (String line : type.getLore()) {
                        player.sendMessage("- " + line);
                    }
                    break;

            }
        }

        return true;

    }

}
