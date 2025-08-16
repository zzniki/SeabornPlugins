package com.seaborn.seabornFishing.dialogs;

import com.seaborn.seabornFishing.SeabornFishing;
import com.seaborn.seabornFishing.SellManager;
import com.seabornmc.seabornDialogs.SeabornDialogs;
import com.seabornmc.seabornDialogs.dialogs.OptionDialog;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class FishmongerDialog {

    public static void create(Player player, NPC npc) {

        Location entityLoc = npc.getEntity().getLocation();
        Vector npcDir = player.getLocation().toVector().subtract(entityLoc.toVector());
        entityLoc.setDirection(npcDir);

        Vector forward = npcDir.normalize();
        Vector right = new Vector(forward.getZ(), 0, -forward.getX());

        double offset = 1.5;
        Location rightSideLoc = entityLoc.clone().add(right.multiply(offset)).add(0, 1.5, 0);

        // Make the hologram face the player
        /*double dx = player.getLocation().getX() - rightSideLoc.getX();
        double dz = player.getLocation().getZ() - rightSideLoc.getZ();
        float yaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        rightSideLoc.setYaw(yaw);
        rightSideLoc.setPitch(0);*/

        OptionDialog dialog = new OptionDialog(
                rightSideLoc,
                player,
                List.of(
                        "I'd like to sell this",
                        "I'd like to sell my inventory"
                )
        );
        SeabornDialogs.getDialogManager().registerDialog(dialog);

        dialog.onSelect((index) -> {

            if (index == 0) {
                float money = SellManager.sellItemInHand(player);
                if (money != -1f) {
                    SellManager.displaySellNotification(player, money);
                }
            } else if (index == 1) {
                float money = SellManager.sellInventory(player);
                if (money != -1f) {
                    SellManager.displaySellNotification(player, money);
                }
            }

            SeabornDialogs.getDialogManager().deleteDialog(dialog);

            return null;
        });

        dialog.spawn();

        player.playSound(Sound.sound().type(Key.key("minecraft", "ui.hud.bubble_pop")).volume(2f).build());

    }

}
