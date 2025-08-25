package com.seaborn.seabornCore.db.model;

import com.seaborn.seabornCore.SeabornCore;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

public class PlayerData implements Cloneable {

    private UUID uuid;
    private PlayerInventory inventory;
    private Location lastLocation;
    private double money;
    private double reputation;
    private String lastWorldName;

    public PlayerData(Player player) {
        this.loadFromPlayer(player);
    }

    public void loadFromPlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.inventory = player.getInventory();
        this.lastLocation = player.getLocation();
        this.lastWorldName = player.getLocation().getWorld().getName();
        this.money = SeabornCore.getEconomy().getBalance(player);
        this.reputation = 0;
    }


    public PlayerData(UUID uuid) {
        this.uuid = uuid;
    }

    public Location getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(Location lastLocation) {
        this.lastLocation = lastLocation;
    }

    public PlayerInventory getInventory() {
        return inventory;
    }

    public void setInventory(PlayerInventory inventory) {
        this.inventory = inventory;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public double getReputation() {
        return reputation;
    }

    public void setReputation(double reputation) {
        this.reputation = reputation;
    }

    public String getLastWorldName() {
        return lastWorldName;
    }

    public void setLastWorldName(String lastWorldName) {
        this.lastWorldName = lastWorldName;
    }

    @Override
    public PlayerData clone() {
        try {
            PlayerData clone = (PlayerData) super.clone();

            // Deep copy mutable objects
            clone.uuid = this.uuid; // UUID is immutable, safe to copy directly

            if (this.inventory != null) {
                // PlayerInventory has no public copy constructor. You’ll likely need to clone its contents manually.
                // One approach is to serialize and re-apply contents into a new inventory object.
                // For now, we'll just reference it to avoid breakage, but note this is shallow.
                clone.inventory = this.inventory;
            }

            if (this.lastLocation != null) {
                clone.lastLocation = this.lastLocation.clone(); // Bukkit Location supports clone()
            }

            if (this.lastWorldName != null) {
                clone.lastWorldName = this.lastWorldName;
            }

            clone.money = this.money;
            clone.reputation = this.reputation;

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError("Cloning PlayerData failed", e);
        }
    }
}
