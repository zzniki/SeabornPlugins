package com.seabornmc.seabornWeapons;

import de.tr7zw.nbtapi.NBT;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class Listeners implements Listener {

    @EventHandler
    public void entityDamageEntity(EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof Player)) return;

        Player damager = (Player) event.getDamager();

        ItemStack item = damager.getInventory().getItemInMainHand();
        if (item.getType() == Material.IRON_SWORD) {
            Float damage = NBT.get(item, nbt -> (Float) nbt.getFloat("damage"));
            if (damage != null && !damage.isNaN() && !damage.isInfinite()) {
                event.setDamage(damage);
            }
        }
    }

}
