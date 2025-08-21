package com.seaborn.seabornFishing.minigames;

import com.seaborn.seabornFishing.Minigame;
import com.seaborn.seabornFishing.SeabornFishing;
import com.seaborn.seabornFishing.Utils;
import com.seaborn.seabornFishing.events.GameEndEvent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import org.w3c.dom.Text;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static org.bukkit.Bukkit.getServer;

public class NumbersGame extends Minigame {

    private BukkitTask gameTask;
    private int targetSlot;
    private int maxPresses = 5;
    private int currentPresses = 0;
    private int shown = 0;
    private FishHook hook;
    private boolean hasPressed = false;

    private final Map<String, String> keyMap = new HashMap<String, String>()
    {{
        put("1", "\ua011");
        put("2", "\ua012");
        put("3", "\ua013");
        put("4", "\ua014");
        put("5", "\ua015");
        put("6", "\ua016");
        put("7", "\ua017");
        put("8", "\ua018");
        put("9", "\ua019");
    }};

    private final Map<String, String> pressedKeyMap = new HashMap<String, String>()
    {{
        put("1", "\ua021");
        put("2", "\ua022");
        put("3", "\ua023");
        put("4", "\ua024");
        put("5", "\ua025");
        put("6", "\ua026");
        put("7", "\ua027");
        put("8", "\ua028");
        put("9", "\ua029");
    }};

    public NumbersGame(SeabornFishing plugin) {
        super("numbers", "Numbers", plugin);
    }


    // Util functions

    public int getNewSlot() {

        Random random = new Random();

        int heldSlot = this.getTargetPlayer().getInventory().getHeldItemSlot();
        int newSlot = heldSlot;

        while (newSlot == heldSlot || newSlot == targetSlot) {
            newSlot = random.nextInt(0, 9);
        }

        return newSlot;

    }


    public void sendKeyTitle(String key, boolean pressed) {

        String finalKey = pressed ? pressedKeyMap.get(key) : keyMap.get(key);

        this.getTargetPlayer().showTitle(
                Title.title(
                        Component.text(finalKey).font(Key.key("seaborn_images", "images")),
                        Component.text(pressed ? "Nice!" : "Press the key"),
                        Title.Times.times(
                                Duration.ofSeconds(0),
                                Duration.ofSeconds(3),
                                Duration.ofSeconds(1)
                        )
                )
        );
    }

    // Game functions

    @Override
    public void cleanup() {
        super.cleanup();
        PlayerItemHeldEvent.getHandlerList().unregister(this);
    }

    @Override
    public void endGame(boolean success) {
        super.endGame(success);

        gameTask.cancel();
    }

    public void winGame() {

        this.endGame(true);

    }

    public void loseGame(TextComponent message) {

        this.endGame(false);

        this.hook.remove();

        this.getTargetPlayer().showTitle(Title.title(
                Component.text("\ua002").font(Key.key("seaborn_images", "icons")),
                message,
                Title.Times.times(
                        Duration.ofMillis(500),
                        Duration.ofSeconds(1),
                        Duration.ofMillis(500)
                )
        ));

        Utils.playSoundEffect(this.getTargetPlayer(), Key.key("minecraft", "block.anvil.land"));
    }

    public float getSinglePressTime() {
        return 1.9f / this.getDifficulty();
    }

    @Override
    public float getTimeTake() {
        return this.getSinglePressTime() * this.maxPresses;
    }

    public void onGameUpdate() {

        if (!this.isRunning()) return;

        if (shown >= maxPresses) {
            this.loseGame(Component.text(ChatColor.RED + "The fish escaped!"));
            return;
        }

        if (currentPresses < shown) {
            this.loseGame(Component.text(ChatColor.RED + "Too slow!"));
            return;
        }

        this.hasPressed = false;
        this.targetSlot = this.getNewSlot();
        this.sendKeyTitle(String.valueOf(this.targetSlot + 1), false);

        shown++;

    }

    @Override
    public void onGameStart(PlayerFishEvent event) {
        super.onGameStart(event);

        Player player = this.getTargetPlayer();
        this.targetSlot = -99;

        BukkitScheduler bukkitScheduler = Bukkit.getScheduler();
        long tps = Math.round(getServer().getTPS()[1]);

        hook = event.getHook();
        gameTask = bukkitScheduler.runTaskTimer(this.getPlugin(), this::onGameUpdate, 0, Math.round(this.getSinglePressTime() * tps));

    }

    @Override
    public void onLineReelIn(PlayerFishEvent event) {
        this.loseGame(Component.text(""));
    }

    @EventHandler
    public void onHeldItemChange(PlayerItemHeldEvent event) {

        if (!this.isRunning()) return;
        if (!this.isTargetPlayer(event.getPlayer())) return;

        Player player = event.getPlayer();

        event.setCancelled(true);

        int newSlot = event.getNewSlot();

        if (!hasPressed) {
            if (newSlot == targetSlot) {

                currentPresses++;

                if (currentPresses < maxPresses) {
                    // Continue game
                    this.sendKeyTitle(String.valueOf(this.targetSlot + 1), true);
                    Utils.playSoundEffect(player, Key.key("minecraft", "block.note_block.pling"));
                    hasPressed = true;
                } else {
                    // Win game
                    this.winGame();
                }
            } else {
                // Lose game
                this.loseGame(Component.text(ChatColor.RED + "Oops!"));
            }
        }


    }

}
