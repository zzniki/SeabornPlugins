package com.seabornmc.seabornDialogs;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisplayManager {

    private SeabornDialogs plugin;

    private List<Display> displays = new ArrayList<>();
    private Map<Player, List<Display>> playerDisplays = new HashMap<>();
    private Map<Player, Display> lookingDisplay = new HashMap<>();

    public DisplayManager(SeabornDialogs plugin) {
        this.plugin = plugin;
    }

    public void showDisplayToPlayer(Display display, Player player) {
        if (display.getEntity() == null) display.createEntity();
        player.showEntity(plugin, display.getEntity());

        if (!this.playerDisplays.containsKey(player)) this.playerDisplays.put(player, new ArrayList<>());
        this.playerDisplays.get(player).add(display);

        display.addShownPlayer(player);
    }

    public Display createDisplay(Location location, String text) {
        Display display = new Display(location, Component.text(text));
        this.displays.add(display);
        return display;
    }

    public Display createDisplay(Location location, Component text) {
        Display display = new Display(location, text);
        this.displays.add(display);
        return display;
    }

    public void deleteDisplay(Display display) {
        if (display.getEntity() != null) display.despawnEntity();

        if (!display.getShownPlayers().isEmpty()) {
            for (Player player : display.getShownPlayers()) {
                this.playerDisplays.get(player).remove(display);
            }
        }

        this.displays.remove(display);
    }

    public void cleanupDisplays() {
        for (Display display : this.displays) {
            if (display.getEntity() != null) display.despawnEntity();
        }
    }

    private double getLookValue(Location eye, Display display) {
        if (display.getEntity() == null) return -1;

        Vector toEntity = display.getEntity().getLocation().toVector().subtract(eye.toVector());
        double dot = toEntity.normalize().dot(eye.getDirection());

        return dot;

    }

    public Display calcLookingDisplay(Player player) {
        if (!this.playerDisplays.containsKey(player)) return null;

        Display disp = null;
        double lookValue = Double.MIN_VALUE;

        for (Display d : this.playerDisplays.get(player)) {

            if (!d.isInteractable()) continue;

            double l = this.getLookValue(player.getEyeLocation(), d);
            if (l > lookValue) {
                disp = d;
                lookValue = l;
            }
        }

        if (lookValue < 0.1) {
            if (disp != null && disp.isInteractable()) {
                if (SeabornDialogs.getDialogManager().getDialog(player) != null) {
                    SeabornDialogs.getDialogManager().deleteDialog(SeabornDialogs.getDialogManager().getDialog(player));
                }
                if (disp != null) this.deleteDisplay(disp);
            }
        }

        return disp;

    }

    public void updateLookingDisplay(Player player) {

        if (!this.playerDisplays.containsKey(player)) {
            this.lookingDisplay.remove(player);
            return;
        }

        Display looking = this.calcLookingDisplay(player);
        int interactableCount = 0;

        for (Display d : this.playerDisplays.get(player)) {
            if (!d.isInteractable()) continue;

            if (looking.equals(d)) {
                d.setHovered(true);
            } else {
                d.setHovered(false);
            }
        }

        if (looking == null) this.lookingDisplay.remove(player);
        else this.lookingDisplay.put(player, looking);

    }

    public Display getLookingDisplay(Player player) {
        return this.lookingDisplay.getOrDefault(player, null);
    }

}
