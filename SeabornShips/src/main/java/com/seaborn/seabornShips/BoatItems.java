package com.seaborn.seabornShips;

import de.tr7zw.nbtapi.NBT;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextDecoration;
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

        List<Component> lore = new ArrayList<>(boatType.getLore().stream().map(t -> "<gray>" + t).map(Utils::formatText).map(c -> c.decoration(TextDecoration.ITALIC, false)).toList());
        lore.add(Component.text(""));
        lore.add(Utils.formatText("<white>Speed: <dark_aqua>" + boatType.getSpeed() + " blocks/sec").decoration(TextDecoration.ITALIC, false));

        item.editMeta(meta -> {
            meta.customName(Utils.formatText(boatType.getName()).decoration(TextDecoration.ITALIC, false));
            meta.setItemModel(boatType.getItemModel());
            meta.lore(lore);
        });

        NBT.modify(item, nbt -> {
            nbt.setString("boat_type", boatType.getId());
        });

        return item;
    }

}
