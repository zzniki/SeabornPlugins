package com.seaborn.seabornFishing;

import com.seaborn.seabornFishing.events.GameEndEvent;
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
            GameSchedule schedule = this.scheduler.createSchedule(event.getPlayer());

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
            event.getPlayer().sendMessage("caught");
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
