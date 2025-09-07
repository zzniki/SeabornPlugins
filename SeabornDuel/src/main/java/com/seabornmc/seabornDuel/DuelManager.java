package com.seabornmc.seabornDuel;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class DuelManager {

    private final int maxDuels;

    private List<Duel> currentDuels;

    public DuelManager(int maxDuels) {
        this.maxDuels = maxDuels;
        this.currentDuels = new ArrayList<>();

        Bukkit.getScheduler().runTaskTimer(SeabornDuel.getInstance(), () -> {
            for (Duel duel : this.currentDuels) {
                duel.tick();
            }
        }, 1L, 1L);

    }

    public boolean isDuelHappening() {
        return this.currentDuels.size() >= this.maxDuels;
    }

    public void startDuel(List<Player> team1, List<Player> team2, String team1Name, String team2Name, BoundingBox loc1, BoundingBox loc2) {

        if (currentDuels.size() >= maxDuels) {
            throw new RuntimeException("There's already a duel in progress, please wait until it's end.");
        }

        Duel duel = new Duel(
                SeabornDuel.getInstance(),
                team1,
                team2,
                team1Name,
                team2Name,
                loc1,
                loc2,
                10 * 60 * 20
        ); // 10 minutes
        duel.setOnEnd((none) -> {
            this.currentDuels.remove(duel);
            return null;
        });

        this.currentDuels.add(duel);
        getServer().getPluginManager().registerEvents(duel, SeabornDuel.getInstance());

        duel.startDuel();
    }

    public void endAllDuels() {
        for (Duel duel : this.currentDuels) {
            duel.endDuel(-1);
        }
    }

}
