package com.seaborn.seabornFishing.emote;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import net.kyori.adventure.text.Component;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Transformation;
import org.bukkit.Material;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.joml.AxisAngle4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.bukkit.Bukkit.getServer;

public class EmoteListener implements Listener {

    public List<Material> materialWhitelist = new ArrayList<>() {{

        add(Material.COD);
        add(Material.SALMON);
        add(Material.TROPICAL_FISH);

    }};

    public Map<Player, ItemDisplay> displayMap = new HashMap<Player, ItemDisplay>();

    public void startEmote(Player player, ItemStack item) {
        float length = NBT.get(item, nbt -> {
            ReadableNBT compound = nbt.getCompound("evenmorefish");
            if (compound ==  null) return 10f;
            return compound.getFloat("emf-fish-length");
        });

        float size = length / 30f;

        player.getWorld().spawn(player.getLocation(), ItemDisplay.class, (display) -> {
            display.setItemStack(item);
            display.setInvulnerable(true);
            display.setTeleportDuration(1);

            AxisAngle4f axisAngleRotMat = new AxisAngle4f((float) Math.toRadians(20), new Vector3f(0, 0, 1));
            Transformation transformation = new Transformation(
                    new Vector3f(0, 0, 0),
                    axisAngleRotMat,
                    new Vector3f(1, 1, 1).mul(size),
                    axisAngleRotMat
            );

            display.setTransformation(transformation);

            display.teleport(EmoteFunc.getEmoteLocation(player));
            displayMap.put(player, display);
        });
    }

    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent event) {
        ItemStack offHandItem = event.getOffHandItem();
        ItemStack mainHandItem = event.getMainHandItem();
        Player player = event.getPlayer();

        if (displayMap.containsKey(player) && displayMap.get(player).getItemStack().equals(mainHandItem) && !materialWhitelist.contains(offHandItem.getType())) {
            // Disable emote
            displayMap.get(player).remove();
            displayMap.remove(player);
        } else if (materialWhitelist.contains(offHandItem.getType())) {
            if (displayMap.containsKey(player)) {
                displayMap.get(player).remove();
            }

            startEmote(player, offHandItem);
        }

    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            if (displayMap.containsKey(player)) {
                if (event.getCurrentItem().equals(displayMap.get(player).getItemStack())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (displayMap.containsKey(player)) {
            displayMap.get(player).teleport(EmoteFunc.getEmoteLocation(player));
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        if (displayMap.containsKey(event.getPlayer())) {
            displayMap.get(event.getPlayer()).remove();
            displayMap.remove(event.getPlayer());
        }
    }

}
