package com.oheers.fish.competition;

import com.oheers.fish.EvenMoreFish;
import com.oheers.fish.database.DatabaseUtil;
import com.oheers.fish.messages.ConfigMessage;
import com.oheers.fish.messages.abstracted.EMFMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinChecker implements Listener {

    // Gives the player the active fishing bar if there's a fishing event cracking off
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Competition activeComp = Competition.getCurrentlyActive();
        if (activeComp == null) {
            return;
        }

        activeComp.getStatusBar().addPlayer(event.getPlayer());
        if (activeComp.getStartMessage() == null) {
            return;
        }

        EMFMessage message = activeComp.getCompetitionType().getStrategy().getTypeFormat(
            activeComp, ConfigMessage.COMPETITION_JOIN
        );

        EvenMoreFish.getScheduler().runTaskLater(() -> message.send(event.getPlayer()), 20L * 3);
    }

    // Removes the player from the bar list if they leave the server
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        final Competition activeComp = Competition.getCurrentlyActive();
        if (activeComp != null) {
            activeComp.getStatusBar().removePlayer(event.getPlayer());
        }

        if (!DatabaseUtil.isDatabaseOnline()) {
            return;
        }

        EvenMoreFish.getInstance().getPluginDataManager().getUserReportDataManager().flush();
        EvenMoreFish.getInstance().getPluginDataManager().getUserFishStatsDataManager().flush();
    }
}