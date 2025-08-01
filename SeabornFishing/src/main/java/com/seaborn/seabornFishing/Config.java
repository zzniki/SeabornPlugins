package com.seaborn.seabornFishing;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {

    private Map<String, CustomRod> customRods = new HashMap<>();
    private Map<String, Rarity> rarities = new HashMap<>();
    private Map<String, Region> regions = new HashMap<>();
    private Map<String, Fish> fish = new HashMap<>();

    public void loadConfig() {

        CustomRod rod = new CustomRod(
                "default",
                "Makeshift Rod"
        );
        rod.setItemModel(new NamespacedKey("seaborn_rods", "makeshift"));

        this.customRods.put("default", rod);
        this.rarities.put("common", new Rarity(
                "common",
                "COMMON",
                1,
                100
        ));
        this.regions.put("global", new Region(
                "global",
                "Global",
                "__global__"
        ));
        this.fish.put("carp", new Fish(
                this.rarities.get("common"),
                "carp",
                "Carp",
                1,
                10,
                new NamespacedKey("seaborn_fish", "carp")
        ));

        this.regions.get("global").addFish(this.fish.get("carp"));

    }

    public CustomRod getCustomRod(String id) {
        return this.customRods.get(id);
    }
    public boolean hasRegion(String id) { return this.regions.containsKey(id); }
    public Region getRegion(String id) { return this.regions.get(id); }

    public Map<String, CustomRod> getCustomRods() {
        return customRods;
    }

    public void setCustomRods(Map<String, CustomRod> customRods) {
        this.customRods = customRods;
    }

    public Map<String, Rarity> getRarities() {
        return rarities;
    }

    public void setRarities(Map<String, Rarity> rarities) {
        this.rarities = rarities;
    }

    public Map<String, Region> getRegions() {
        return regions;
    }

    public void setRegions(Map<String, Region> regions) {
        this.regions = regions;
    }

    public Map<String, Fish> getFish() {
        return fish;
    }

    public void setFish(Map<String, Fish> fish) {
        this.fish = fish;
    }
}
