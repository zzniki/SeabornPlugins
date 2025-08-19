package com.seaborn.seabornCore.events;

import com.seaborn.seabornCore.db.dao.PlayerDataDAO;
import com.seaborn.seabornCore.db.model.PlayerData;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerDataLoadEvent extends Event {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private PlayerData data;

    public PlayerDataLoadEvent(PlayerData data) {
        this.data = data;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public PlayerData getData() {
        return data;
    }

    public void setData(PlayerData data) {
        this.data = data;
    }
}
