package com.seabornmc.seabornCosmetics.commands;

import com.seabornmc.seabornCosmetics.SeabornCosmetics;
import com.seabornmc.seabornCosmetics.cosmetics.items.HatItem;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HatsCommand {

    private CommandAPICommand command;

    public HatsCommand() {
        this.command = new CommandAPICommand("hats")
                .withPermission("seaborn.hats")
                .withSubcommands(
                        getGive()
                );
    }

    public CommandAPICommand getGive() {

        ArgumentSuggestions<CommandSender> suggestions = ArgumentSuggestions.strings(info -> SeabornCosmetics.getCosmeticManager().getHats().keySet().toArray(String[]::new));

        return new CommandAPICommand("give")
                .withPermission("seaborn.hats.give")
                .withArguments(new StringArgument("name").replaceSuggestions(suggestions))
                .executes((sender, args) -> {
                    String name = (String) args.get("name");

                    if (!(sender instanceof Player)) return;

                    if (!SeabornCosmetics.getCosmeticManager().getHats().containsKey(name)) {
                        sender.sendMessage(ChatColor.RED + "Hat does not exist");
                        return;
                    }

                    HatItem hatItem = new HatItem(SeabornCosmetics.getCosmeticManager().getHat(name));
                    ((Player) sender).getInventory().addItem(hatItem.getItemStack());

                    sender.sendMessage(ChatColor.GREEN + "Given!");
                });
    }

    public CommandAPICommand getCommand() { return this.command; }

}
