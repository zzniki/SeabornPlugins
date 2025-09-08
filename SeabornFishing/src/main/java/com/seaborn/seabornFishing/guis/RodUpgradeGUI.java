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
import xyz.xenondevs.invui.window.AbstractWindow;
import xyz.xenondevs.invui.window.Window;

import java.util.List;
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

    private double luckPrice = 0;
    private double diffPrice = 0;

    private float luckUpgrade = 0;
    private float diffUpgrade = 0;

    private LuckLabelItem luckLabelItem;
    private DifficultyLabelItem difficultyLabelItem;
    private ConfirmItem confirmItem;

    private double calcPrice(float totalUpgrade) {
        return Math.pow(totalUpgrade, 6.5) + 700 * totalUpgrade;
    }

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

        ItemStack cancelItemStack = new ItemStack(Material.PAPER);
        cancelItemStack.editMeta((meta) -> {
            meta.customName(Utils.formatTextMM("<red>Cancel").decoration(TextDecoration.ITALIC, false));
            meta.setItemModel(new NamespacedKey("seaborn_icons", "empty"));
        });

        this.luckLabelItem = new LuckLabelItem();
        this.difficultyLabelItem = new DifficultyLabelItem();
        this.confirmItem = new ConfirmItem();

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
                .addIngredient('L', this.luckLabelItem)
                .addIngredient('D', this.difficultyLabelItem)
                .addIngredient('C', this.confirmItem)
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
    private void updateWindow() {
        AbstractWindow w = ((AbstractWindow) this.currentWindow);
        w.handleItemProviderUpdate(luckLabelItem);
        w.handleItemProviderUpdate(difficultyLabelItem);
        w.handleItemProviderUpdate(confirmItem);
    }

    // === Item classes ===

    // Luck label item (clickable to increase luck)
    private class LuckLabelItem extends AbstractItem {

        @Override
        public ItemBuilder getItemProvider() {
            ItemStack item = new ItemStack(Material.PAPER);
            item.editMeta(meta -> {
                meta.customName(Utils.formatTextMM("<bold>Luck").decoration(TextDecoration.ITALIC, false));
                meta.setItemModel(new NamespacedKey("seaborn_icons", "empty"));

                String luckStr = String.valueOf(Math.round((rod.getTotalLuck() + luckUpgrade) * 100f) / 100f);
                String priceStr = String.valueOf(Math.round(calcPrice(luckUpgrade + 0.1f) * 100f) / 100f);
                String currentPriceStr = String.valueOf(Math.round(luckPrice * 100f) / 100f);

                meta.lore(java.util.List.of(
                        Utils.formatTextMM("<white>Click to add luck").decoration(TextDecoration.ITALIC, false),
                        Utils.formatTextMM("<green>Add 0.1x [" + priceStr + " ⛃]").decoration(TextDecoration.ITALIC, false),
                        Component.text(""),
                        Utils.formatTextMM("<white>Current value: <dark_aqua>" + luckStr).decoration(TextDecoration.ITALIC, false),
                        Utils.formatTextMM("<white>Current price: <dark_aqua>" + currentPriceStr).decoration(TextDecoration.ITALIC, false)
                ));
            });
            return new ItemBuilder(item);
        }

        @Override
        public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
            // increment luck by 1 on left click, by 5 on shift-left
            float delta = clickType.isShiftClick() ? 1 : 0.1f;
            luckUpgrade += delta;
            luckPrice += calcPrice(luckUpgrade);
            updateWindow();
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
                meta.lore(java.util.List.of(
                        Utils.formatTextMM("<green>Click to reduce difficulty").decoration(TextDecoration.ITALIC, false),
                        Utils.formatTextMM("<green>Substracts 0.1x").decoration(TextDecoration.ITALIC, false),
                        Utils.formatTextMM("<white>Current Value: <dark_aqua>" + diffStr).decoration(TextDecoration.ITALIC, false)
                ));
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

        @Override
        public ItemBuilder getItemProvider() {

            ItemStack confirmItemStack = new ItemStack(Material.PAPER);

            String priceStr = String.valueOf(Math.round((luckPrice + diffPrice) * 100f) / 100f);

            confirmItemStack.editMeta((meta) -> {
                meta.customName(Utils.formatTextMM("<green>Confirm").decoration(TextDecoration.ITALIC, false));
                meta.setItemModel(new NamespacedKey("seaborn_icons", "empty"));
                meta.lore(List.of(
                        Utils.formatTextMM(priceStr).decoration(TextDecoration.ITALIC, false)
                ));
            });

            return new ItemBuilder(confirmItemStack);
        }

        @Override
        public void handleClick(ClickType clickType, Player player, InventoryClickEvent event) {
            ItemStack held = player.getInventory().getItemInMainHand();

            if (held != null && held.getType() == Material.FISHING_ROD && held.equals(original)) {

                double price = luckPrice + diffPrice;
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
