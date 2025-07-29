package com.seaborn.seabornFishing;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;

public class Utils {

    public static void playSoundEffect(Player player, Key key) {
        player.playSound(
                Sound.sound().type(key).source(Sound.Source.BLOCK).volume(.5f).build()
        );
    }

}
