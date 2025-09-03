package com.seabornmc.seabornCosmetics.cosmetics.items;

import com.seabornmc.seabornCosmetics.Utils;
import com.seabornmc.seabornCosmetics.cosmetics.Hat;
import de.tr7zw.nbtapi.NBT;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class HatItem {

    private Hat hat;

    public HatItem(Hat hat) {
        this.hat = hat;
    }

    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(Material.CARVED_PUMPKIN);

        item.editMeta(meta -> {
            meta.displayName(Utils.formatText("<white>" + this.hat.getDisplayName()).decoration(TextDecoration.ITALIC, false));
            meta.lore(getLore());
            meta.setItemModel(this.hat.getItemModel());
        });

        NBT.modify(item, nbt -> {
            nbt.setString("sb_item_type", "hat");
            nbt.setString("hat_type", this.hat.getId());
        });

        return item;
    }

    public List<TextComponent> getLore() {
        List<TextComponent> lore = new ArrayList<>();

        if (this.hat.getShortDescription() != null && !this.hat.getShortDescription().isEmpty()) {
            lore.add((TextComponent) Utils.formatText("<gray>" + this.hat.getShortDescription()).decoration(TextDecoration.ITALIC, false));
        }

        return lore;
    }

    public Hat getHat() {
        return hat;
    }

    public void setHat(Hat hat) {
        this.hat = hat;
    }
}