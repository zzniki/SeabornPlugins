package com.seabornmc.seabornCompass;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.yaml.snakeyaml.error.Mark;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.structs.ActiveObjective;
import rocks.gravili.notquests.paper.structs.ActiveQuest;
import rocks.gravili.notquests.paper.structs.QuestPlayer;
import rocks.gravili.notquests.paper.structs.objectives.ReachLocationObjective;

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
    }

    public void removeTrackers(Player player) {
        if (markers.containsKey(player)) {
            for (Marker marker : this.markers.get(player)) {
                marker.despawn();
            }
            this.markers.remove(player);
        }
    }

    public void showMarkers(Player player) {
        for (Marker marker : this.markers.get(player)) {
            marker.spawn();
        }
    }

    public void hideMarkers(Player player) {
        for (Marker marker : this.markers.get(player)) {
            marker.despawn();
        }
    }

    public void addTrackersFromQuest() {
        for (Player player : Bukkit.getOnlinePlayers()) {

            if (!this.markers.containsKey(player)) this.markers.put(player, new ArrayList<>());

            List<Location> locations = new ArrayList<>();

            QuestPlayer questPlayer = NotQuests.getInstance().getQuestPlayerManager().getActiveQuestPlayer(player.getUniqueId());
            if (questPlayer == null) continue;
            for (ActiveQuest quest : questPlayer.getActiveQuests()) {
                for (ActiveObjective objective : quest.getActiveObjectives()) {
                    if (objective.getObjective() instanceof ReachLocationObjective reachLocObj) {
                        Location finalLoc = reachLocObj.getMaxLocation().clone().add(reachLocObj.getMinLocation()).multiply(.5);
                        locations.add(finalLoc);
                    }
                }
            }

            for (Location loc : locations) {
                if (!this.markers.get(player).stream().map(Marker::getLocation).toList().contains(loc)) {
                    this.addTracker(player, loc);
                }
            }

            List<Marker> toRemove = new ArrayList<>();

            for (Marker marker : this.markers.get(player)) {
                if (!locations.contains(marker.getLocation())) {
                    marker.despawn();
                    toRemove.add(marker);
                }
            }

            for (Marker marker : toRemove) {
                this.markers.get(player).remove(marker);
            }
        }
    }

    public void updateTrackers() {

        this.addTrackersFromQuest();

        for (Player player : markers.keySet()) {
            for (Marker marker : markers.get(player)) {
                marker.update();
            }
        }
    }

}
