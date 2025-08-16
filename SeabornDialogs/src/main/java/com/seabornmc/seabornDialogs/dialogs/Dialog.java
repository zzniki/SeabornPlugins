package com.seabornmc.seabornDialogs.dialogs;

import com.seabornmc.seabornDialogs.Display;
import com.seabornmc.seabornDialogs.SeabornDialogs;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public abstract class Dialog {

    private List<Display> displays;
    private Player player;
    private Location location;

    public Dialog(Location location, Player player) {

        this.displays = new ArrayList<>();
        this.player = player;
        this.location = location;

    }

    public void spawn() {



    }
    public void delete() {
        for (Display d : this.displays) {
            SeabornDialogs.getDisplayManager().deleteDisplay(d);
        }
        this.displays.clear();
    }

    public List<Display> getDisplays() {
        return displays;
    }

    public void setDisplays(List<Display> displays) {
        this.displays = displays;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
