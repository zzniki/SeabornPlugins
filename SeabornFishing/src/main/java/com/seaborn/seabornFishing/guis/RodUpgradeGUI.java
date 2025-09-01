package com.seaborn.seabornFishing.guis;

import com.seaborn.seabornFishing.Utils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.Window;

public class RodUpgradeGUI {

    private Gui gui;
    private Window currentWindow = null;

    private Player player;

    public RodUpgradeGUI(Player player) {
        this.player = player;

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

        gui = Gui.normal()
                .setStructure(
                        ". . . . . . . . .",
                        ". . . . . . . . .",
                        ". . . . R . . . .",
                        ". . . . . . . . .",
                        ". . . . . . . . .",
                        ". . . C C C . N ."
                )
                .addIngredient('R', new SimpleItem(new ItemBuilder(Material.FISHING_ROD)))
                .addIngredient('C', new SimpleItem(confirmItemStack))
                .addIngredient('N', new SimpleItem(cancelItemStack))
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

}
