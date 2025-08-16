package com.seabornmc.seabornWeapons.weapons.items;

import com.seabornmc.seabornWeapons.Utils;
import com.seabornmc.seabornWeapons.weapons.Sword;
import de.tr7zw.nbtapi.NBT;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import javax.naming.Name;
import java.util.ArrayList;
import java.util.List;

import static com.seabornmc.seabornWeapons.Utils.formatText;

public class SwordItem {

    private Sword sword;          // Base sword data
    private float extraDamage;    // Upgradeable damage (specific to this item instance)

    public SwordItem(Sword sword) {
        this.sword = sword;
        this.extraDamage = 0f; // default with no upgrades
    }

    public SwordItem(ItemStack item) {

        String type = NBT.get(item, nbt -> (String) nbt.getString("sword_type"));

        this.extraDamage = NBT.get(item, nbt -> (float) nbt.getFloat("extra_damage"));
    }

    public ItemStack getItemStack() {
        ItemStack item = new ItemStack(Material.IRON_SWORD);

        item.editMeta(meta -> {
            meta.displayName(Component.text(ChatColor.AQUA + formatText(this.sword.getDisplayName())));
            meta.lore(getLore());
            meta.setItemModel(this.sword.getItemModel());
            meta.hasItemFlag(ItemFlag.HIDE_ATTRIBUTES);
        });

        NBT.modify(item, nbt -> {
            nbt.setString("sb_item_type", "sword");
            nbt.setString("sword_type", this.sword.getId());
            nbt.setFloat("extra_damage", this.getExtraDamage());
            nbt.setFloat("damage", this.getTotalDamage());
            nbt.setFloat("min_rep", this.sword.getMinRep());
        });

        return item;
    }

    public float getTotalDamage() {
        return this.sword.getDamage() + this.extraDamage;
    }

    public List<TextComponent> getLore() {
        List<TextComponent> lore = new ArrayList<>();

        // Long description
        if (this.sword.getDescription() != null && !this.sword.getDescription().isEmpty()) {
            for (String line : this.sword.getDescription()) {
                lore.add(Component.text(ChatColor.DARK_AQUA + formatText(line)));
            }
            lore.add(Component.text(""));
        }

        // Short description
        if (this.sword.getShortDescription() != null && !this.sword.getShortDescription().isEmpty()) {
            lore.add(Component.text(ChatColor.GRAY + formatText(this.sword.getShortDescription())));
            lore.add(Component.text(""));
        }

        // Stats
        lore.add(Component.text(ChatColor.WHITE + "Min REP: " + ChatColor.DARK_AQUA + this.sword.getMinRep()));

        lore.add(Component.text(ChatColor.WHITE + "Total Damage: " + ChatColor.DARK_AQUA + getTotalDamage()));
        lore.add(Component.text(ChatColor.WHITE + "  ↳ Base: " + ChatColor.DARK_AQUA + this.sword.getDamage()));
        lore.add(Component.text(ChatColor.WHITE + "  ↳ Bonus: " + ChatColor.DARK_AQUA + this.extraDamage));
        //lore.add(Component.text(ChatColor.WHITE + "Price: " + ChatColor.GOLD + this.sword.getPrice()));

        return lore;
    }

    public Sword getSword() {
        return sword;
    }

    public void setSword(Sword sword) {
        this.sword = sword;
    }

    public float getExtraDamage() {
        return extraDamage;
    }

    public void setExtraDamage(float extraDamage) {
        this.extraDamage = extraDamage;
    }
}
