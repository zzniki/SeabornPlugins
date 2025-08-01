package com.seaborn.seabornFishing;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

public class FishSelector {

    public static Fish selectFish(FishHook hook, Player player, ItemStack rod) {

        CustomRodItem properties = Utils.getRodProperties(rod);

        // Find region
        Location hookLocation = hook.getLocation();
        Region region = Utils.findRegion(hookLocation);

        if (region == null) {
            Bukkit.getLogger().log(Level.SEVERE, "Player is not in any region");
            return null;
        }

        // Select fish from region with luck
        double luck = properties.getTotalLuck();
        Random random = new Random();

        Set<Rarity> rarities = region.getAvailableRarities();
        if (rarities.isEmpty()) {
            Bukkit.getLogger().log(Level.SEVERE, "No rarities available in region " + region.getId());
            return null;
        }

        Rarity pickedRarity = WeightedRandom.pick(rarities.stream().toList(), Rarity::getWeight, luck, random);

        return WeightedRandom.pick(region.getFishOfRarity(pickedRarity), Fish::getRandomWeight, luck, random);
    }

}
