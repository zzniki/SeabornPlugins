package com.seaborn.seabornMap;

import net.kyori.adventure.text.Component;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.item.ItemWorldMap;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.saveddata.maps.WorldMap;
import org.bukkit.entity.Player;
import org.bukkit.map.*;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;

import static org.bukkit.Bukkit.getServer;

public class Renderer extends MapRenderer {

    WorldMap worldMap;

    public Renderer(WorldMap worldMap) {
        this.worldMap = worldMap;
    }

    @Override
    public void render(@NotNull MapView mapView, @NotNull MapCanvas canvas, @NotNull Player player) {

        for (int x = 0; x < 128; ++x) {
            for (int y = 0; y < 128; ++y) {
                canvas.setPixel(x, y, worldMap.g[y * 128 + x]);
                //canvas.setPixelColor(x, y, Color.BLACK);
            }
        }
    }
}
