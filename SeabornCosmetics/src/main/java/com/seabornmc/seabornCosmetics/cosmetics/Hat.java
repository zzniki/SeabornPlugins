package com.seabornmc.seabornCosmetics.cosmetics;

import org.bukkit.NamespacedKey;

public class Hat {

    private String id;
    private String displayName;
    private String shortDescription;
    private NamespacedKey itemModel;

    public Hat(String id) {
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

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public NamespacedKey getItemModel() {
        return itemModel;
    }

    public void setItemModel(NamespacedKey itemModel) {
        this.itemModel = itemModel;
    }

}