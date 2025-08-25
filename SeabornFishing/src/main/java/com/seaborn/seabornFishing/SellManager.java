package com.seaborn.seabornFishing;

import com.seaborn.seabornFishing.rewards.FishItemReward;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.black_ixx.playerpoints.PlayerPoints;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;

public class SellManager {

    public static void displaySellNotification(Player player, float money) {
        String moneyStr = String.valueOf(Math.round(money * 100f) / 100f);

        player.showTitle(Title.title(
                Component.text(""),
                Component.text("\ua003 ").font(Key.key("seaborn_images", "icons"))
                        .append(Component.text(moneyStr)),
                Title.Times.times(
                        Duration.ofSeconds(0),
                        Duration.ofSeconds(2),
                        Duration.ofSeconds(1)
                )
        ));
        player.playSound(
                Sound.sound().type(Key.key("seaborn_sounds", "coins")).source(Sound.Source.BLOCK).volume(1f).build()
        );
    }

    public static float sellItemInHand(Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.getType() != Material.COD) {
            player.sendMessage(Component.text(ChatColor.RED + "You don't have any fish in your hand."));
            return -1f;
        }

        FishItemReward fishItem = new FishItemReward(item);
        float worth = fishItem.getWorth();

        player.getInventory().removeItem(item);

        SeabornFishing.getEconomy().depositPlayer(player, worth);
        PlayerPoints.getInstance().getAPI().give(player.getUniqueId(), Math.round(worth * 2));

        return worth;

    }

    public static float sellInventory(Player player) {

        float totalWorth = 0;

        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;
            if (item.getType() != Material.COD) continue;

            FishItemReward fishItem = new FishItemReward(item);
            float worth = fishItem.getWorth();

            player.getInventory().removeItemAnySlot(item);

            totalWorth += worth;

        }

        SeabornFishing.getEconomy().depositPlayer(player, totalWorth);
        PlayerPoints.getInstance().getAPI().give(player.getUniqueId(), Math.round(totalWorth * 2));

        return totalWorth;
    }

}
