package com.seaborn.seabornCrews.listeners;

import com.infernalsuite.asp.api.world.SlimeWorldInstance;
import com.seaborn.seabornCore.db.model.PlayerData;
import com.seaborn.seabornCore.events.PeriodicSaveEvent;
import com.seaborn.seabornCore.events.PlayerDataLoadEvent;
import com.seaborn.seabornCrews.SeabornCrews;
import com.seaborn.seabornCrews.service.IslandService;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class IslandListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        Location loc = player.getLocation().clone();

        if (loc.getWorld().getName().startsWith(IslandService.PREFIX)) {
            Bukkit.getScheduler().runTaskLater(SeabornCrews.getInstance(), () -> {
                SeabornCrews.getIslandService().unloadIslandIfEmpty(loc.getWorld().getName());
            }, 1L);
        }

    }
    
    @EventHandler
    public void onPlayerDataLoad(PlayerDataLoadEvent event) {

        PlayerData data = event.getData();

        if (data.getLastWorldName().startsWith(IslandService.PREFIX)) {
            Location spawnLocation = new Location(Bukkit.getWorld("world"), 913.5, 82.5, 333.5, 90f, 0.0f);;
            event.getData().setLastLocation(spawnLocation);

            String worldName = data.getLastWorldName();
            SeabornCrews.getIslandService().loadWorld(worldName).thenAccept((instance) -> {
                if (instance == null) return;
                Bukkit.getScheduler().runTaskLater(SeabornCrews.getInstance(), () -> {
                    Player player = Bukkit.getPlayer(data.getUuid());

                    if (player == null) return;
                    if (!player.isOnline()) return;

                    player.teleport(new Location(instance.getBukkitWorld(), 0, 62, 0));

                }, 1L);
            });
        }
    }

    @EventHandler
    public void onPeriodicSave(PeriodicSaveEvent event) {
        SeabornCrews.getInstance().saveIslands();
    }
}
