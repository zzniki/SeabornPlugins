package com.seabornmc.seabornChairs;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChairManager {

    public Map<Player, Chair> chairs = new HashMap<>();
    public Map<Entity, Chair> standChairs = new HashMap<>();

    public void createChair(Player player) {
        Chair chair = new Chair(player);
        chairs.put(player, chair);
        chair.spawn();
        standChairs.put(chair.getStand(), chair);
    }

    public void removeChair(Player player) {
        Chair chair = chairs.get(player);
        chair.despawn();
        standChairs.remove(chair.getStand());
        chairs.remove(player);
    }

    public void removeChairs() {
        for (Player player : chairs.keySet()) {
            this.removeChair(player);
        }
    }

    public void toggleChair(Player player) {
        if (!chairs.containsKey(player)) {
            createChair(player);
        } else removeChair(player);
    }

    public void onDismount(Entity entity) {
        if (entity instanceof ArmorStand stand) {
            if (standChairs.containsKey(stand)) {
                this.removeChair(standChairs.get(entity).getPlayer());
            }
        }
    }

}
