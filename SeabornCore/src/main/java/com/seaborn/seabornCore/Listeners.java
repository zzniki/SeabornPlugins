package com.seaborn.seabornCore;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import java.time.Duration;

import static org.bukkit.Bukkit.getServer;

public class Listeners implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        TextComponent imageText = Component.text("\ua000")
                        .font(Key.key("seaborn_images", "images"));

        event.getPlayer().showTitle(Title.title(
                imageText,
                Component.text(""),
                Title.Times.times(
                        Duration.ofSeconds(1),
                        Duration.ofSeconds(3),
                        Duration.ofSeconds(1))
        ));

        event.getPlayer().playSound(
                Sound.sound().type(Key.key("seaborn_sounds", "morning")).source(Sound.Source.RECORD).volume(.5f).build()
        );
    }

}
