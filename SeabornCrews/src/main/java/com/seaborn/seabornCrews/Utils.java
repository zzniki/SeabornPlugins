package com.seaborn.seabornCrews;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class Utils {

    private static MiniMessage miniMessage = MiniMessage.miniMessage();

    public static Component formatText(String text) {
        return miniMessage.deserialize(text);
    }

}
