package com.seabornmc.seabornDuel;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BoundingBox;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class DuelInvite {

    private Player inviter;
    private Player target;
    private BukkitTask task = null;

    public DuelInvite(Player inviter, Player target) {
        this.inviter = inviter;
        this.target = target;
    }

    public void accept() {

        AtomicInteger timer = new AtomicInteger();

        inviter.sendMessage(Utils.formatText("<green>" + target.getName() + " has accepted your duel!"));
        target.sendMessage(Utils.formatText("<green>You have accepted " + inviter.getName() + "'s duel challenge!"));

        task = Bukkit.getScheduler().runTaskTimer(SeabornDuel.getInstance(), () -> {
            Component message = Utils.formatText("<green>Duel starting in " + (3 - timer.get()) + " seconds.");

            inviter.sendMessage(message);
            target.sendMessage(message);

            timer.getAndIncrement();

            if (timer.get() == 4) {
                SeabornDuel.getDuelManager().startDuel(
                        List.of(inviter),
                        List.of(target),
                        inviter.getName(),
                        target.getName(),
                        new BoundingBox(178, 69, 397, 194, 69, 411),
                        new BoundingBox(192, 69, 437, 176, 69, 451)
                );
                inviter.sendMessage("duel started!!!");
                cancelTask();
            }

        }, 0L, 20L);
    }

    private void cancelTask() {
        if (this.task != null) Bukkit.getScheduler().cancelTask(task.getTaskId());
    }

    public void reject() {
        inviter.sendMessage(Utils.formatText("<red>" + target.getName() + " has rejected your duel."));
        target.sendMessage(Utils.formatText("<red>You have rejected " + inviter.getName() + "'s duel challenge."));

    }

    public void send() {
        Player player = this.target;
        player.sendMessage(Utils.formatText("<dark_aqua><st>-----------------------------------------"));
        player.sendMessage(Utils.formatText("<red><b>DUEL CHALLENGE!"));
        player.sendMessage(Utils.formatText("You have been challenged to a duel with " + this.inviter.getName()));
        player.sendMessage("");
        player.sendMessage(Utils.formatText("<b>    <green><hover:show_text:Click to accept><click:run_command:/duel accept>ACCEPT</click></hover>      <hover:show_text:Click to reject><click:run_command:/crew duel reject><red>REJECT</click></hover>"));
        player.sendMessage("");
        player.sendMessage(Utils.formatText("<dark_aqua><st>-----------------------------------------"));

        this.inviter.sendMessage(Utils.formatText("<green>Duel invite sent!"));
    }

}
