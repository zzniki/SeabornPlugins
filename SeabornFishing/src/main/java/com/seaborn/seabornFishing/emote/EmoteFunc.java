package com.seaborn.seabornFishing.emote;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class EmoteFunc {

    public static Location rotateLoc(Location loc, Location axis, double angle) {
        return loc.clone().subtract(axis).toVector().multiply(new Vector(1,0,1)).rotateAroundY(angle).add(axis.toVector()).toLocation(loc.getWorld());
    }

    public static Location getEmoteLocation(Player player) {
        return player.getEyeLocation().add(player.getLocation().getDirection().multiply(2));
    }
}
