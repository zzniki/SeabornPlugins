package com.seabornmc.seabornChairs;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;

public class Chair {

    private Location location;
    private Location displayLocation;
    private Location standLocation;
    private Player player;

    private boolean spawned = false;
    private ItemDisplay display;
    private ArmorStand stand;

    public Chair(Player player) {
        this.player = player;
        this.location = player.getLocation();

        this.displayLocation = this.location.clone().add(0, 0.5, 0);
        this.displayLocation.setRotation(this.location.getYaw(), 0);

        this.standLocation = this.displayLocation.clone().add(0, -2, 0);
    }

    public void spawn() {

        ItemStack item = new ItemStack(Material.PAPER);
        item.editMeta((meta) -> {
            meta.setItemModel(new NamespacedKey("seaborn_items", "chair"));
        });

        player.getWorld().spawn(this.displayLocation, ItemDisplay.class, (display) -> {
            display.setInvulnerable(true);
            display.setSilent(true);

            display.setItemStack(item);

            Transformation transform = display.getTransformation();
            Quaternionf newRoll = new Quaternionf().rotateLocalY((float) Math.toRadians(-90));

            Transformation newTransform = new Transformation(
                    transform.getTranslation(),
                    transform.getLeftRotation(),
                    transform.getScale(),
                    newRoll
            );

            display.setTransformation(newTransform);

            this.display = display;
        });

        player.getWorld().spawn(this.standLocation, ArmorStand.class, (stand) -> {

            stand.setInvulnerable(true);
            stand.setSilent(true);
            stand.setCanMove(false);
            stand.setVisible(false);
            stand.setCollidable(false);

            stand.addPassenger(this.player);

            this.stand = stand;

        });

        this.spawned = true;

    }

    public void despawn() {

        if (this.spawned) {

            if (this.stand != null) {
                for (Entity passenger : this.stand.getPassengers()) {
                    this.stand.removePassenger(passenger);
                    passenger.teleport(this.location);
                }
                this.stand.remove();
            }

            if (this.display != null) this.display.remove();


            this.spawned = false;
        }

    }



    // Getsets


    public boolean isSpawned() {
        return spawned;
    }

    public ItemDisplay getDisplay() {
        return display;
    }

    public ArmorStand getStand() {
        return stand;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
