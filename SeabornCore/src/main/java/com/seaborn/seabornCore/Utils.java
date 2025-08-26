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

    public static String numberFormat(long n) {
        // Suffixes with corresponding powers of 10
        String[] data = {"QT,18", "Q,15", "T,12", "B,9", "M,6", "k,3"};

        for (String entry : data) {
            String[] parts = entry.split(",");
            String suffix = parts[0];
            int power = Integer.parseInt(parts[1]);

            long divisor = (long) Math.pow(10, power);
            if (n >= divisor) {
                double value = (double) n / divisor;
                return String.format("%.2f%s", value, suffix);
            }
        }
        return String.valueOf(n); // if smaller than 1k, return as-is
    }

}
