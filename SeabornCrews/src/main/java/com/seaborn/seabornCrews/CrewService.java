package com.seaborn.seabornCrews;

import com.infernalsuite.asp.api.AdvancedSlimePaperAPI;
import com.infernalsuite.asp.api.world.SlimeWorld;
import com.infernalsuite.asp.api.world.SlimeWorldInstance;
import com.infernalsuite.asp.api.world.properties.SlimeProperties;
import com.infernalsuite.asp.api.world.properties.SlimePropertyMap;
import com.seaborn.seabornCrews.SeabornCrews;
import com.seaborn.seabornCrews.db.CrewDAO;
import com.seaborn.seabornCrews.model.Crew;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;
import java.util.concurrent.CompletableFuture;

public class CrewService {
    private final SeabornCrews plugin;
    private final CrewDAO dao;
    private final AdvancedSlimePaperAPI aspApi; // may be null
    private final Map<String, Crew> cacheByName = new HashMap<>();
    private final Logger logger;
    private final Map<UUID, String> lastWorld = new HashMap<>();

    public CrewService(SeabornCrews plugin, DataSource ds, AdvancedSlimePaperAPI aspApi) {
        this.plugin = plugin;
        this.dao = new CrewDAO(ds, plugin.getLogger());
        this.aspApi = aspApi;
        this.logger = plugin.getLogger();
    }

    public void init() {
        try {
            dao.createTables();
        } catch (Exception e) {
            logger.severe("Could not create DB tables: " + e.getMessage());
        }
    }

    public CompletableFuture<Optional<Crew>> createCrewAsync(String name, UUID owner) {
        // Prevent creating a crew if already in one
        /*if (getCrewByMember(owner).isPresent()) {
            return CompletableFuture.completedFuture(Optional.empty());
        }*/
        return CompletableFuture.supplyAsync(() -> dao.createCrew(name, owner));
    }

    public Optional<Crew> getCrewByName(String name) {
        return dao.findByName(name);
    }

    public Optional<Crew> getCrewByMember(UUID player) {
        return dao.findByMember(player);
    }

    public boolean addMember(Crew crew, UUID player) {
        try {
            Optional<Integer> count = dao.getMemberCount(crew.getId());
            if (count.isPresent() && count.get() >= 25) return false;
            dao.addMember(crew.getId(), player);
            return true;
        } catch (Exception e) {
            logger.severe("addMember error: " + e.getMessage());
            return false;
        }
    }

    public boolean removeMember(Crew crew, UUID player) {
        try {
            return dao.removeMember(crew.getId(), player);
        } catch (Exception e) {
            logger.severe("removeMember error: " + e.getMessage());
            return false;
        }
    }

    public void addReputation(String crewName, int amount) {
        Optional<Crew> oc = dao.findByName(crewName);
        oc.ifPresent(c -> dao.changeReputation(c.getId(), amount));
    }

    public CompletableFuture<Optional<String>> createIslandForCrewAsync(Crew crew) {
        if (aspApi == null) {
            return CompletableFuture.completedFuture(Optional.empty());
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                String worldName = "crew_island_" + crew.getId();

                SlimePropertyMap props = new SlimePropertyMap();
                props.setValue(SlimeProperties.ENVIRONMENT, "normal");
                props.setValue(SlimeProperties.WORLD_TYPE, "DEFAULT");
                props.setValue(SlimeProperties.PVP, true);
                props.setValue(SlimeProperties.ALLOW_MONSTERS, true);
                props.setValue(SlimeProperties.ALLOW_ANIMALS, true);
                props.setValue(SlimeProperties.DEFAULT_BIOME, "minecraft:plains");

                // ASYNC: Read the template world
                SlimeWorld template = aspApi.readWorld(plugin.getLoader(), "template", false, props);
                return template.clone(worldName);

            } catch (Exception e) {
                logger.severe("Failed to read template world: " + e.getMessage());
                return null;
            }
        }).thenCompose(slimeWorld -> {
            if (slimeWorld == null) {
                return CompletableFuture.completedFuture(Optional.empty());
            }

            CompletableFuture<Optional<String>> future = new CompletableFuture<>();

            // SYNC: Load the world
            Bukkit.getScheduler().runTask(plugin, () -> {
                try {
                    SlimeWorldInstance instance = aspApi.loadWorld(slimeWorld, true);
                    org.bukkit.World bukkitWorld = instance.getBukkitWorld();

                    crew.setIslandWorldName(slimeWorld.getName());
                    dao.setIslandWorldName(crew.getId(), slimeWorld.getName());

                    aspApi.saveWorld(instance);

                    future.complete(Optional.of(bukkitWorld.getName()));
                } catch (Exception e) {
                    logger.severe("Failed to load world: " + e.getMessage());
                    future.complete(Optional.empty());
                }
            });

            return future;
        });
    }

    public void teleportToIsland(Player player, Crew crew) {
        if (crew.getIslandWorldName() == null) {
            player.sendMessage("No island found for this crew.");
            return;
        }

        SlimeWorldInstance worldInstance = aspApi.getLoadedWorld(crew.getIslandWorldName());

        if (worldInstance != null) {
            player.teleport(worldInstance.getBukkitWorld().getSpawnLocation());
            return;
        }

        // Read asynchronously
        CompletableFuture.supplyAsync(() -> {
            SlimePropertyMap props = new SlimePropertyMap();
            props.setValue(SlimeProperties.ENVIRONMENT, "normal");
            props.setValue(SlimeProperties.WORLD_TYPE, "DEFAULT");
            props.setValue(SlimeProperties.PVP, true);
            props.setValue(SlimeProperties.ALLOW_MONSTERS, true);
            props.setValue(SlimeProperties.ALLOW_ANIMALS, true);
            props.setValue(SlimeProperties.DEFAULT_BIOME, "minecraft:plains");

            try {
                return aspApi.readWorld(plugin.getLoader(), crew.getIslandWorldName(), false, props);
            } catch (Exception e) {
                logger.severe("Failed to read world: " + e.getMessage());
                return null;
            }
        }).thenAccept(slimeWorld -> {
            if (slimeWorld == null) {
                player.sendMessage("Could not load the island.");
                return;
            }

            // Load synchronously
            Bukkit.getScheduler().runTask(plugin, () -> {
                try {
                    SlimeWorldInstance instance = aspApi.loadWorld(slimeWorld, true);
                    player.teleport(instance.getBukkitWorld().getSpawnLocation());
                } catch (Exception e) {
                    logger.severe("Failed to load world: " + e.getMessage());
                    player.sendMessage("Could not load the island.");
                }
            });
        });
    }

    public void recordLastWorld(UUID player, String worldName) {
        lastWorld.put(player, worldName);
    }

    public void handlePlayerJoin(Player player) {
        plugin.getLogger().info(player.getLocation().getWorld().getName());
        String last = lastWorld.get(player.getUniqueId());
        if (last != null && aspApi != null && aspApi.getLoadedWorld(last) == null) {
            // Try to load the world
            CompletableFuture.supplyAsync(() -> {
                SlimePropertyMap props = new SlimePropertyMap();
                props.setValue(SlimeProperties.ENVIRONMENT, "normal");
                props.setValue(SlimeProperties.WORLD_TYPE, "DEFAULT");
                props.setValue(SlimeProperties.PVP, true);
                props.setValue(SlimeProperties.ALLOW_MONSTERS, true);
                props.setValue(SlimeProperties.ALLOW_ANIMALS, true);
                props.setValue(SlimeProperties.DEFAULT_BIOME, "minecraft:plains");
                try {
                    return aspApi.readWorld(plugin.getLoader(), last, false, props);
                } catch (Exception e) {
                    logger.severe("Failed to read world: " + e.getMessage());
                    return null;
                }
            }).thenAccept(slimeWorld -> {
                if (slimeWorld != null) {
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        try {
                            SlimeWorldInstance instance = aspApi.loadWorld(slimeWorld, true);
                            player.teleport(instance.getBukkitWorld().getSpawnLocation());
                        } catch (Exception e) {
                            logger.severe("Failed to load world: " + e.getMessage());
                        }
                    });
                }
            });
        }
    }

    public void shutdown() {
        // graceful shutdown tasks if needed
    }

    public void disbandCrew(Crew crew) {
        // Kick all members
        for (UUID member : new ArrayList<>(crew.getMembers())) {
            try {
                dao.removeMember(crew.getId(), member);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            Player p = Bukkit.getPlayer(member);
            if (p != null) p.sendMessage("Your crew has been disbanded.");
        }
        // Delete crew from DB
        // (Assuming you add a deleteCrew method to CrewDAO)
        dao.deleteCrew(crew.getId());
        // Delete island
        if (crew.getIslandWorldName() != null && aspApi != null) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                SlimeWorldInstance instance = aspApi.getLoadedWorld(crew.getIslandWorldName());
                if (instance != null) {
                    try {
                        Bukkit.unloadWorld(instance.getBukkitWorld(), true);
                    } catch (Exception e) {
                        logger.severe("Failed to unload island: " + e.getMessage());
                    }
                }
                try {
                    plugin.getLoader().deleteWorld(crew.getIslandWorldName());
                } catch (Exception e) {
                    logger.severe("Failed to delete island: " + e.getMessage());
                }
            });
        }
    }

    public void checkAndUnloadIslandIfEmpty(String worldName) {
        if (aspApi == null || worldName == null) return;
        org.bukkit.World world = Bukkit.getWorld(worldName);
        if (world == null) return;
        // Only unload if it's a crew island
        if (!worldName.startsWith("crew_island_")) return;
        // If no players left in this world, save and unload
        if (world.getPlayerCount() == 1) {
            SlimeWorldInstance instance = aspApi.getLoadedWorld(worldName);
            if (instance != null) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    try {
                        aspApi.saveWorld(instance);
                        Bukkit.unloadWorld(instance.getBukkitWorld(), true);
                        logger.info("Unloaded empty crew island: " + worldName);
                    } catch (Exception e) {
                        logger.severe("Failed to save/unload crew island: " + e.getMessage());
                    }
                });
            }
        }
    }
}
