package com.seabornmc.seabornCompass;

import com.maximde.hologramlib.hologram.RenderMode;
import com.maximde.hologramlib.hologram.TextHologram;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Marker {

    private double markerDist = 20;

    private TextHologram holo;
    private Location location;
    private Location displayLoc;
    private Player player;

    private double distance = 0;

    public Marker(Location location, Player player) {
        this.location = location;
        this.player = player;

        this.updateDisplayLoc();
        this.holo = new TextHologram(UUID.randomUUID().toString(), RenderMode.VIEWER_LIST)
                .setText(this.getText())
                .setSeeThroughBlocks(true)
                .setViewRange(Double.MAX_VALUE)
                .setScale(2f, 2f ,2f)
                .setShadow(false)
                .setTeleportDuration(5)
                .addViewer(player);
    }

    private Component getText() {
        String distanceStr = String.valueOf(Math.round(this.distance));
        return Component.text("\ua004\n" + distanceStr + "M").font(Key.key("seaborn_images", "icons"));
    }

    private void updateDisplayLoc() {

        this.distance = this.player.getLocation().distance(this.location);

        if (distance > markerDist) {
            this.displayLoc = this.player.getEyeLocation().add(
                    this.location.toVector().subtract(this.player.getEyeLocation().toVector())
                        .normalize()
                        .multiply(markerDist).toLocation(player.getLocation().getWorld())
            );
        } else {
            this.displayLoc = this.location;
        }
    }

    public void spawn() {
        SeabornCompass.getHoloManager().spawn(
                this.holo,
                this.displayLoc
        );
    }

    public void despawn() {
        SeabornCompass.getHoloManager().remove(this.holo);
    }

    public void update() {
        this.updateDisplayLoc();

        this.holo
                .teleport(this.displayLoc)
                .setText(this.getText())
                .update();

    }

}
