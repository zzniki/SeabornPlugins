package com.seaborn.seabornFishing;

import de.tr7zw.nbtapi.NBT;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Utils {

    public static void playSoundEffect(Player player, Key key) {
        player.playSound(
                Sound.sound().type(key).source(Sound.Source.BLOCK).volume(.5f).build()
        );
    }

    public static String formatText(String text) {
        return text.replaceAll("&", "§");
    }

    public static RodProperties getRodProperties(ItemStack item) {

        String rodType = NBT.get(item, nbt -> (String) nbt.getString("rod-type"));
        CustomRod baseRod = SeabornFishing.config.getCustomRod(rodType);

        RodProperties prop = new RodProperties(baseRod);
        prop.loadItemStack(item);
        return prop;
    }

}
