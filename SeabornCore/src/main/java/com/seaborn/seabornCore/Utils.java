package com.seaborn.seabornCore;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class Utils {

    public static Component formatWithPlaceholder(Player player, String text) {
        String res = PlaceholderAPI.setPlaceholders(player, text);
        return MiniMessage.miniMessage().deserialize(res);
    }

    public static Component formatText(String string) {
        return MiniMessage.miniMessage().deserialize(string);
    }

}
