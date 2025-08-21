package com.seaborn.seabornCrews.service;

import com.infernalsuite.asp.api.AdvancedSlimePaperAPI;
import com.infernalsuite.asp.api.exceptions.UnknownWorldException;
import com.infernalsuite.asp.api.loaders.SlimeLoader;
import com.infernalsuite.asp.api.world.SlimeWorld;
import com.infernalsuite.asp.api.world.SlimeWorldInstance;
import com.infernalsuite.asp.api.world.properties.SlimeProperties;
import com.infernalsuite.asp.api.world.properties.SlimePropertyMap;
import com.seaborn.seabornCrews.SeabornCrews;
import com.seaborn.seabornCrews.Utils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class IslandService {

    public static final String PREFIX = "crew_island_";

    private AdvancedSlimePaperAPI api;
    private SlimeLoader loader;

    public IslandService(AdvancedSlimePaperAPI api, SlimeLoader loader) {
        this.api = api;
        this.loader = loader;
    }

    private SlimePropertyMap getPropertyMap() {
        SlimePropertyMap props = new SlimePropertyMap();
        props.setValue(SlimeProperties.ENVIRONMENT, "normal");
        props.setValue(SlimeProperties.WORLD_TYPE, "DEFAULT");
        props.setValue(SlimeProperties.PVP, true);
        props.setValue(SlimeProperties.ALLOW_MONSTERS, true);
        props.setValue(SlimeProperties.ALLOW_ANIMALS, true);
        props.setValue(SlimeProperties.DEFAULT_BIOME, "minecraft:plains");

        return props;
    }

    public CompletableFuture<String> createIsland(int crewId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String worldName = PREFIX + String.valueOf(crewId);

                // ASYNC: Read the template world
                SlimeWorld template = api.readWorld(loader, "template", false, this.getPropertyMap());
                return template.clone(worldName, loader);

            } catch (Exception e) {
                SeabornCrews.getInstance().getLogger().severe("Failed to read template world: " + e.getMessage());
                return null;
            }

        }).thenCompose((slimeWorld) -> {
            if (slimeWorld == null) return CompletableFuture.completedFuture(null);

            CompletableFuture<String> future = new CompletableFuture<>();

            // SYNC: Load the world
            Bukkit.getScheduler().runTask(SeabornCrews.getInstance(), () -> {
                try {
                    SlimeWorldInstance instance = api.loadWorld(slimeWorld, true);
                    World bukkitWorld = instance.getBukkitWorld();

                    api.saveWorld(instance);
                    future.complete(bukkitWorld.getName());
                } catch (Exception e) {
                    SeabornCrews.getInstance().getLogger().severe("Failed to load world: " + e.getMessage());
                    future.complete(null);
                }
            });

            return future;
        });
    }

    public CompletableFuture<SlimeWorldInstance> loadWorld(String worldId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return api.readWorld(loader, worldId, false, this.getPropertyMap());
            } catch (Exception e){
                SeabornCrews.getInstance().getLogger().severe("Failed to read world " + worldId + ": " + e.getMessage());
                return null;
            }
        }).thenCompose((slimeWorld -> {
            if (slimeWorld == null) {
                return CompletableFuture.completedFuture(null);
            }

            CompletableFuture<SlimeWorldInstance> future = new CompletableFuture<>();

            Bukkit.getScheduler().runTask(SeabornCrews.getInstance(), () -> {
                try {
                    SlimeWorldInstance instance = api.loadWorld(slimeWorld, true);
                    future.complete(instance);
                } catch (Exception e) {
                    SeabornCrews.getInstance().getLogger().severe("Failed to load world " + worldId + ": " + e.getMessage());
                    future.complete(null);
                }
            });

            return future;

        }));
    }

    public void teleportToIsland(Player player, String worldId) {
        SlimeWorldInstance worldInstance = api.getLoadedWorld(worldId);

        player.sendMessage(Utils.formatText("<green>Teleporting..."));

        if (worldInstance != null) {
            player.teleport(worldInstance.getBukkitWorld().getSpawnLocation());
            return;
        }

        this.loadWorld(worldId).thenAccept(slimeWorldInstance -> {

            if (slimeWorldInstance == null) {
                player.sendMessage(Utils.formatText("<red>Could not load island. Please report this error to our staff team."));
                return;
            }

            Bukkit.getScheduler().runTask(SeabornCrews.getInstance(), () -> {
                player.teleport(slimeWorldInstance.getBukkitWorld().getSpawnLocation());
            });
        });

    }

    public void unloadIsland(String worldId) {
        SlimeWorldInstance world = api.getLoadedWorld(worldId);

        if (world == null) {
            SeabornCrews.getInstance().getLogger().warning("World is not loaded!");
            return;
        }

        World bukkitWorld = world.getBukkitWorld();
        try {
            api.saveWorld(world);
            Bukkit.unloadWorld(bukkitWorld, true);
        } catch (Exception e) {
            SeabornCrews.getInstance().getLogger().severe("Failed to save/unload crew island: " + e.getMessage());
        }

    }

    public void unloadIslandIfEmpty(String worldId) {
        SlimeWorldInstance world = api.getLoadedWorld(worldId);

        if (world == null) {
            SeabornCrews.getInstance().getLogger().warning("World is not loaded!");
            return;
        }

        World bukkitWorld = world.getBukkitWorld();
        if (bukkitWorld.getPlayerCount() == 0) {
            try {
                api.saveWorld(world);
                Bukkit.unloadWorld(bukkitWorld, true);
            } catch (Exception e) {
                SeabornCrews.getInstance().getLogger().severe("Failed to save/unload crew island: " + e.getMessage());
            }
        }
    }

    public void saveIsland(String worldId) {
        SlimeWorldInstance world = api.getLoadedWorld(worldId);

        if (world == null) {
            SeabornCrews.getInstance().getLogger().warning("World is not loaded!");
            return;
        }

        World bukkitWorld = world.getBukkitWorld();
        try {
            api.saveWorld(world);
        } catch (Exception e) {
            SeabornCrews.getInstance().getLogger().severe("Failed to save crew island " + worldId + " : " + e.getMessage());
        }
    }

    public CompletableFuture<Boolean> deleteIsland(String worldId) {

        return CompletableFuture.supplyAsync(() -> {
            try {
                loader.deleteWorld(worldId);
                return true;
            } catch (UnknownWorldException e) {
                return false;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

}
