package com.seaborn.seabornFishing;

import com.seaborn.seabornFishing.events.GameEndEvent;
import com.seaborn.seabornFishing.minigames.GameSchedule;
import com.seaborn.seabornFishing.minigames.GameScheduler;
import com.seaborn.seabornFishing.rewards.FishItemReward;
import com.seaborn.seabornFishing.rewards.FishReward;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.minecraft.world.EnumHand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_21_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.*;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;
import static org.bukkit.Bukkit.getServer;

public class Listeners implements Listener {

    private GameScheduler scheduler;
    private SeabornFishing plugin;

    public Listeners(GameScheduler scheduler, SeabornFishing plugin) {
        this.scheduler = scheduler;
        this.plugin = plugin;
    }

    public void scheduleNextGame(GameSchedule schedule) {
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {

            Minigame game = schedule.advanceGame();
            game.setRunning(true);
            game.onGameStart(schedule.getInitialEvent());

        }, (long) getServer().getTPS()[0]);
    }

    private void doRightClick(Player player, EnumHand hand) {
        Object handle;
        try {
            handle = player.getClass().getMethod("getHandle").invoke(player);

            // Get the item in hand
            Method getItemInHand = handle.getClass().getMethod("getItemInHand", hand.getClass());
            Object item = getItemInHand.invoke(handle, hand);

            // Convert to Bukkit ItemStack if needed
            ItemStack bukkitItem = CraftItemStack.asBukkitCopy((net.minecraft.world.item.ItemStack) item);

            // Call useItem()
            Object gameMode = handle.getClass().getField("gameMode").get(handle);
            Method level = handle.getClass().getMethod("level");
            Method useItem = gameMode.getClass().getMethod("useItem", handle.getClass(), level.getReturnType(), item.getClass(), hand.getClass());
            useItem.invoke(gameMode, handle, level.invoke(handle), item, hand);

            // Call swing()
            Method swing = handle.getClass().getMethod("swing", hand.getClass(), boolean.class);
            swing.invoke(handle, hand, true);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @EventHandler
    public void onFish(PlayerFishEvent event) {

        PlayerFishEvent.State state = event.getState();

        // On line cast
        if (state == PlayerFishEvent.State.FISHING) {

            Player player = event.getPlayer();

            ItemStack rod = player.getInventory().getItemInMainHand();
            if (rod.getType() != Material.FISHING_ROD) {
                event.setCancelled(true);
                return;
            }

            Fish selectedFish = FishSelector.selectFish(event.getHook(), player, rod);
            if (selectedFish == null) {
                getLogger().log(Level.SEVERE, "Could not find a valid fish for " + player.getName());
                event.setCancelled(true);
                return;
            }

            FishItemReward reward = new FishItemReward(selectedFish, player);
            GameSchedule schedule = this.scheduler.createSchedule(player, reward);
            getServer().getPluginManager().registerEvents(schedule, this.plugin); // Register schedule listeners

            event.getHook().setRainInfluenced(false);
            event.getHook().setSkyInfluenced(false);

            // Set actual wait time
            int waitTime = (int) Math.round((schedule.getTotalTime()) * getServer().getTPS()[1]);
            event.getHook().setMinWaitTime(waitTime);
            event.getHook().setMaxWaitTime(waitTime);
            event.getHook().setWaitTime(waitTime);

            schedule.setInitialEvent(event);
            scheduleNextGame(schedule);

            event.getHook().setLureTime(10, 10);

        } else if (state == PlayerFishEvent.State.LURED) {
            double tps = getServer().getTPS()[0];
            event.getHook().setMinLureTime(10);
            event.getHook().setMaxLureTime(10);

        } else if (state == PlayerFishEvent.State.BITE) {
            // Do right click
            Player player = event.getPlayer();

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                doRightClick(player, EnumHand.a);
            }, 5);

        } else if (state == PlayerFishEvent.State.CAUGHT_ENTITY) {
        }
    }

    @EventHandler
    public void onGameEnd(GameEndEvent event) {

        if (!event.wasSuccessful()) return;

        GameSchedule schedule = event.getSchedule();

        if (schedule.hasNextGame()) {
            this.scheduleNextGame(schedule);
        } else {
            // End fishing and get fish
            event.getGame().getTargetPlayer().showTitle(Title.title(
                    Component.text("\ua001").font(Key.key("seaborn_images", "icons")),
                    Component.text(ChatColor.GREEN + "That's a bite!")
            ));

            Utils.playSoundEffect(event.getGame().getTargetPlayer(), Key.key("minecraft", "entity.player.levelup"));
        }

    }

}
