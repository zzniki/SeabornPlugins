package com.seabornmc.seabornCompass;

import io.papermc.paper.event.player.PlayerInventorySlotChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class Listeners implements Listener {

    @EventHandler
    public void onHeldItemChange(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(event.getNewSlot());

        if (item == null || item.getType() != Material.COMPASS) {
            SeabornCompass.getTrackerManager().removeTrackers(player);
            return;
        }

        SeabornCompass.getTrackerManager().addTracker(
                player,
                new Location(Bukkit.getWorld("world"), 231.5, 69.5, 145.447, -299.62f, 16.32f)
        );

    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        SeabornCompass.getTrackerManager().removeTrackers(event.getPlayer());
    }

}
