package com.seaborn.seabornFishing;

import com.seaborn.seabornFishing.emote.EmoteListener;
import com.seaborn.seabornFishing.minigames.NumbersGame;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class SeabornFishing extends JavaPlugin {

    public Map<String, Minigame> minigames = new HashMap<String, Minigame>();
    public GameScheduler scheduler;

    private void loadMinigames() {

        this.registerMinigame(new NumbersGame(this));

    }

    public void registerMinigame(Minigame game) {
        this.minigames.put(game.getId(), game);
        //getServer().getPluginManager().registerEvents(game, this);
    }

    @Override
    public void onEnable() {

        this.loadMinigames();
        getServer().getLogger().info("Loaded " + minigames.keySet().size() + " fishing minigames!");

        scheduler = new GameScheduler(this);

        getServer().getPluginManager().registerEvents(new Listeners(scheduler, this), this);
        getServer().getPluginManager().registerEvents(new EmoteListener(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
