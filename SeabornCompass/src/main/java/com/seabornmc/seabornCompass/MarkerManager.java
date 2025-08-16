package com.seabornmc.seabornCompass;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MarkerManager {

    private Map<Player, List<Marker>> markers = new HashMap<>();

    public void addTracker(Player player, Location location) {
        Marker marker = new Marker(location, player);

        if (!this.markers.containsKey(player)) this.markers.put(player, new ArrayList<>());
        this.markers.get(player).add(marker);

        marker.spawn();
    }

    public void removeTrackers(Player player) {
        if (markers.containsKey(player)) {
            for (Marker marker : this.markers.get(player)) {
                marker.despawn();
            }
            this.markers.remove(player);
        }
    }

    public void updateTrackers() {
        for (Player player : markers.keySet()) {
            for (Marker marker : markers.get(player)) {
                marker.update();
            }
        }
    }

}
