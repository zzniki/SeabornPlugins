package com.seabornmc.seabornWeapons.weapons;

import org.bukkit.NamespacedKey;

import java.util.List;

public class Sword {

    private String id;
    private String displayName;
    private List<String> description;
    private String shortDescription;
    private float damage;
    private float price;
    private float minRep;
    private NamespacedKey itemModel;

    public Sword(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public float getMinRep() {
        return minRep;
    }

    public void setMinRep(float minRep) {
        this.minRep = minRep;
    }

    public NamespacedKey getItemModel() {
        return itemModel;
    }

    public void setItemModel(NamespacedKey itemModel) {
        this.itemModel = itemModel;
    }
}
