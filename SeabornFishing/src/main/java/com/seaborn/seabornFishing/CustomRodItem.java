package com.seaborn.seabornFishing;

import de.tr7zw.nbtapi.NBT;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class CustomRodItem {

    private CustomRod baseRod;
    private float luckMultiplier;
    private float difficulty;

    public CustomRodItem(CustomRod baseRod) {
        this.baseRod = baseRod;
        this.luckMultiplier = 1f;
        this.difficulty = 1f;
    }

    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(Material.FISHING_ROD);

        item.editMeta(meta -> {
            // TODO: Lore
            //meta.lore(String.valueOf(this.baseRod.getLuckMultiplier()));
            meta.displayName(Component.text(Utils.formatText(this.getBaseRod().getDisplayName())));
            meta.setItemModel(this.baseRod.getItemModel());
        });

        NBT.modify(item, nbt -> {
            nbt.setString("sb_item_type", "rod");
            nbt.setString("rod_type", this.baseRod.getId());
            nbt.setFloat("luck", this.getLuckMultiplier());
            nbt.setFloat("difficulty", this.getDifficulty());
        });

        return item;
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
