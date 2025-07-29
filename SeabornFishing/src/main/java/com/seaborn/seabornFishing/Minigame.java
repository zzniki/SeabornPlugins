package com.seaborn.seabornFishing;

import com.seaborn.seabornFishing.events.GameEndEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

import static org.bukkit.Bukkit.getServer;

public abstract class Minigame implements Listener, Cloneable {

    private String id;
    private String name;
    private Player targetPlayer = null;
    private float difficulty = 1f;
    private boolean running = false;
    private GameSchedule schedule = null;

    private SeabornFishing plugin;

    public Minigame(String id, String name, SeabornFishing plugin) {
        this.id = id;
        this.name = name;
        this.plugin = plugin;
    }

    public Minigame(Minigame minigame, Player player) {
        this.id = minigame.getId();
        this.name = minigame.getName();
        this.plugin = minigame.getPlugin();
        this.targetPlayer = player;
    }

    public Minigame setTargetPlayer(Player player) {
        this.targetPlayer = player;
        return this;
    }

    public Minigame setDifficulty(float diff) {
        this.difficulty = diff;
        return this;
    }

    public Minigame setSchedule(GameSchedule schedule) {
        this.schedule = schedule;
        return this;
    }

    public void setRunning(boolean r) {
        this.running = r;
    }

    public float getDifficulty() {
        return this.difficulty;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public boolean isRunning() {
        return running;
    }

    public SeabornFishing getPlugin() {
        return this.plugin;
    }

    public GameSchedule getSchedule() {
        return this.schedule;
    }

    @Override
    public Minigame clone() throws CloneNotSupportedException {
        return (Minigame) super.clone();
    }

    public float getTimeTake() { return 0f; }
    public void onGameStart(PlayerFishEvent event) {}
    public void onLineCast(PlayerFishEvent event) {}
    public void onFishApproach(PlayerFishEvent event) {}
    public void onFishBite(PlayerFishEvent event) {}
    public void onLineReelIn(PlayerFishEvent event) {}
    public void onFishCaught(PlayerFishEvent event) {}

    public boolean isTargetPlayer(Player player) {
        if (this.targetPlayer == null) return false;
        return targetPlayer.getUniqueId().toString().equals(player.getUniqueId().toString());
    }

    public Player getTargetPlayer() { return this.targetPlayer; }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (!running) return;
        if (targetPlayer == null) return;
        if (!this.isTargetPlayer(event.getPlayer())) return;

        switch (event.getState()) {
            case PlayerFishEvent.State.FISHING:
                this.onLineCast(event);
                break;
            case PlayerFishEvent.State.LURED:
                this.onFishApproach(event);
                break;
            case PlayerFishEvent.State.BITE:
                this.onFishBite(event);
                break;
            case PlayerFishEvent.State.CAUGHT_FISH:
                this.onFishCaught(event);
                break;
            case PlayerFishEvent.State.REEL_IN:
                this.onLineReelIn(event);
                break;
        }
    }

    public void cleanup() {
        PlayerFishEvent.getHandlerList().unregister(this);
    }

    public void endGame(boolean success) {
        this.setRunning(false);

        GameEndEvent endEvent = new GameEndEvent(this, this.getSchedule(), success);
        getServer().getPluginManager().callEvent(endEvent);

        this.cleanup();
    }

}
