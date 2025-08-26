package com.seaborn.seabornCrews.menus;

import com.seaborn.seabornCrews.SeabornCrews;
import com.seaborn.seabornCrews.Utils;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xyz.xenondevs.inventoryaccess.InventoryAccess;
import xyz.xenondevs.inventoryaccess.component.ComponentWrapper;
import xyz.xenondevs.invui.gui.Gui;
import xyz.xenondevs.invui.item.ItemProvider;
import xyz.xenondevs.invui.item.builder.ItemBuilder;
import xyz.xenondevs.invui.item.impl.AbstractItem;
import xyz.xenondevs.invui.item.impl.SimpleItem;
import xyz.xenondevs.invui.window.AnvilWindow;
import xyz.xenondevs.invui.window.Window;

import javax.naming.Name;
import java.sql.SQLException;
import java.util.List;

public class CrewMasterMenu {

    private Window currentWindow = null;

    private Window mainGuiWindow;
    private AnvilWindow setCrewNameWindow;
    private AnvilWindow setCrewTagWindow;

    private String crewName;
    private String crewTag;

    private Player player;

    public CrewMasterMenu(Player player) {

        this.player = player;

        ItemStack createItemStack = new ItemStack(Material.PAPER);
        createItemStack.editMeta((meta) -> {
            meta.customName(Utils.formatText("<reset><aqua>Crete a crew").decoration(TextDecoration.ITALIC, false));
            meta.setItemModel(new NamespacedKey("seaborn_icons", "add"));
        });

        // -- Create GUIs --

        Gui mainGui = Gui.normal()
                .setStructure(
                        ". . . . . . . . .",
                        ". . C . . . . . .",
                        ". . . . . . . . .")
                .addIngredient('C', new SimpleItem(createItemStack, (click) -> { this.setWindow(setCrewNameWindow); }))
                .build();

        ItemStack emptyItemStack = new ItemStack(Material.PAPER);
        emptyItemStack.editMeta((meta) -> {
            meta.customName(Component.text("").decoration(TextDecoration.ITALIC, false));
            meta.setItemModel(new NamespacedKey("seaborn_icons", "empty"));
        });


        CrewNameConfirmItem crewNameConfirmItem = new CrewNameConfirmItem();
        Gui setCrewNameGui = Gui.normal()
                .setStructure("E E C . . . . . .")
                .addIngredient('C', crewNameConfirmItem)
                .addIngredient('E', new SimpleItem(emptyItemStack))
                .build();

        CrewTagConfirmItem crewTagConfirmItem = new CrewTagConfirmItem();
        Gui setCrewTagGui = Gui.normal()
                .setStructure("E E C . . . . . .")
                .addIngredient('C', crewTagConfirmItem)
                .addIngredient('E', new SimpleItem(emptyItemStack))
                .build();


        // -- Create windows --

        this.mainGuiWindow = Window.single()
                .setGui(mainGui)
                .build(player);


        TextComponent crewNameWindowTitle = new TextComponent("-112346889\ua000");
        crewNameWindowTitle.setFont("seaborn_images:guis");
        crewNameWindowTitle.setColor(ChatColor.WHITE);

        this.setCrewNameWindow = AnvilWindow.single()
                .addRenameHandler(s -> {
                    this.crewName = s;
                    crewNameConfirmItem.checkNameAvailability();
                })
                .setGui(setCrewNameGui)
                .setTitle(new TextComponent[] { crewNameWindowTitle })
                .build(player);

        TextComponent crewTagWindowTitle = new TextComponent("-112346889\ua000");
        crewTagWindowTitle.setFont("seaborn_images:guis");
        crewTagWindowTitle.setColor(ChatColor.WHITE);

        this.setCrewTagWindow = AnvilWindow.single()
                .addRenameHandler(s -> {
                    this.crewTag = s.toUpperCase();
                    crewTagConfirmItem.checkAvailability();
                })
                .setGui(setCrewTagGui)
                .setTitle(new TextComponent[] { crewTagWindowTitle })
                .build(player);

        this.currentWindow = mainGuiWindow;

    }

    public void show() {
        this.currentWindow.open();
        this.player.playSound(Sound.sound()
                .type(Key.key("minecraft", "ui.loom.take_result"))
                .source(Sound.Source.PLAYER)
                .build()
        );
    }

    private void setWindow(Window window) {
        if (currentWindow != null && currentWindow.isOpen()) currentWindow.close();
        this.currentWindow = window;
        this.currentWindow.open();

    }

    private class CrewNameConfirmItem extends AbstractItem {

        private boolean isNameAvailable = false;
        private String reason;

        @Override
        public ItemProvider getItemProvider() {
            String displayName = isNameAvailable ? "<reset><green>Confirm" : "<reset><red>" + this.reason;

            ItemStack confirmItemStack = new ItemStack(Material.PAPER);
            confirmItemStack.editMeta((meta) -> {
                meta.customName(Utils.formatText(displayName).decoration(TextDecoration.ITALIC, false));
                meta.setItemModel(new NamespacedKey("seaborn_icons", isNameAvailable ? "accept" : "exclamation"));
            });

            return new ItemBuilder(confirmItemStack);
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {

            checkNameAvailability();

            if (isNameAvailable) {
                setWindow(setCrewTagWindow);
            }

        }

        private void checkNameAvailability() {
            try {

                if (crewName.isBlank()) {
                    this.isNameAvailable = false;
                    this.reason = "You must write a name";
                } else if (crewName.startsWith(" ")) {
                    this.isNameAvailable = false;
                    this.reason = "Cannot start with a space";
                } else if (crewName.length() > 16) {
                    this.isNameAvailable = false;
                    this.reason = "Cannot be longer than 16 characters";
                } else if (SeabornCrews.getCrewService().isCrewNameTaken(crewName)) {
                    this.isNameAvailable = false;
                    this.reason = "Already taken";
                } else this.isNameAvailable = true;

                notifyWindows();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private class CrewTagConfirmItem extends AbstractItem {

        private boolean valid = false;
        private String reason;

        @Override
        public ItemProvider getItemProvider() {
            String displayName = valid ? "<reset><green>Confirm" : "<reset><red>" + this.reason;

            ItemStack confirmItemStack = new ItemStack(Material.PAPER);
            confirmItemStack.editMeta((meta) -> {
                meta.customName(Utils.formatText(displayName).decoration(TextDecoration.ITALIC, false));
                meta.setItemModel(new NamespacedKey("seaborn_icons", valid ? "accept" : "exclamation"));
            });

            return new ItemBuilder(confirmItemStack);
        }

        @Override
        public void handleClick(@NotNull ClickType clickType, @NotNull Player player, @NotNull InventoryClickEvent event) {

            checkAvailability();
            if (this.valid) {
                createCrew();
            }

        }

        private void checkAvailability() {
            try {

                if (crewTag.isBlank()) {
                    this.valid = false;
                    this.reason = "You must write a tag";
                } else if (crewTag.contains(" ")) {
                    this.valid = false;
                    this.reason = "Cannot contain spaces";
                } else if (crewTag.length() > 8) {
                    this.valid = false;
                    this.reason = "Cannot be more than 8 characters long";
                } else if (SeabornCrews.getCrewService().isCrewTagTaken(crewTag)) {
                    this.valid = false;
                    this.reason = "Already taken";
                } else this.valid = true;

                notifyWindows();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void createCrew() {
        player.showTitle(Title.title(
                Component.text(""),
                Component.text("Creating crew...")
        ));
        this.currentWindow.close();
        SeabornCrews.getCrewManager().createCrew(this.player, this.crewName, this.crewTag);
    }



}
