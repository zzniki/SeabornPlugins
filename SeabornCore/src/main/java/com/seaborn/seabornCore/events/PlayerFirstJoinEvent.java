package com.seaborn.seabornCore.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerFirstJoinEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private Player player;
    private PlayerJoinEvent joinEvent;

    public PlayerFirstJoinEvent(Player player, PlayerJoinEvent joinEvent) {
        this.player = player;
        this.joinEvent = joinEvent;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public Player getPlayer() {
        return player;
    }

    public PlayerJoinEvent getJoinEvent() {
        return joinEvent;
    }
}
