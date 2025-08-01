package com.seaborn.seabornFishing;

import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class FishSelector {

    public static Fish selectFish(FishHook hook, Player player, ItemStack rod) {

        RodProperties properties = Utils.getRodProperties(rod);


        // Find region
        // Select fish from region with luck
    }

}
