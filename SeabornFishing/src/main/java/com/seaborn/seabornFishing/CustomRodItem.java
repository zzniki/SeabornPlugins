package com.seaborn.seabornFishing;

import de.tr7zw.nbtapi.NBT;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.ArrayList;
import java.util.List;

public class CustomRodItem {

    private CustomRod baseRod;
    private float luckMultiplier;
    private float difficulty;

    public CustomRodItem(CustomRod baseRod) {
        this.baseRod = baseRod;
        this.luckMultiplier = 0f;
        this.difficulty = 0f;
    }

    public CustomRodItem(ItemStack stack) {
        String rodTypeId = NBT.get(stack, nbt -> (String) nbt.getString("rod_type"));
        if (SeabornFishing.config.getCustomRods().containsKey(rodTypeId)) {
            CustomRod baseRod = SeabornFishing.config.getCustomRod(rodTypeId);
            this.baseRod = baseRod;
            this.luckMultiplier = 0f;
            this.difficulty = 0f;
        }

        this.loadItemStack(stack);
    }

    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(Material.FISHING_ROD);

        item.editMeta(meta -> {
            meta.displayName(Component.text(ChatColor.AQUA + Utils.formatText(this.getBaseRod().getDisplayName())));
            meta.lore(this.getLore());
            meta.setItemModel(this.baseRod.getItemModel());
            ((Damageable) meta).setMaxDamage(this.baseRod.getUses());
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

    public float getTotalLuck() { return this.getBaseLuck() + this.getLuckMultiplier(); }
    public float getTotalDifficulty() { return this.getBaseDifficulty() + this.getDifficulty(); }

    public List<TextComponent> getLore() {
        List<TextComponent> res = new ArrayList<>();

        if (this.baseRod.getDescription() != null) {
            List<String> desc = List.of(this.baseRod.getDescription().split("\n"));
            for (String line : desc) {
                res.add(Component.text(ChatColor.DARK_AQUA + Utils.formatText(line)));
            }
            res.add(Component.text(""));
        }

        String luckStr = String.valueOf(this.getTotalLuck());
        String baseLuckStr = String.valueOf(this.getBaseLuck());
        String rodLuckStr = String.valueOf(this.getLuckMultiplier());

        res.add(Component.text(ChatColor.WHITE + "Luck: " + ChatColor.DARK_AQUA + luckStr + "x"));
        res.add(Component.text(ChatColor.WHITE + "  ↳ Base: " + ChatColor.DARK_AQUA + baseLuckStr + "x"));
        res.add(Component.text(ChatColor.WHITE + "  ↳ Rod: " + ChatColor.DARK_AQUA + rodLuckStr + "x"));

        String diffStr = String.valueOf(this.getTotalDifficulty());
        res.add(Component.text(""));
        res.add(Component.text(ChatColor.WHITE + "Difficulty: " + ChatColor.DARK_AQUA + diffStr + "x"));

        return res;
    }

}
