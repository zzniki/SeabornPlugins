package com.seabornmc.seabornDuel;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class DuelInvite {

    private Player inviter;
    private Player target;

    public DuelInvite(Player inviter, Player target) {
        this.inviter = inviter;
        this.target = target;
    }

    public void accept() {

        AtomicInteger timer = new AtomicInteger();

        Bukkit.getScheduler().runTaskTimer(SeabornDuel.getInstance(), () -> {
            Component message = Utils.formatText("<green>Duel starting in " + (3 - timer.get()) + " seconds.");

            inviter.sendMessage(message);
            target.sendMessage(message);

            timer.getAndIncrement();

            if (timer.get() == 3) {
                // TODO: Start duel
            }

        }, 0L, 20L);
    }

}
