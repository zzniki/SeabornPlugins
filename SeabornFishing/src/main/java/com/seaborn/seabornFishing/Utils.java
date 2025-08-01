package com.seaborn.seabornFishing;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import de.tr7zw.nbtapi.NBT;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Location;
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

    public static Region findRegion(Location loc) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionQuery query = container.createQuery();
        ApplicableRegionSet set = query.getApplicableRegions(BukkitAdapter.adapt(loc));

        Region finalRegion = null;

        for (ProtectedRegion region : set.getRegions()) {
            if (!SeabornFishing.config.hasRegion(region.getId())) continue;

            Region cRegion = SeabornFishing.config.getRegion(region.getId());

            if (finalRegion == null) {
                finalRegion = cRegion;
                continue;
            }

            if (cRegion.getPriority() >= finalRegion.getPriority()) finalRegion = cRegion;
        }

        return finalRegion;
    }

}
