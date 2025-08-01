package com.seaborn.seabornFishing;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

    private Map<String, CustomRod> customRods = new HashMap<>();
    private Map<String, Rarity> rarities = new HashMap<>();
    private Map<String, Region> regions = new HashMap<>();
    private Map<String, Fish> fish = new HashMap<>();

    public void loadConfig() {

        this.customRods.put("default", new CustomRod());
        this.rarities.put("common", new Rarity(
                "common",
                "COMMON",
                1,
                100
        ));
        this.regions.put("__global__", new Region(
                "global",
                "Global",
                "__global__"
        ));
        //this.fish.put("cod", new Fish())

    }

    public CustomRod getCustomRod(String id) {
        return this.customRods.get(id);
    }
    public boolean hasRegion(String id) { return this.regions.containsKey(id); }
    public Region getRegion(String id) { return this.regions.get(id); }

}
