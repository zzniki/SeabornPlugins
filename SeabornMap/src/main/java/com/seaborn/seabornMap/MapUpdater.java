package com.seaborn.seabornMap;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemWorldMap;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.saveddata.maps.WorldMap;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_21_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R3.map.CraftMapView;
import org.bukkit.map.MapView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.UUID;

public final class MapUpdater extends EntityHuman {
    public static final UUID ID = UUID.randomUUID();
    public static final String NAME = "_____MapUpdater_____";

    private net.minecraft.world.level.World world;

    public MapUpdater(World world) {

        super(((CraftWorld) world).getHandle(),new BlockPosition(0, Bukkit.getWorld("world").getHighestBlockYAt(0, 0),0),0, new GameProfile(ID, NAME));
        this.world = ((CraftWorld) world).getHandle();

    }

    public void update(MapView mapView) {

        ArrayList<Chunk> chunks = new ArrayList<>();

        if (mapView == null) {
            throw new IllegalArgumentException("mapView cannot be null");
        }

        if (((CraftWorld) mapView.getWorld()).getHandle() != this.world) {
            throw new IllegalArgumentException("world of mapView cannot be different");
        }

        Chunk center = mapView.getWorld().getChunkAt(mapView.getCenterX() >> 4, mapView.getCenterZ() >> 4);

        //We loop to load the region 32*32 around the middle chunk
        for(int x = center.getX() -16; x <= center.getX() + 16; x++){

            for(int z = center.getZ() -16; z <= center.getZ() + 16; z++){
                Chunk chunk = mapView.getWorld().getChunkAt(x, z);
                chunk.addPluginChunkTicket(SeabornMap.getInstance());
                chunks.add(chunk);
            }
        }

        // We render the map
        Bukkit.getScheduler().scheduleSyncDelayedTask(SeabornMap.getInstance(), ()->{

            try {
                Field field = CraftMapView.class.getDeclaredField("worldMap");
                field.setAccessible(true);
                WorldMap worldMap = (WorldMap) field.get(mapView);

                int size = 128 << mapView.getScale().getValue();

                for (int x = mapView.getCenterX() - size / 2; x <= mapView.getCenterX() + size / 2; x += 16) {
                    for (int z = mapView.getCenterZ() - size / 2; z <= mapView.getCenterZ() + size / 2; z += 16) {

                        o(x, 1., z);
                        ((ItemWorldMap) Items.sR).a(this.world, this, worldMap);
                    }
                }
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }

            // We unload the chunk loaded previously
            for(Chunk c : chunks){
                c.removePluginChunkTicket(SeabornMap.getInstance());
            }

        }, 5L);


    }

    @Override
    public boolean Z_() {
        return false;
    }

    @Override
    public boolean b() {
        return false;
    }
}