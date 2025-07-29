package com.seaborn.seabornFishing;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class GameSchedule {

    private List<Minigame> scheduledGames = new ArrayList<>();
    private Player player;
    private int currentPos = -1;
    private PlayerFishEvent initialEvent = null;

    public GameSchedule(Player player) {
        this.player = player;
    }

    public void addGame(Minigame game, SeabornFishing plugin, float difficulty) {

        try {

            Minigame newGame = game.clone().setTargetPlayer(player).setSchedule(this).setDifficulty(difficulty);
            getServer().getPluginManager().registerEvents(newGame, plugin);

            this.scheduledGames.add(newGame);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public List<Minigame> getScheduledGames() {
        return this.scheduledGames;
    }

    public boolean hasNextGame() {
        return currentPos + 1 < this.scheduledGames.size();
    }

    public Minigame advanceGame() {
        currentPos++;
        return this.scheduledGames.get(currentPos);
    }

    public float getTotalTime() { // In seconds
        float total = 0f;
        for (Minigame game : this.scheduledGames) {
            total += game.getTimeTake();
        }
        return total;
    }

    public void setInitialEvent(PlayerFishEvent event) {
        this.initialEvent = event;
    }

    public PlayerFishEvent getInitialEvent() {
        return this.initialEvent;
    }

}
