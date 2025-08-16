package com.seaborn.seabornFishing.minigames;

import com.seaborn.seabornFishing.CustomRodItem;
import com.seaborn.seabornFishing.SeabornFishing;
import com.seaborn.seabornFishing.rewards.FishReward;
import de.tr7zw.nbtapi.NBT;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GameScheduler {

    private SeabornFishing plugin;

    public GameScheduler(SeabornFishing plugin) {
        this.plugin = plugin;
    }

    public GameSchedule createSchedule(Player player, FishReward reward) {
        GameSchedule schedule = new GameSchedule(player, reward);

        ItemStack rod = player.getInventory().getItemInMainHand();

        String rodType = NBT.get(rod, nbt -> {

            return nbt.getString("rod_type");

        });

        CustomRodItem rodItem = new CustomRodItem(SeabornFishing.config.getCustomRod(rodType));
        rodItem.loadItemStack(rod);

        float difficulty = rodItem.getTotalDifficulty();

        schedule.addGame(plugin.minigames.get("numbers"), this.plugin, difficulty);
        return schedule;
    }

}
