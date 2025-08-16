package com.seabornmc.seabornWeapons;

import com.seabornmc.seabornWeapons.weapons.Sword;

import java.util.HashMap;
import java.util.Map;

public class WeaponManager {

    private Map<String, Sword> swords = new HashMap<>();

    public void loadFromAPI() {
        this.swords = APILoader.loadSwords();

        SeabornWeapons.getInstance().getLogger().info("Loaded " + this.swords.size() + " swords!");

    }

    public Map<String, Sword> getSwords() { return this.swords; }

    public Sword getSword(String id) { return this.swords.get(id); }
}
