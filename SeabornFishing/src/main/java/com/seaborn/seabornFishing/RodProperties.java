package com.seaborn.seabornFishing;

import de.tr7zw.nbtapi.NBT;
import org.bukkit.inventory.ItemStack;

public class RodProperties {

    private CustomRod baseRod;
    private float luckMultiplier;
    private float difficulty;

    public RodProperties(CustomRod baseRod) {
        this.baseRod = baseRod;
    }

    public void loadItemStack(ItemStack item) {
        NBT.get(item, nbt -> {
            this.luckMultiplier = nbt.getFloat("luck");
            this.difficulty = nbt.getFloat("difficulty");
        });
    }

    public CustomRod getBaseRod() {
        return baseRod;
    }

    public void setBaseRod(CustomRod baseRod) {
        this.baseRod = baseRod;
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

    public float getBaseLuck() { return this.getBaseRod().getLuckMultiplier(); }
    public float getBaseDifficulty() { return this.getBaseRod().getDifficulty(); }

    public float getTotalLuck() { return this.getBaseLuck() * this.getLuckMultiplier(); }
    public float getTotalDifficulty() { return this.getBaseDifficulty() * this.getDifficulty(); }

}
