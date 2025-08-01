package com.seaborn.seabornCore;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.World;

import java.util.concurrent.TimeUnit;

import static org.bukkit.Bukkit.getServer;

public class Bossbars {

    private BossBar globalBar;

    public Bossbars() {
        this.globalBar = BossBar.bossBar(
                Component.text("Loading..."),
                .5f,
                BossBar.Color.BLUE,
                BossBar.Overlay.PROGRESS
        );
        this.updateGlobalBar();
    }

    public void updateGlobalBar() {
        World world = getServer().getWorld("world");

        if (world == null) return;

        long gameTime = world.getTime();
        long hours = gameTime / 1000 + 6;
        long minutes = (gameTime % 1000) * 60 / 1000;
        String ampm = "AM";
        if (hours >= 12) {
            hours -= 12;
            ampm = "PM";
        }
        if (hours >= 12) {
            hours -= 12;
            ampm = "AM";
        }
        if (hours == 0) hours = 12;
        String mm = "0" + minutes;
        mm = mm.substring(mm.length() - 2, mm.length());

        this.globalBar.name(Component.text(hours + ":" + mm + " " + ampm));
    }

    public BossBar getGlobalBar() {
        return this.globalBar;
    }

}
