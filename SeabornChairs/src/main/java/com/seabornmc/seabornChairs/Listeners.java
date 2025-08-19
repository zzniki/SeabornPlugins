package com.seabornmc.seabornChairs;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDismountEvent;

import static org.bukkit.Bukkit.getServer;

public class Listeners implements Listener {

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        SeabornChairs.getChairManager().onDismount(event.getDismounted());
    }

}
