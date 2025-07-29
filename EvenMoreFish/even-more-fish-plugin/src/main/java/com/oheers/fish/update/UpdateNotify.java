package com.oheers.fish.update;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.messages.ConfigMessage;
import com.oheers.fish.permissions.AdminPerms;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateNotify implements Listener {

    @EventHandler
    // informs admins with emf.admin permission that the plugin needs updating
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!EvenMoreFish.getInstance().isUpdateAvailable()) {
            return;
        }

        if (!event.getPlayer().hasPermission(AdminPerms.UPDATE_NOTIFY)) {
            return;
        }

        ConfigMessage.ADMIN_UPDATE_AVAILABLE.getMessage().send(event.getPlayer());
    }
}