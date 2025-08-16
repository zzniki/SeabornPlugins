package com.seabornmc.seabornCosmetics;

import com.seabornmc.seabornCosmetics.cosmetics.Hat;

import java.util.HashMap;
import java.util.Map;

public class CosmeticManager {

    private Map<String, Hat> hats = new HashMap<>();

    public void loadFromAPI() {
        this.hats = APILoader.loadHats();

        SeabornCosmetics.getInstance().getLogger().info("Loaded " + this.hats.keySet().size() + " hats!");
    }

    public Hat getHat(String id) { return this.hats.get(id); }

    public Map<String, Hat> getHats() {
        return hats;
    }

    public void setHats(Map<String, Hat> hats) {
        this.hats = hats;
    }
}
