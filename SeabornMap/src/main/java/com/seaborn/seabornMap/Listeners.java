package com.seaborn.seabornMap;

import net.kyori.adventure.text.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.item.ItemWorldMap;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.saveddata.maps.WorldMap;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R3.CraftServer;
import org.bukkit.craftbukkit.v1_21_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_21_R3.map.CraftMapView;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.lang.reflect.*;

import static org.bukkit.Bukkit.getServer;

public class Listeners implements Listener {

    private int lastUpdate = 0;

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {

        int tick = getServer().getCurrentTick();
        int diff = tick - lastUpdate;

        if (diff < 40) return;

        lastUpdate = tick;

        ItemStack item = event.getPlayer().getInventory().getItemInMainHand();

        if (item.getType() != Material.FILLED_MAP) return;

        Player player = event.getPlayer();

        MapMeta mapMeta = (MapMeta) item.getItemMeta();
        MapView mapView = mapMeta.getMapView();

        if (mapView == null) return;

        mapView.setCenterX(player.getLocation().getBlockX());
        mapView.setCenterZ(player.getLocation().getBlockZ());

        mapView.setScale(MapView.Scale.FAR);
        mapView.setTrackingPosition(true);

        for (MapRenderer renderer : mapView.getRenderers()) {
            mapView.removeRenderer(renderer);
        }

        // Rendering
        //new MapUpdater(Bukkit.getWorld("world")).update(mapView);

        //final WorldMap worldMap = (WorldMap) ((MinecraftServer) getServer()).world.get(0).worldMaps.get(WorldMap.class, "map_" + event.getMap().getId());



        /*Field field = null;
        try {
            field = CraftMapView.class.getDeclaredField("worldMap");
            field.setAccessible(true);
            WorldMap worldMap = (WorldMap) field.get((CraftMapView) mapView);


            //((ItemWorldMap) Items.sR).a(((CraftWorld) mapView.getWorld()).getHandle(), ((CraftPlayer) player).getHandle(), worldMap);//more efficient than the updater (shows the area around the player only, not all loaded chunks)
            ((ItemWorldMap) Items.sR).a(((CraftWorld) mapView.getWorld()).getHandle(), (), worldMap);

            mapView.addRenderer(new Renderer(worldMap));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }*/


        /*for (MapRenderer renderer : mapView.getRenderers()) {
            renderer.render(mapView, );
        }*/

        mapMeta.setMapView(mapView);
        item.setItemMeta(mapMeta);

        //getServer().broadcast(Component.text("Updating map!"));

    }

}
