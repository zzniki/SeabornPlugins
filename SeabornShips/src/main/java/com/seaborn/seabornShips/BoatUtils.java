package com.seaborn.seabornShips;

import de.tr7zw.nbtapi.NBT;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.entity.Villager;

public class BoatUtils {

    public static boolean isOwner(Player player, Entity boat) {
        String ownerUUID = NBT.getPersistentData(boat, nbt -> (String) nbt.getString("owner"));
        return ownerUUID.equals(player.getUniqueId().toString());
    }

    public static BoatType getBoatType(SeabornShips plugin, Entity boat) {
        String type = NBT.getPersistentData(boat, nbt -> (String) nbt.getString("boat_type"));
        return plugin.boatTypes.get(type);
    }

    public static NamespacedKey getEntityModel(String boatType) {
        return SeabornShips.boatTypes.get(boatType).getEntityModel();
    }

    public static void deleteBoat(Vehicle vehicle) {
        for (Entity passenger : vehicle.getPassengers()) {
            if (passenger instanceof Villager && passenger.getCustomName().equals("___BoatDisplay")) {
                passenger.remove();
            }
        }
        vehicle.remove();
    }

}
