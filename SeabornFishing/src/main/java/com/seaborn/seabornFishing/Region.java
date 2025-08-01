package com.seaborn.seabornFishing;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class Region {

    private String id;
    private String displayName;
    private String worldguardId;
    private Set<Fish> availableFish = new HashSet<>();
    private int priority = 0;

    public Region(String id, String displayName, String worldguardId) {
        this.id = id;
        this.displayName = displayName;
        this.worldguardId = worldguardId;
    }

    public void addFish(Fish fish) {
        this.availableFish.add(fish);
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

    public String getWorldguardId() {
        return worldguardId;
    }

    public void setWorldguardId(String worldguardId) {
        this.worldguardId = worldguardId;
    }

    public Set<Fish> getAvailableFish() {
        return availableFish;
    }
    public Set<Rarity> getAvailableRarities() {
        Set<Rarity> result = new HashSet<Rarity>();

        for (Fish fish : this.getAvailableFish()) {
            result.add(fish.getRarity());
        }

        return result;

    }

    public List<Fish> getFishOfRarity(Rarity rarity) {
        return this.getAvailableFish()
                .stream()
                .filter(f -> f.getRarity().getId().equals(rarity.getId()))
                .toList();
    }

    public void setAvailableFish(Set<Fish> availableFish) {
        this.availableFish = availableFish;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
