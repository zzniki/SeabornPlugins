package com.seaborn.seabornFishing.rewards;

import com.seaborn.seabornFishing.Fish;
import com.seaborn.seabornFishing.Rarity;
import com.seaborn.seabornFishing.SeabornFishing;
import com.seaborn.seabornFishing.Utils;
import de.tr7zw.nbtapi.NBT;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class FishItemReward extends FishReward {


    private static Random random = null;
    private ItemStack item;
    private Fish fish;
    private float weight;

    public FishItemReward(Fish fish, Player player) {

        this.fish = fish;

        if (random == null) random = new Random();

        float weight = random.nextFloat(fish.getMinWeight(), fish.getMaxWeight());

        this.weight = weight;

        this.item = new ItemStack(Material.COD);
        this.item.editMeta(meta -> {
            if (fish.getItemModel() != null) meta.setItemModel(fish.getItemModel());
            meta.lore(fish.getLore(weight, player));
            meta.displayName(Component.text(ChatColor.AQUA + Utils.formatText(fish.getDisplayName())));
        });

        NBT.modify(this.item, nbt -> {
            nbt.setString("sb_item_type", "fish");
            nbt.setFloat("weight", weight);
            nbt.setString("fish_type", fish.getId());
            nbt.setString("caught_by", player.getName());
            nbt.setString("rarity", fish.getRarity().getId());
        });
    }

    public FishItemReward(ItemStack item) {
        this.item = item.clone();

        NBT.get(item, nbt -> {
            if (nbt == null) return;

            // Read stored values
            String type = nbt.getString("sb_item_type");
            if (!"fish".equalsIgnoreCase(type)) {
                throw new IllegalArgumentException("ItemStack is not a fish item");
            }

            this.weight = nbt.getFloat("weight");
            String fishId = nbt.getString("fish_type");

            // Retrieve fish from your registry
            this.fish = SeabornFishing.config.getFish().get(fishId);
        });
    }

    @Override
    public void giveToPlayer(Player player) {
        player.getInventory().addItem(item);
    }

    public ItemStack getItem() {
        return this.item;
    }

    public float getWorth() {
        return this.weight * this.fish.getRarity().getWorthMultiplier() * this.fish.getWorthMultiplier();
    }

}
