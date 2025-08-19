package com.seabornmc.seabornCompass;

import com.maximde.hologramlib.HologramLib;
import com.maximde.hologramlib.hologram.HologramManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.structs.objectives.ReachLocationObjective;

public final class SeabornCompass extends JavaPlugin {

    private static HologramManager hologramManager;
    private static MarkerManager markerManager;

    private BukkitTask updateTask;

    @Override
    public void onLoad() {
        HologramLib.onLoad(this);
    }

    @Override
    public void onEnable() {
        hologramManager = HologramLib.getManager().orElse(null);
        if (hologramManager == null) {
            getLogger().severe("Failed to initialize HologramLib manager. Plugin will not function correctly.");
            return;
        } else {
            getLogger().info("Successfully initialized HologramLib!");
        }

        markerManager = new MarkerManager();

        getServer().getPluginManager().registerEvents(new Listeners(), this);

        this.updateTask = getServer().getScheduler().runTaskTimer(this, () -> {
            markerManager.updateTrackers();
        }, 0, 5);



    }

    @Override
    public void onDisable() {
        this.updateTask.cancel();
        hologramManager.removeAll();
    }

    public static HologramManager getHoloManager() { return hologramManager; }
    public static MarkerManager getTrackerManager() { return markerManager; }
}
