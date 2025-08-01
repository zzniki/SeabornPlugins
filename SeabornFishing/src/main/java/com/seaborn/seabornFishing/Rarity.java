package com.seaborn.seabornFishing;

public class Rarity {

    private String id;
    private String displayName;
    private float worthMultiplier = 0.1f;
    private float weight = 100;

    public Rarity(String id, String displayName, float worthMultiplier, float weight) {
        this.id = id;
        this.displayName = displayName;
        this.worthMultiplier = worthMultiplier;
        this.weight = weight;
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

    public float getWorthMultiplier() {
        return worthMultiplier;
    }

    public void setWorthMultiplier(float worthMultiplier) {
        this.worthMultiplier = worthMultiplier;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}
