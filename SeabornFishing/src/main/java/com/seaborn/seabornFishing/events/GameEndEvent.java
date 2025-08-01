package com.seaborn.seabornFishing.events;

import com.seaborn.seabornFishing.minigames.GameSchedule;
import com.seaborn.seabornFishing.Minigame;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameEndEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    private Minigame game;
    private GameSchedule schedule;
    private boolean success;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public GameEndEvent(Minigame game, GameSchedule schedule, boolean success) {
        this.game = game;
        this.schedule = schedule;
        this.success = success;
    }

    public Minigame getGame() { return this.game; }
    public GameSchedule getSchedule() { return this.schedule; }
    public boolean wasSuccessful() { return this.success; }

}
