package com.seaborn.seabornFishing;

import org.bukkit.NamespacedKey;

public class CustomRod {

    private String id;
    private String displayName;
    private float luckMultiplier = 1;
    private float difficulty = 1;
    private NamespacedKey itemModel;

    public CustomRod(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() {
        return id;
    }

    public float getLuckMultiplier() {
        return luckMultiplier;
    }

    public void setLuckMultiplier(float luckMultiplier) {
        this.luckMultiplier = luckMultiplier;
    }

    public float getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(float difficulty) {
        this.difficulty = difficulty;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public NamespacedKey getItemModel() {
        return itemModel;
    }

    public void setItemModel(NamespacedKey itemModel) {
        this.itemModel = itemModel;
    }
}
