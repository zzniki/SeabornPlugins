package com.seaborn.seabornFishing;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Fish {

    private String id;
    private String displayName;
    private String description;
    private String shortDescription;
    private float minWeight;
    private float maxWeight;
    private float worthMultiplier = 1;
    private NamespacedKey itemModel;
    private Rarity rarity;
    private float randomWeight = 1f;

    public Fish(Rarity rarity, String id, String displayName, float minWeight, float maxWeight, NamespacedKey itemModel) {
        this.rarity = rarity;
        this.id = id;
        this.displayName = displayName;
        this.minWeight = minWeight;
        this.maxWeight = maxWeight;
        this.itemModel = itemModel;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getMinWeight() {
        return minWeight;
    }

    public void setMinWeight(float minWeight) {
        this.minWeight = minWeight;
    }

    public float getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(float maxWeight) {
        this.maxWeight = maxWeight;
    }

    public float getWorthMultiplier() {
        return worthMultiplier;
    }

    public void setWorthMultiplier(float worthMultiplier) {
        this.worthMultiplier = worthMultiplier;
    }

    public NamespacedKey getItemModel() {
        return itemModel;
    }

    public void setItemModel(NamespacedKey itemModel) {
        this.itemModel = itemModel;
    }

    public Rarity getRarity() {
        return rarity;
    }

    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public float getRandomWeight() {
        return randomWeight;
    }

    public void setRandomWeight(float randomWeight) {
        this.randomWeight = randomWeight;
    }

    public List<TextComponent> getLore(float weight, Player player) {
        List<TextComponent> lore = new ArrayList<>();
        lore.add(Component.text(ChatColor.WHITE + Utils.formatText(this.shortDescription)));
        lore.add(Component.text(""));
        lore.add(Component.text(ChatColor.WHITE + "Caught by " + player.getName()));
        lore.add(Component.text(ChatColor.WHITE + "Weights " + String.valueOf(Math.round(weight * 100f) / 100f) + "kg"));
        lore.add(Component.text(""));
        lore.add(Component.text(ChatColor.WHITE + Utils.formatText(this.getRarity().getDisplayName())));
        return lore;
    }

}
