package com.seabornmc.seabornCosmetics;

import de.tr7zw.nbtapi.NBT;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class Listeners implements Listener {

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (item.getType() != Material.CARVED_PUMPKIN) return;

        String itemType = NBT.get(item, nbt -> (String) nbt.getString("sb_item_type"));

        if (itemType != null && itemType.equals("hat")) {
            event.setCancelled(true);
        }

    }

}
