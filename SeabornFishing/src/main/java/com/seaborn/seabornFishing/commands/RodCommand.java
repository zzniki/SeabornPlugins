package com.seaborn.seabornFishing.commands;

import com.seaborn.seabornFishing.CustomRod;
import com.seaborn.seabornFishing.CustomRodItem;
import com.seaborn.seabornFishing.SeabornFishing;
import de.tr7zw.nbtapi.NBT;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RodCommand {

    private final SeabornFishing plugin;
    private final CommandAPICommand command;

    public RodCommand(SeabornFishing plugin) {
        this.plugin = plugin;

        this.command = new CommandAPICommand("rod")
                .withPermission("seaborn.rod")
                .withSubcommands(
                        getGive(),
                        getList(),
                        getModify()
                )
                .executes((sender, args) -> {
                    sender.sendMessage("list or give or modfloat");
                });
    }

    public CommandAPICommand getCommand() { return command; }

    private CommandAPICommand getGive() {

        ArgumentSuggestions<CommandSender> suggestions = ArgumentSuggestions.strings(info -> SeabornFishing.config.getCustomRods().keySet().toArray(String[]::new));

        return new CommandAPICommand("give")
                .withPermission("seaborn.rod.give")
                .withArguments(new StringArgument("type").replaceSuggestions(suggestions))
                .executes((sender, args) -> {
                    String id = (String) args.get("type");

                    if (!(sender instanceof Player)) return;

                    if (!SeabornFishing.config.getCustomRods().containsKey(id)) {
                        sender.sendMessage(ChatColor.RED + "Rod type does not exist");
                        return;
                    }

                    CustomRod rod = SeabornFishing.config.getCustomRod(id);
                    ItemStack item = new CustomRodItem(rod).getItemStack();
                    ((Player) sender).getInventory().addItem(item);

                    sender.sendMessage(ChatColor.GREEN + "Given!");
                });

    }
    private CommandAPICommand getList() {
        return new CommandAPICommand("list")
                .withPermission("seaborn.rod.list")
                .executes((sender, args) -> {
                    sender.sendMessage("Rod list:");
                    for (String id : SeabornFishing.config.getCustomRods().keySet()) {
                        sender.sendMessage(" -" + id);
                    }
                });
    }

    private CommandAPICommand getModify() {
        return new CommandAPICommand("modfloat")
                .withPermission("seaborn.rod.modfloat")
                .withArguments(new StringArgument("key"), new FloatArgument("value"))
                .executes((sender, args) -> {

                    if (!(sender instanceof Player)) return;
                    Player player = (Player) sender;

                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (item.getType() != Material.FISHING_ROD) {
                        sender.sendMessage(ChatColor.RED + "You must be holding a fishing rod");
                        return;
                    }

                    String key = (String) args.get("key");
                    Float value = (Float) args.get("value");

                    NBT.modify(item, nbt -> {
                        nbt.setFloat(key, value);
                    });

                    sender.sendMessage(ChatColor.GREEN + "Set float value " + key + " to " + value);

                });
    }

}
