package com.seaborn.seabornFishing;

public class CustomRod {

    private String id;
    private float luckMultiplier;
    private float difficulty;

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
}
