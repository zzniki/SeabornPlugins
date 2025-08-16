package com.seabornmc.seabornWeapons.commands;

import com.seabornmc.seabornWeapons.SeabornWeapons;
import com.seabornmc.seabornWeapons.weapons.Sword;
import com.seabornmc.seabornWeapons.weapons.items.SwordItem;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class WeaponCommand {

    private final CommandAPICommand command;

    public WeaponCommand() {
        this.command = new CommandAPICommand("weapon")
                .withPermission("seaborn.weapon")
                .withSubcommand(
                        getGive()
                )
                .executes((sender, args) -> {
                    sender.sendMessage("give");
                });
    }

    public CommandAPICommand getCommand() { return command; }

    private CommandAPICommand getGive() {

        return new CommandAPICommand("give")
                .withPermission("seaborn.weapon.give")
                .withSubcommands(
                        getGiveSword()
                )
                .executes((sender, args) -> {
                    sender.sendMessage("give");
                });
    }

    private CommandAPICommand getGiveSword() {
        ArgumentSuggestions<CommandSender> suggestions = ArgumentSuggestions.strings(info -> SeabornWeapons.getWeaponManager().getSwords().keySet().toArray(String[]::new));

        return new CommandAPICommand("sword")
                .withPermission("seaborn.weapon.give.sword")
                .withArguments(new StringArgument("name").replaceSuggestions(suggestions))
                .executes((sender, args) -> {
                    String id = (String) args.get("name");

                    if (!(sender instanceof Player)) return;

                    if (!SeabornWeapons.getWeaponManager().getSwords().containsKey(id)) {
                        sender.sendMessage(ChatColor.RED + "Sword type does not exist");
                        return;
                    }

                    Sword sword = SeabornWeapons.getWeaponManager().getSword(id);
                    ItemStack item = new SwordItem(sword).getItemStack();
                    ((Player) sender).getInventory().addItem(item);

                    sender.sendMessage(ChatColor.GREEN + "Given!");
                });
    }

}
