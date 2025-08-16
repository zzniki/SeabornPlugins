package com.seaborn.seabornFishing.minigames;

import com.seaborn.seabornFishing.Minigame;
import com.seaborn.seabornFishing.SeabornFishing;
import com.seaborn.seabornFishing.rewards.FishItemReward;
import com.seaborn.seabornFishing.rewards.FishReward;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class GameSchedule implements Listener {

    private List<Minigame> scheduledGames = new ArrayList<>();
    private Player player;
    private int currentPos = -1;
    private PlayerFishEvent initialEvent = null;
    private FishReward reward;

    public GameSchedule(Player player, FishReward reward) {
        this.player = player;
        this.reward = reward;
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

    private void cleanup() {
        PlayerFishEvent.getHandlerList().unregister(this);
        // Unregister all minigames
        for (Minigame game : this.scheduledGames) {
            HandlerList.unregisterAll(game);
        }
    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {
        if (!event.getPlayer().equals(this.player)) return; // Only handle events for this player

        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            Item item = (Item) event.getCaught();
            if (item == null) return;

            if (this.reward instanceof FishItemReward) {
                item.setItemStack(((FishItemReward) this.reward).getItem());
            } else {
                this.reward.giveToPlayer(player);
            }

            this.cleanup();
        }
    }

}
