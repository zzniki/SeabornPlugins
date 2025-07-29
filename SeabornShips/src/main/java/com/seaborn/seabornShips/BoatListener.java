package com.seaborn.seabornShips;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import de.tr7zw.nbtapi.NBT;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.entity.boat.OakBoat;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityPlaceEvent;
import org.bukkit.event.player.PlayerInputEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Bukkit.getServer;

public class BoatListener implements Listener {

    private Map<String, Vehicle> spawnedBoats = new HashMap<String, Vehicle>();
    private Map<Player, Float> driveStatus = new HashMap<Player, Float>();

    private SeabornShips plugin;

    public BoatListener(SeabornShips plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityPlace(EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof Boat)) return;

        Entity boatEntity = event.getEntity();

        if (event.getPlayer() == null) return;

        Player player = event.getPlayer();
        ItemStack boatItem = player.getInventory().getItemInMainHand();
        if (boatItem.getType() != Material.BAMBOO_RAFT) {
            player.sendMessage(ChatColor.RED + "You most hold a boat in the main hand to place it!");
            event.setCancelled(true);
            return;
        }

        if (this.spawnedBoats.containsKey(player.getUniqueId().toString())) {
            Vehicle target = this.spawnedBoats.get(player.getUniqueId().toString());
            BoatUtils.deleteBoat(target);
            ItemStack item = BoatItems.getBoatItemFromEntity(plugin, target);
            player.getInventory().addItem(item);
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1f);
        }

        String boatType = NBT.get(boatItem, nbt -> (String) nbt.getString("boat_type"));

        NBT.modifyPersistentData(boatEntity, nbt -> {
            nbt.setString("boat_type", boatType);
            nbt.setString("owner", player.getUniqueId().toString());
        });
        boatEntity.setCustomName(player.getName() + "'s boat");

        // Model
        ItemStack modelItem = new ItemStack(Material.BAMBOO_RAFT);
        modelItem.editMeta(meta -> meta.setItemModel(
                BoatUtils.getEntityModel(boatType)
        ));

        player.getWorld().spawn(boatEntity.getLocation(), Villager.class, (display) -> {
            //display.setItemStack(modelItem);
            display.setInvulnerable(true);
            display.setInvisible(true);
            display.setAI(false);
            display.setSilent(true);
            display.getEquipment().setItemInMainHand(modelItem);
            display.getEquipment().setItemInOffHand(modelItem);
            display.setCustomNameVisible(false);
            display.setCustomName("___BoatDisplay");
            boatEntity.addPassenger(display);
        });

        spawnedBoats.put(player.getUniqueId().toString(), (Vehicle) boatEntity);

    }

    @EventHandler
    public void onVehicleMove(VehicleMoveEvent event) {
        if (event.getVehicle().getType() != EntityType.BAMBOO_RAFT) return;

        Vehicle vehicle = event.getVehicle();
        for (Entity passenger : vehicle.getPassengers()) {
            if (passenger instanceof Player) {
                Player player = (Player) passenger;
                double forwardSpeed = 0f;

                if (!driveStatus.containsKey(player)) continue;

                BoatType boatType = BoatUtils.getBoatType(plugin, vehicle);

                if (driveStatus.get(player) > 0) {
                    forwardSpeed = boatType.getSpeed();
                } else if (driveStatus.get(player) < 0) {
                    forwardSpeed = -boatType.getSpeed();
                }

                if (forwardSpeed != 0f) {
                    double tps = getServer().getTPS()[0];
                    Vector speed = vehicle.getLocation().getDirection().multiply(forwardSpeed / tps);
                    vehicle.setVelocity(speed);
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getEntity().getCustomName().equals("___BoatDisplay")) event.setCancelled(true);
    }

    @EventHandler
    public void onVehicleEnter(VehicleEnterEvent event) {
        if (event.getVehicle().getType() != EntityType.BAMBOO_RAFT) return;
        if (!(event.getEntered() instanceof Player)) return;

        Player player = (Player) event.getEntered();
        Vehicle vehicle = event.getVehicle();

        if (!BoatUtils.isOwner(player, vehicle)) {
            player.sendMessage(ChatColor.RED + "You cannot board boats that aren't yours.");
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onVehicleDestroy(VehicleDestroyEvent event) {
        if (event.getVehicle().getType() != EntityType.BAMBOO_RAFT) return;
        if (!(event.getAttacker() instanceof Player)) return;

        Player player = (Player) event.getAttacker();
        Vehicle vehicle = event.getVehicle();

        if (!BoatUtils.isOwner(player, vehicle)) {
            player.sendMessage("You cannot destroy other players' boats!");
            event.setCancelled(true);
            return;
        } else { // Player is owner
            event.setCancelled(true);

            ItemStack item = BoatItems.getBoatItemFromEntity(plugin, vehicle);
            player.getInventory().addItem(item);
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1f);

            BoatUtils.deleteBoat(vehicle);
            spawnedBoats.remove(player.getUniqueId().toString());
            driveStatus.remove(player);

        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        String uuid = event.getPlayer().getUniqueId().toString();
        Player player = event.getPlayer();
        if (spawnedBoats.containsKey(uuid)) {

            Vehicle boat = spawnedBoats.get(uuid);

            ItemStack item = BoatItems.getBoatItemFromEntity(plugin, boat);
            player.getInventory().addItem(item);

            BoatUtils.deleteBoat(boat);
            spawnedBoats.remove(uuid);
            driveStatus.remove(player);

        }
    }

    @EventHandler
    public void onPlayerInput(PlayerInputEvent event) {

        if (event.getPlayer().getVehicle() == null) return;
        if (!spawnedBoats.containsKey(event.getPlayer().getUniqueId().toString())) return;

        Input input = event.getInput();

        this.driveStatus.put(event.getPlayer(), input.isForward() ? 1f : input.isBackward() ? -1f : 0f);

    }

}