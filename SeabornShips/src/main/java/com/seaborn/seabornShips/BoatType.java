package com.seaborn.seabornShips;

import org.bukkit.NamespacedKey;

import java.util.List;

public class BoatType {

    private String id;
    private String name;
    private double speed; // Blocks per second
    private NamespacedKey entityModel;
    private NamespacedKey itemModel;
    private List<String> lore;

    public BoatType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public NamespacedKey getEntityModel() {
        return entityModel;
    }

    public void setEntityModel(NamespacedKey itemModel) {
        this.entityModel = itemModel;
    }

    public NamespacedKey getItemModel() {
        return itemModel;
    }

    public void setItemModel(NamespacedKey itemModel) {
        this.itemModel = itemModel;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore.stream().map(e -> e.replaceAll("&", "§")).toList();
    }
}
