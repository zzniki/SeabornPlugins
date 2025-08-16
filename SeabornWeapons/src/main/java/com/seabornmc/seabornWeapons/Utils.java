package com.seabornmc.seabornWeapons;

import net.md_5.bungee.api.ChatColor;

public class Utils {
    public static String formatText(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
