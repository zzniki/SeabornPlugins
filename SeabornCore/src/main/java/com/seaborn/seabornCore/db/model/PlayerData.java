package com.seaborn.seabornCore.db.model;

import com.seaborn.seabornCore.SeabornCore;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

public class PlayerData {

    private UUID uuid;
    private PlayerInventory inventory;
    private Location lastLocation;
    private double money;
    private double reputation;

    public PlayerData(Player player) {
        this.loadFromPlayer(player);
    }

    public void loadFromPlayer(Player player) {
        this.uuid = player.getUniqueId();
        this.inventory = player.getInventory();
        this.lastLocation = player.getLocation();
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
}
