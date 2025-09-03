package com.seaborn.seabornFishing.guis;

import com.seaborn.seabornFishing.CustomRod;
import com.seaborn.seabornFishing.CustomRodItem;
import com.seaborn.seabornFishing.SeabornFishing;
import com.seaborn.seabornFishing.Utils;
import de.tr7zw.nbtapi.NBT;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

import java.util.Objects;

/**
 * GUI to update a rod's NBT values for "luck" and "difficulty".
 *
 * - A : add more luck
 * - T : remove difficulty
 * - L : static empty item that shows current luck
 * - D : static empty item that shows current difficulty
 * - C : confirm (applies the values to the player's held rod / gives the modified rod)
 * - N : exit (closes the window)
 *
 * This class follows the style used in the example provided by the user.
 */
public class RodUpgradeGUI {

    private Gui gui;
    private Window currentWindow = null;

    private Player player;

    // the ItemStack that represents the rod inside the GUI (we modify this in-memory and can apply it)
    private ItemStack original;
    private CustomRodItem rod;

    private float price = 0;

    // NamespacedKeys for persistent data
    private final NamespacedKey luckKey = new NamespacedKey("seaborn_fishing", "luck");
    private final NamespacedKey difficultyKey = new NamespacedKey("seaborn_fishing", "difficulty");

    public RodUpgradeGUI(Player player) {
        this.player = player;

        // start with a fresh fishing rod (you may want to copy the player's held rod instead)
        original = player.getInventory().getItemInMainHand();
        this.rod = new CustomRodItem(original);

        // display items
        ItemStack addLuckItemStack = new ItemStack(Material.PAPER);
        addLuckItemStack.editMeta((meta) -> {
            meta.customName(Utils.formatTextMM("<green>+ Luck").decoration(TextDecoration.ITALIC, false));
            meta.setItemModel(new NamespacedKey("seaborn_icons", "expand"));
        });

        ItemStack removeDifficultyItemStack = new ItemStack(Material.PAPER);
        removeDifficultyItemStack.editMeta((meta) -> {
            meta.customName(Utils.formatTextMM("<gold>- Difficulty").decoration(TextDecoration.ITALIC, false));
            meta.setItemModel(new NamespacedKey("seaborn_icons", "collapse"));
        });

        ItemStack emptyItemStack = new ItemStack(Material.PAPER);
        emptyItemStack.editMeta((meta) -> {
            meta.customName(Utils.formatTextMM("<reset>").decoration(TextDecoration.ITALIC, false));
            meta.setItemModel(new NamespacedKey("seaborn_icons", "empty"));
        });

        ItemStack confirmItemStack = new ItemStack(Material.PAPER);
        confirmItemStack.editMeta((meta) -> {
            meta.customName(Utils.formatTextMM("<green>Confirm").decoration(TextDecoration.ITALIC, false));
            meta.setItemModel(new NamespacedKey("seaborn_icons", "empty"));
        });

        ItemStack cancelItemStack = new ItemStack(Material.PAPER);
        cancelItemStack.editMeta((meta) -> {
            meta.customName(Utils.formatTextMM("<red>Cancel").decoration(TextDecoration.ITALIC, false));
            meta.setItemModel(new NamespacedKey("seaborn_icons", "empty"));
        });

        // Build GUI structure (matches user's layout)
        gui = Gui.normal()
                .setStructure(
                        ". . . . . . . . .",
                        ". . . . . . . . .",
                        ". . . . R . . . .",
                        ". L . . . . . D .",
                        ". . . . . . . . .",
                        ". . . C C C . N ."
                )
                .addIngredient('R', new SimpleItem(this.rod.getItemStack()))
                .addIngredient('L', new LuckLabelItem())
                .addIngredient('D', new DifficultyLabelItem())
                .addIngredient('C', new ConfirmItem(confirmItemStack))
                .addIngredient('N', new ExitItem(cancelItemStack))
                .build();

        TextComponent windowTitle = new TextComponent("-112345689\ua001");
        windowTitle.setFont("seaborn_images:guis");
        windowTitle.setColor(ChatColor.WHITE);

        currentWindow = Window.single()
                .setGui(gui)
                .setTitle(new TextComponent[] { windowTitle })
                .build(player);

    }

    public void show() {
        this.currentWindow.open();
    }

    // === Item classes ===

    // Luck label item (clickable to increase luck)
    private class LuckLabelItem extends AbstractItem {

        @Override
        public ItemBuilder getItemProvider() {
            ItemStack item = new ItemStack(Material.PAPER);
            item.editMeta(meta -> {
                meta.customName(Utils.formatTextMM("<yellow>Luck").decoration(TextDecoration.ITALIC, false));
                meta.setItemModel(new NamespacedKey("seaborn_icons", "empty"));
                float luck = rod.getTotalLuck();
                String luckStr = String.valueOf(Math.round(luck * 100f) / 100f);
                meta.lore(java.util.List.of(Component.text("Click to add luck"), Component.text("Value: " + luckStr)));
            });
            return new ItemBuilder(item);
        }

        @Override
        public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
            // increment luck by 1 on left click, by 5 on shift-left
            int delta = clickType.isShiftClick() ? 5 : 1;
            float newLuck = rod.getLuckMultiplier() + delta;
            rod.setLuckMultiplier(newLuck);
            notifyWindows(); // refresh this slot
        }
    }

    // Difficulty label item (clickable to decrease difficulty)
    private class DifficultyLabelItem extends AbstractItem {

        @Override
        public ItemBuilder getItemProvider() {
            ItemStack item = new ItemStack(Material.PAPER);
            item.editMeta(meta -> {
                meta.customName(Utils.formatTextMM("<yellow>Difficulty").decoration(TextDecoration.ITALIC, false));
                meta.setItemModel(new NamespacedKey("seaborn_icons", "empty"));
                float diff = rod.getTotalDifficulty();
                String diffStr = String.valueOf(Math.round(diff * 100f) / 100f);
                meta.lore(java.util.List.of(Component.text("Click to reduce difficulty"), Component.text("Value: " + diffStr)));
            });
            return new ItemBuilder(item);
        }

        @Override
        public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
            // decrement difficulty by 1 on left click, by 3 on shift-left
            float delta = clickType.isShiftClick() ? 3 : 1;
            float current = rod.getDifficulty();
            float newDiff = Math.max(0f, current - delta);
            rod.setDifficulty(newDiff);
            notifyWindows(); // refresh this slot
        }
    }

    private class ConfirmItem extends AbstractItem {
        private final ItemStack template;

        ConfirmItem(ItemStack template) {
            this.template = template;
        }

        @Override
        public ItemBuilder getItemProvider() {
            return new ItemBuilder(template);
        }

        @Override
        public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
            ItemStack held = player.getInventory().getItemInMainHand();

            if (held != null && held.getType() == Material.FISHING_ROD && held.equals(original)) {

                double balance = SeabornFishing.getEconomy().getBalance(player);
                if (balance >= price) {
                    if (SeabornFishing.getEconomy().withdrawPlayer(player, price).transactionSuccess()) {
                        player.getInventory().setItemInMainHand(rod.getItemStack());
                        player.showTitle(Title.title(Component.text(""), Utils.formatTextMM("<green>Rod upgraded!")));
                    } else {
                        player.showTitle(Title.title(Component.text(""), Utils.formatTextMM("<red>Error")));
                    }
                } else {
                    player.showTitle(Title.title(Component.text(""), Utils.formatTextMM("<red>You don't have enough money")));
                }

            } else {
                player.showTitle(Title.title(Component.text(""), Utils.formatTextMM("<red>You must hold your rod")));
            }

            // give visual feedback and close the window
            if (currentWindow != null && currentWindow.isOpen()) currentWindow.close();
        }
    }

    private class ExitItem extends AbstractItem {
        private final ItemStack template;

        ExitItem(ItemStack template) {
            this.template = template;
        }

        @Override
        public ItemBuilder getItemProvider() {
            return new ItemBuilder(template);
        }

        @Override
        public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
            if (currentWindow != null && currentWindow.isOpen()) currentWindow.close();
        }
    }


}
