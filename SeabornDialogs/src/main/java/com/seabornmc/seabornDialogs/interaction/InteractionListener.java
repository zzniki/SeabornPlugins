package com.seabornmc.seabornDialogs.interaction;

import com.seabornmc.seabornDialogs.Display;
import com.seabornmc.seabornDialogs.SeabornDialogs;
import com.seabornmc.seabornDialogs.dialogs.Dialog;
import com.seabornmc.seabornDialogs.dialogs.DialogManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class InteractionListener implements Listener {

    private List<Action> clickActions = List.of(
            Action.LEFT_CLICK_AIR,
            Action.LEFT_CLICK_BLOCK,
            Action.RIGHT_CLICK_AIR,
            Action.RIGHT_CLICK_BLOCK
    );

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        SeabornDialogs.getPlugin().getServer().getScheduler().runTaskLater(SeabornDialogs.getPlugin(), () -> {
            SeabornDialogs.getDisplayManager().updateLookingDisplay(event.getPlayer());
        }, 1);

        Dialog dialog = SeabornDialogs.getDialogManager().getDialog(event.getPlayer());
        if (dialog == null) return;

        if (event.getTo().distance(dialog.getLocation()) > 6) {
            SeabornDialogs.getDialogManager().deleteDialog(dialog);
        }

    }

    @EventHandler
    public void onLeftClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Display lookingDisplay = SeabornDialogs.getDisplayManager().getLookingDisplay(player);

        if (lookingDisplay == null) return;
        if (!clickActions.contains(event.getAction())) return;

        lookingDisplay.callInteract(player);
        event.setCancelled(true);

    }

}
