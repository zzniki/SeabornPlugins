package com.seaborn.seabornShips;

import de.tr7zw.nbtapi.NBT;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BoatItems {

    public static ItemStack getBoatItemFromEntity(SeabornShips plugin, Entity boat) {
        return getItemFromType(BoatUtils.getBoatType(plugin, boat));
    }

    public static ItemStack getItemFromType(BoatType boatType) {
        ItemStack item = new ItemStack(Material.BAMBOO_RAFT);

        List<TextComponent> lore = new ArrayList<>(boatType.getLore().stream().map(Component::text).toList());
        lore.add(Component.text(""));
        lore.add(Component.text(ChatColor.WHITE + "Speed: " + ChatColor.DARK_AQUA + boatType.getSpeed() + " blocks/sec"));

        item.editMeta(meta -> {
            meta.customName(Component.text(boatType.getName().replaceAll("&", "§")));
            meta.setItemModel(boatType.getEntityModel());
            meta.lore(lore);
        });

        NBT.modify(item, nbt -> {
            nbt.setString("boat_type", boatType.getId());
        });

        return item;
    }

}
