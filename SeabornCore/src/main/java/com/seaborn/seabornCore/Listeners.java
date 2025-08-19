package com.seaborn.seabornCore;

import com.seaborn.seabornCore.db.model.PlayerData;
import com.seaborn.seabornCore.events.PlayerDataLoadEvent;
import com.seaborn.seabornCore.events.PlayerFirstJoinEvent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.time.Duration;

public class Listeners implements Listener {

    private SeabornCore plugin;

    public Listeners(SeabornCore plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();

        // Load player data
        PlayerData data = SeabornCore.getPlayerDataDAO().load(player.getUniqueId(), player.getInventory());
        boolean isFirstJoin = data == null;

        if (isFirstJoin) {
            data = new PlayerData(event.getPlayer());
            SeabornCore.getPlayerDataDAO().save(data);

            Bukkit.getPluginManager().callEvent(new PlayerFirstJoinEvent(player, event));
        }

        Bukkit.getPluginManager().callEvent(new PlayerDataLoadEvent(data));

        double targetBalance = data.getMoney();
        double currentPlayersBalance = SeabornCore.getEconomy().getBalance(player);
        double difference = targetBalance - currentPlayersBalance;

        if (difference > 0){
            SeabornCore.getEconomy().depositPlayer(player, difference);
        } else {
            SeabornCore.getEconomy().withdrawPlayer(player, -difference);
        }

        if (!isFirstJoin) player.teleport(data.getLastLocation());

        // TODO: Load rep

        // Show initial title
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


        // Show bossbar
        event.getPlayer().showBossBar(
                this.plugin.bossbars.getGlobalBar()
        );
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        PlayerData data = new PlayerData(event.getPlayer());
        SeabornCore.getPlayerDataDAO().save(data);
    }

}
