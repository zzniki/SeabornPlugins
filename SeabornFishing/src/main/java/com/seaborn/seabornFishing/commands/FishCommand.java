package com.seaborn.seabornFishing.commands;

import com.seaborn.seabornFishing.CustomRod;
import com.seaborn.seabornFishing.CustomRodItem;
import com.seaborn.seabornFishing.SeabornFishing;
import com.seaborn.seabornFishing.rewards.FishItemReward;
import com.seaborn.seabornFishing.rewards.FishReward;
import de.tr7zw.nbtapi.NBT;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.FloatArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FishCommand {

    private final SeabornFishing plugin;
    private final CommandAPICommand command;

    public FishCommand(SeabornFishing plugin) {
        this.plugin = plugin;

        this.command = new CommandAPICommand("fish")
                .withPermission("seaborn.fish")
                .withSubcommands(
                        getGive(),
                        getList()
                )
                .executes((sender, args) -> {
                    sender.sendMessage("list or give");
                });
    }

    public CommandAPICommand getCommand() { return command; }

    private CommandAPICommand getGive() {

        ArgumentSuggestions<CommandSender> suggestions = ArgumentSuggestions.strings(info -> SeabornFishing.config.getFish().keySet().toArray(String[]::new));

        return new CommandAPICommand("give")
                .withPermission("seaborn.fish.give")
                .withArguments(new StringArgument("type").replaceSuggestions(suggestions))
                .executes((sender, args) -> {
                    String id = (String) args.get("type");

                    if (!(sender instanceof Player)) return;

                    if (!SeabornFishing.config.getCustomRods().containsKey(id)) {
                        sender.sendMessage(ChatColor.RED + "Fish does not exist");
                        return;
                    }

                    FishReward reward = new FishItemReward(SeabornFishing.config.getFish().get(id), (Player) sender);
                    reward.giveToPlayer((Player) sender);

                    sender.sendMessage(ChatColor.GREEN + "Given!");
                });

    }
    private CommandAPICommand getList() {
        return new CommandAPICommand("list")
                .withPermission("seaborn.fish.list")
                .executes((sender, args) -> {
                    sender.sendMessage("Fish list:");
                    for (String id : SeabornFishing.config.getFish().keySet()) {
                        sender.sendMessage(" -" + id);
                    }
                });
    }

}
