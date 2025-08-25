package com.seaborn.seabornCore.events;

import com.seaborn.seabornCore.db.model.PlayerData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PeriodicSaveEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    public PeriodicSaveEvent() {
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

}
