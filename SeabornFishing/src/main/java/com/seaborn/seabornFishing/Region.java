package com.seaborn.seabornFishing;

import java.util.List;

public class Region {

    private String id;
    private String displayName;
    private String worldguardId;
    private List<Fish> availableFish;

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

    public List<Fish> getAvailableFish() {
        return availableFish;
    }

    public void setAvailableFish(List<Fish> availableFish) {
        this.availableFish = availableFish;
    }
}
