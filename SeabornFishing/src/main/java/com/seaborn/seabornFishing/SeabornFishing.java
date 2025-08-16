package com.seaborn.seabornFishing;

import com.seaborn.seabornFishing.commands.BestiaryCommand;
import com.seaborn.seabornFishing.commands.FishCommand;
import com.seaborn.seabornFishing.commands.RodCommand;
import com.seaborn.seabornFishing.emote.EmoteListener;
import com.seaborn.seabornFishing.minigames.GameScheduler;
import com.seaborn.seabornFishing.minigames.NumbersGame;
import com.seaborn.seabornFishing.traits.FishmongerTrait;
import com.seabornmc.seabornDialogs.SeabornDialogs;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPICommand;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class SeabornFishing extends JavaPlugin {

    public Map<String, Minigame> minigames = new HashMap<String, Minigame>();
    public GameScheduler scheduler;
    public static Config config;
    private static Economy economy;

    private static SeabornFishing plugin;

    public static SeabornFishing getInstance() {
        return plugin;
    }

    public static Economy getEconomy() { return economy; }

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

        plugin = this;

        getLogger().info("Loading SeabornFishing...");

        setupEconomy();

        config = new Config(this);
        config.loadConfigFromAPI();

        this.loadMinigames();
        getServer().getLogger().info("Loaded " + minigames.keySet().size() + " fishing minigames!");

        scheduler = new GameScheduler(this);

        getServer().getPluginManager().registerEvents(new Listeners(scheduler, this), this);
        getServer().getPluginManager().registerEvents(new EmoteListener(), this);

        //CommandAPI.onEnable();
        this.registerCommands();

        CitizensAPI.getTraitFactory().registerTrait(
                TraitInfo.create(FishmongerTrait.class)
        );

    }

    @Override
    public void onDisable() {

        terminateGuis();
        CitizensAPI.getTraitFactory().deregisterTrait(TraitInfo.create(FishmongerTrait.class));
        //CommandAPI.onDisable();

        CommandAPI.unregister("bestiary");

    }

    private void registerCommands() {
        new RodCommand(this).getCommand().register(this);
        new FishCommand(this).getCommand().register(this);
        new BestiaryCommand().getCommand().register(this);
    }

    private void setupEconomy() {
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) return;
        economy = rsp.getProvider();
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
