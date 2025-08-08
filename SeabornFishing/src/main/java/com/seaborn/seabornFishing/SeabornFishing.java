package com.seaborn.seabornFishing;

import com.seaborn.seabornFishing.commands.FishCommand;
import com.seaborn.seabornFishing.commands.RodCommand;
import com.seaborn.seabornFishing.emote.EmoteListener;
import com.seaborn.seabornFishing.minigames.GameScheduler;
import com.seaborn.seabornFishing.minigames.NumbersGame;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class SeabornFishing extends JavaPlugin {

    public Map<String, Minigame> minigames = new HashMap<String, Minigame>();
    public GameScheduler scheduler;
    public static Config config;

    private void loadMinigames() {

        this.registerMinigame(new NumbersGame(this));

    }

    public void registerMinigame(Minigame game) {
        this.minigames.put(game.getId(), game);
        //getServer().getPluginManager().registerEvents(game, this);
    }

    @Override
    public void onLoad() {
        /*CommandAPIBukkitConfig config = new CommandAPIBukkitConfig(this)
                .shouldHookPaperReload(true)
                .missingExecutorImplementationMessage("You are not able to use this command!");
        CommandAPI.onLoad(config);*/
    }

    @Override
    public void onEnable() {

        getLogger().info("Loading SeabornFishing...");

        config = new Config(this);
        config.loadConfigFromAPI();

        this.loadMinigames();
        getServer().getLogger().info("Loaded " + minigames.keySet().size() + " fishing minigames!");

        scheduler = new GameScheduler(this);

        getServer().getPluginManager().registerEvents(new Listeners(scheduler, this), this);
        getServer().getPluginManager().registerEvents(new EmoteListener(), this);

        //CommandAPI.onEnable();
        this.registerCommands();

    }

    @Override
    public void onDisable() {

        terminateGuis();
        //CommandAPI.onDisable();

    }

    private void registerCommands() {
        new RodCommand(this).getCommand().register(this);
        new FishCommand(this).getCommand().register(this);
    }

    private void terminateGuis() {
        /*getServer().getOnlinePlayers().forEach(player -> {
            InventoryGui inventoryGui = InventoryGui.getOpen(player);
            if (inventoryGui != null) {
                inventoryGui.close();
            }
        });*/
    }
}
