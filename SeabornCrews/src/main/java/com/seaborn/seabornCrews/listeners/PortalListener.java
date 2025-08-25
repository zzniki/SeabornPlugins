package com.seaborn.seabornCrews.listeners;

import com.seaborn.seabornCrews.SeabornCrews;
import com.seaborn.seabornCrews.Utils;
import com.seaborn.seabornCrews.model.Crew;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PortalListener implements Listener {

    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    Map<UUID, Crew> teleportingPlayers = new HashMap<>();

    @EventHandler
    public void onMove(PlayerMoveEvent event) {

        Player player = event.getPlayer();

        RegionQuery query = container.createQuery();
        Set<ProtectedRegion> set = query.getApplicableRegions(BukkitAdapter.adapt(player.getLocation())).getRegions();

        if (!set.isEmpty() && !teleportingPlayers.containsKey(player.getUniqueId())) {
            for (ProtectedRegion region : set) {
                if (region.getId().startsWith("island_portal")) {
                    try {
                        Crew crew = SeabornCrews.getCrewService().getPlayerCrew(player.getUniqueId());
                        if (crew == null) {
                            this.teleportingPlayers.put(player.getUniqueId(), null);
                            player.sendMessage(Utils.formatText("<red>You are not in a crew! Talk with Alice."));
                            player.setVelocity(player.getLocation().getDirection().normalize().multiply(-1));

                            Bukkit.getScheduler().runTaskLater(SeabornCrews.getInstance(), () -> {
                                this.teleportingPlayers.remove(player.getUniqueId());
                            }, 20L);

                            continue;
                        }

                        teleportingPlayers.put(player.getUniqueId(), crew);
                        SeabornCrews.getIslandService().teleportToIsland(player, crew.getIslandWorld()).thenRun(() -> {
                            Bukkit.getScheduler().runTaskLater(SeabornCrews.getInstance(), () -> {
                                this.teleportingPlayers.remove(player.getUniqueId());
                            }, 3L);
                        });
                    } catch (SQLException e) {
                        continue;
                    }
                }
            }
        }

    }

}
