package com.seaborn.seabornFishing;

import de.tr7zw.nbtapi.NBT;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

public class GameScheduler {

    private SeabornFishing plugin;

    public GameScheduler(SeabornFishing plugin) {
        this.plugin = plugin;
    }

    public GameSchedule createSchedule(Player player) {
        GameSchedule schedule = new GameSchedule(player);

        ItemStack rod = player.getInventory().getItemInMainHand();
        float difficulty = NBT.get(rod, nbt -> {

            Double diff = nbt.getDouble("difficulty");
            return diff == null ? 1f : diff.floatValue();

        });

        schedule.addGame(plugin.minigames.get("numbers"), this.plugin, difficulty);
        return schedule;
    }

}
