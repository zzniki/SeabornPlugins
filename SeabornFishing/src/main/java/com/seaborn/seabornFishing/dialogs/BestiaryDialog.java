package com.seaborn.seabornFishing.dialogs;

import com.seaborn.seabornFishing.Fish;
import com.seaborn.seabornFishing.Region;
import com.seaborn.seabornFishing.SeabornFishing;
import com.seaborn.seabornFishing.Utils;
import com.seabornmc.seabornDialogs.Display;
import com.seabornmc.seabornDialogs.SeabornDialogs;
import com.seabornmc.seabornDialogs.dialogs.Dialog;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class BestiaryDialog extends Dialog {

    private final int rowSize = 4;
    private final double fishSize = 0.2;
    private final double spacing = 0.1;

    private Fish selectedFish = SeabornFishing.config.getFish().get("tuna");
    private Region selectedRegion;

    public BestiaryDialog(Player player) {
        super(null, player);

        Location loc = player.getEyeLocation()
                .add(player.getEyeLocation().getDirection().normalize().multiply(2))
                .addRotation(180, -player.getEyeLocation().getPitch())
                .add(new Vector(0, -.1, 0));

        this.setLocation(loc);
    }

    public void cleanup() {

        for (Display d : this.getDisplays()) {
            SeabornDialogs.getDisplayManager().deleteDisplay(d);
        }

        this.getDisplays().clear();

    }

    public void update() {

        this.cleanup();
        this.spawnUpdate();

    }

    private String addSpaces(String string,int spaces){
        StringBuilder stringBuilder = new StringBuilder(string);
        for (int i = 0; i < spaces; i++) {
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    public void spawnUpdate() {

        Vector forward = this.getLocation().getDirection().clone().normalize();
        Vector right = forward.clone().rotateAroundAxis(new Vector(0, 1, 0), Math.toRadians(90)).normalize();
        Vector down = right.clone().crossProduct(forward).normalize();

        int itemCount = 10;
        int totalCols = rowSize;
        int totalRows = (itemCount + rowSize - 1) / rowSize;

        double halfWidth = (totalCols - 1) * fishSize / 2.0;
        double halfHeight = (totalRows - 1) * fishSize / 2.0;

        // === Spawn title ===

        Location titleLoc = this.getLocation().clone().add(down.clone().multiply(-1));
        Display titleDisplay = SeabornDialogs.getDisplayManager().createDisplay(
                titleLoc,
                Component.text("\ua003").font(Key.key("seaborn_images", "images"))
        );
        titleDisplay.setInteractable(false);
        titleDisplay.setDialog(this);
        titleDisplay.setScale(.5f);

        this.getDisplays().add(titleDisplay);
        SeabornDialogs.getDisplayManager().showDisplayToPlayer(titleDisplay, this.getPlayer());
        titleDisplay.getEntity().setBackgroundColor(Color.fromARGB(0, 0, 0, 0));

        // === Spawn background ===

        Location bgLoc = this.getLocation().clone()
                .add(forward.clone().multiply(-.01))
                .add(down.clone().multiply(-.1));

        Display bgDisplay = SeabornDialogs.getDisplayManager().createDisplay(
                bgLoc,
                Component.text("\ua002").font(Key.key("seaborn_images", "images"))
        );
        bgDisplay.setInteractable(false);
        bgDisplay.setDialog(this);
        bgDisplay.setScale(.85f);

        this.getDisplays().add(bgDisplay);
        SeabornDialogs.getDisplayManager().showDisplayToPlayer(bgDisplay, this.getPlayer());
        bgDisplay.getEntity().setBackgroundColor(Color.fromARGB(0, 0, 0, 0));

        // === Spawn main grid ===
        for (int i = 0; i < itemCount; i++) {
            int row = i / rowSize;
            int col = i % rowSize;

            double colShift = col * (fishSize + spacing) - (totalCols - 1) * (fishSize + spacing) / 2.0;
            double rowShift = row * (fishSize + spacing) - (totalRows - 1) * (fishSize + spacing) / 2.0;

            Vector colOffset = right.clone().multiply(colShift);
            Vector rowOffset = down.clone().multiply(rowShift);

            Location loc = this.getLocation().clone().add(colOffset).add(rowOffset);

            Display display = SeabornDialogs.getDisplayManager().createDisplay(
                    loc,
                    Component.text((char) (0xa000 + i)).font(Key.key("seaborn_images", "fish"))
            );
            display.setInteractable(true);
            display.setDialog(this);

            this.getDisplays().add(display);
            SeabornDialogs.getDisplayManager().showDisplayToPlayer(display, this.getPlayer());
            display.getEntity().setBackgroundColor(Color.fromARGB(0, 0, 0, 0));

            int index = i;
            display.onInteract((player) -> {
                player.sendMessage(String.valueOf(index));
                return null;
            });
        }



        // === Spawn left-side menu ===
        String[] menuItems = SeabornFishing.config.getRegions().keySet().stream().map(id -> SeabornFishing.config.getRegion(id).getDisplayName()).toArray(String[]::new);
        int maxLength = 0;

        for (String menuItem : menuItems) {
            String deserialized = ((TextComponent) MiniMessage.miniMessage().deserialize(menuItem)).content();
            if (deserialized.length() > maxLength) maxLength = deserialized.length();
        }

        double menuSpacing = fishSize; // vertical spacing same as grid cells
        double menuOffsetFromGrid = fishSize * (totalCols / 2.0 + 1) + .75; // push left of grid

        Location leftMenuLoc = this.getLocation().clone().addRotation(-20, 0).add(forward.clone().multiply(.2));

        for (int i = 0; i < menuItems.length; i++) {
            double verticalShift = (i - (menuItems.length - 1) / 2.0) * menuSpacing;

            Vector leftOffset = right.clone().multiply(-menuOffsetFromGrid);
            Vector verticalOffset = down.clone().multiply(verticalShift);

            Location menuLoc = leftMenuLoc.clone().add(leftOffset).add(verticalOffset);

            Display menuDisplay = SeabornDialogs.getDisplayManager().createDisplay(
                    menuLoc,
                    MiniMessage.miniMessage().deserialize(addSpaces(menuItems[i], maxLength - menuItems[i].length()))
            );
            menuDisplay.setInteractable(true);
            menuDisplay.setDialog(this);

            this.getDisplays().add(menuDisplay);
            SeabornDialogs.getDisplayManager().showDisplayToPlayer(menuDisplay, this.getPlayer());
            menuDisplay.getEntity().setBackgroundColor(Color.fromARGB(150, 0, 0, 0));

            int index = i;
            menuDisplay.onInteract((player) -> {
                player.sendMessage("Clicked menu: " + menuItems[index]);
                return null;
            });
        }


        // === Spawn right-side info panel for current fish ===
        if (selectedFish != null) {
            double infoOffsetFromGrid = fishSize * (totalCols / 2.0 + 1) + 0.7; // push right of grid
            Vector rightOffset = right.clone().multiply(infoOffsetFromGrid);

            Location infoLoc = this.getLocation().clone().addRotation(20, 0).add(forward.clone().multiply(.2)).add(rightOffset);

            // Image of the fish
            Location fishImageLoc = infoLoc.clone().add(down.clone().multiply(-0.3)); // slightly above center
            Display fishImage = SeabornDialogs.getDisplayManager().createDisplay(
                    fishImageLoc,
                    Component.text("\ua005").font(Key.key("seaborn_images", "fish")) // dummy image
            );
            fishImage.setScale(2.0f); // scale to 2
            fishImage.setInteractable(false);
            this.getDisplays().add(fishImage);
            SeabornDialogs.getDisplayManager().showDisplayToPlayer(fishImage, this.getPlayer());
            fishImage.getEntity().setBackgroundColor(Color.fromARGB(0, 0, 0, 0));

            // Name below the image

            List<Component> text = new ArrayList<>();
            text.add(Component.text(""));
            text.add(Component.text(""));
            text.add(Component.text(""));
            text.add(Component.text(""));
            text.add(Component.text(""));
            text.add(Component.text(""));

            text.add(Utils.formatTextMM(this.selectedFish.getDisplayName()));
            text.add(Component.text(Utils.formatText(this.selectedFish.getRarity().getDisplayName())));
            text.add(Component.text(""));
            text.add(Component.text(this.selectedFish.getShortDescription()));
            text.add(Component.text(""));
            text.add(Component.text("Largest caught: 32.45kg"));
            text.add(Component.text("Caught by: zNiki_"));

            Component finalText = Component.text("");
            for (Component t : text) {
                finalText = finalText.append(t).append(Component.text("\n"));
            }

            Location fishNameLoc = fishImageLoc.clone().add(down.clone().multiply(.09 * text.size())).add(down.clone().multiply((-.09 * 4) - .05)); // slightly below image
            Display fishName = SeabornDialogs.getDisplayManager().createDisplay(
                    fishNameLoc,
                    finalText
                    //Utils.formatTextMM(this.selectedFish.getDisplayName())
            );
            fishName.setInteractable(false);
            fishName.setScale(.6f);
            this.getDisplays().add(fishName);
            SeabornDialogs.getDisplayManager().showDisplayToPlayer(fishName, this.getPlayer());
            fishName.getEntity().setAlignment(TextDisplay.TextAlignment.CENTER);
            fishName.getEntity().setLineWidth(130);
            fishName.getEntity().setBackgroundColor(Color.fromARGB(150, 0, 0, 0));

            // Rarity below the name
            /*Location fishRarityLoc = fishNameLoc.clone().add(down.clone().multiply(.125));
            Display fishRarity = SeabornDialogs.getDisplayManager().createDisplay(
                    fishRarityLoc,
                    Utils.formatText(this.selectedFish.getRarity().getDisplayName())
            );
            fishRarity.setInteractable(false);
            this.getDisplays().add(fishRarity);
            SeabornDialogs.getDisplayManager().showDisplayToPlayer(fishRarity, this.getPlayer());

            // Description below the rarity
            Location fishDescLoc = fishRarityLoc.clone().add(down.clone().multiply(.2));
            Display fishDesc = SeabornDialogs.getDisplayManager().createDisplay(
                    fishDescLoc,
                    Utils.formatText(this.selectedFish.getShortDescription())
            );
            fishDesc.setInteractable(false);
            fishDesc.setScale(.35f);
            this.getDisplays().add(fishDesc);
            SeabornDialogs.getDisplayManager().showDisplayToPlayer(fishDesc, this.getPlayer());
            fishDesc.getEntity().setAlignment(TextDisplay.TextAlignment.LEFT);
            fishDesc.getEntity().setDisplayHeight(50);

            // Largest caught below description
            Location largestCaughtLoc = fishDescLoc.clone().add(down.clone().multiply(.2));
            Display largestCaught = SeabornDialogs.getDisplayManager().createDisplay(
                    largestCaughtLoc,
                    Utils.formatText(this.selectedFish.getShortDescription())
            );
            largestCaught.setInteractable(false);
            largestCaught.setScale(.35f);
            this.getDisplays().add(largestCaught);
            SeabornDialogs.getDisplayManager().showDisplayToPlayer(largestCaught, this.getPlayer());*/

        }

    }

    @Override
    public void spawn() {
        this.spawnUpdate();
    }

}
