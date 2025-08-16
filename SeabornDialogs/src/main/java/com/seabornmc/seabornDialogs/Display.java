package com.seabornmc.seabornDialogs;

import com.seabornmc.seabornDialogs.dialogs.Dialog;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Display {

    private Component text;
    private Location location;
    private float opacity = 1f;
    private float scale = 1f;

    private TextDisplay entity;
    private List<Player> shownPlayers = new ArrayList<>();

    private boolean isHovered = false;
    private boolean isInteractable = false;

    private Function<Player, Void> onInteractFunction;

    private Dialog dialog = null;

    public Display(Location loc, Component text) {
        this.text = text;
        this.location = loc;

        this.entity = null;
    }

    public TextDisplay createEntity() {
        TextDisplay textDisplay = (TextDisplay) location.getWorld().spawnEntity(location, EntityType.TEXT_DISPLAY, CreatureSpawnEvent.SpawnReason.CUSTOM, entity -> {
           entity.setVisibleByDefault(false);
           //entity.getPersistentDataContainer().set()
        });

        textDisplay.text(text);

        byte opacityByte = (byte) Math.round(this.opacity * 255f);
        textDisplay.setTextOpacity(opacityByte);

        textDisplay.setBillboard(org.bukkit.entity.Display.Billboard.FIXED);
        textDisplay.setShadowed(false);
        textDisplay.setSeeThrough(false);
        textDisplay.setAlignment(TextDisplay.TextAlignment.LEFT);
        textDisplay.setPersistent(false);

        Transformation transformation = textDisplay.getTransformation();
        Vector3f newScale = transformation.getScale().set(this.scale * .5);
        Quaternionf newRoll = new Quaternionf();

        Transformation newTransform = new Transformation(
                transformation.getTranslation(),
                transformation.getLeftRotation(),
                newScale,
                newRoll
        );

        textDisplay.setTransformation(newTransform);

        this.entity = textDisplay;

        return textDisplay;

    }

    public TextDisplay getEntity() {
        return entity;
    }

    public void despawnEntity() {
        this.entity.remove();
        this.entity = null;
    }

    public Component getText() {
        return text;
    }

    public void setText(Component text) {
        this.text = text;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public float getOpacity() {
        return opacity;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public List<Player> getShownPlayers() {
        return shownPlayers;
    }

    public void setShownPlayers(List<Player> shownPlayers) {
        this.shownPlayers = shownPlayers;
    }

    public void addShownPlayer(Player player) {
        this.shownPlayers.add(player);
    }

    public boolean getHovered() { return this.isHovered; }

    public void setHovered(boolean hover) {
        this.isHovered = hover;

        if (this.isHovered && this.getEntity() != null) {
            this.getEntity().text(this.text.color(TextColor.color(0, 255, 0)));
        } else if (!this.isHovered && this.getEntity() != null) {
            this.getEntity().text(this.text);
        }
    }

    public boolean isInteractable() {
        return isInteractable;
    }

    public void setInteractable(boolean interactable) {
        isInteractable = interactable;
    }

    public void onInteract(Function<Player, Void> func) {
        this.onInteractFunction = func;
    }

    public void callInteract(Player player) {
        this.onInteractFunction.apply(player);
    }

    public Dialog getDialog() {
        return dialog;
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
