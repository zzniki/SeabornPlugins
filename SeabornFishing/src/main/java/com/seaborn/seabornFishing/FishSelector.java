package com.seaborn.seabornFishing;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getServer;

public class FishSelector {

    public static Fish selectFish(FishHook hook, Player player, ItemStack rod) {

        RodProperties properties = Utils.getRodProperties(rod);

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

        Rarity pickedRarity = WeightedRandom.pick(region.getAvailableRarities().stream().toList(), Rarity::getWeight, luck, random);

        return WeightedRandom.pick(region.getFishOfRarity(pickedRarity), Fish::getRandomWeight, luck, random);
    }

}
