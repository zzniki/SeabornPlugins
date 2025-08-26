package com.seaborn.seabornCore.actionbar;

import com.seaborn.seabornCore.SeabornCore;
import com.seaborn.seabornCore.Utils;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class ActionbarManager implements Listener {

    private Map<UUID, ActionBar> actionBars = new HashMap<>();

    public ActionbarManager(SeabornCore plugin) {

        // Set up updates
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {

            for (ActionBar actionBar : actionBars.values()) {

                if (actionBar.isExecDialog()) actionBar.update();
                else if (actionBar.isFinishingDialog() && getServer().getCurrentTick() % 10 == 0) {
                    actionBar.update();
                }
                else {
                    // Only once a second
                    if (getServer().getCurrentTick() % 20 == 0 && !actionBar.isLocked()) {
                        actionBar.sendText(Utils.formatText(
                            "<gold>⛃ "
                                + Utils.numberFormat(Math.round(SeabornCore.getEconomy().getBalance(actionBar.getPlayer())))
                                + "        "
                                + "<aqua>⚓ " + Utils.numberFormat(PlayerPoints.getInstance().getAPI().look(actionBar.getPlayer().getUniqueId()))
                        ));
                    }
                }
            }

        }, 0, 1L);
    }

    public void addActionBar(Player player) {
        this.actionBars.put(player.getUniqueId(), new ActionBar(player));
    }

    public void removeActionBar(Player player) {
        this.actionBars.remove(player.getUniqueId());
    }

    public ActionBar getActionBar(Player player) {
        return this.actionBars.get(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        this.addActionBar(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        this.removeActionBar(event.getPlayer());
    }

    public void clearActionBars() {
        this.actionBars.clear();
    }


}
