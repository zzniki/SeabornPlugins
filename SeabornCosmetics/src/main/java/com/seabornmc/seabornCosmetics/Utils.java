package com.seabornmc.seabornCosmetics;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class Utils {

    public static Component formatText(String text) {
        return MiniMessage.miniMessage().deserialize(text);
    }

}
