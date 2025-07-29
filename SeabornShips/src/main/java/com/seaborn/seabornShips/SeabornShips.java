package com.seaborn.seabornShips;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class SeabornShips extends JavaPlugin {

    public static Map<String, BoatType> boatTypes = new HashMap<String, BoatType>();

    public void loadBoatTypes() {
        FileConfiguration config = this.getConfig();

        ConfigurationSection typesSection = config.getConfigurationSection("boats");

        for (String typeId : typesSection.getKeys(false)) {
            ConfigurationSection type = typesSection.getConfigurationSection(typeId);

            BoatType boatType = new BoatType(typeId);

            String[] model = type.getString("entity-model").split(":");

            boatType.setName(type.getString("name"));
            boatType.setSpeed(type.getDouble("speed"));
            boatType.setEntityModel(new NamespacedKey(model[0], model[1]));
            boatType.setLore(type.getStringList("lore"));

            this.boatTypes.put(typeId, boatType);

        }

        getLogger().info("Loaded " + String.valueOf(this.boatTypes.keySet().size()) + " boat types.");
    }

    @Override
    public void onEnable() {

        //protocolManager = ProtocolLibrary.getProtocolManager();

        BoatListener boatListener = new BoatListener(this);
        getServer().getPluginManager().registerEvents(boatListener, this);

        this.saveDefaultConfig();
        this.loadBoatTypes();

        this.getCommand("boat").setExecutor(new Commands());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
