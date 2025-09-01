package com.seaborn.seabornCore;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener, ChatRenderer {

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        event.renderer(this);
    }

    @Override
    public Component render(Player source, Component sourceDisplayName, Component message, Audience viewer) {
        String crewTag = PlaceholderAPI.setPlaceholders(source, "%crew_tag%");
        String rank = PlaceholderAPI.setPlaceholders(source, "%vault_rank%");

        if (crewTag.equalsIgnoreCase("None")) crewTag = "";
        else crewTag = "<aqua>[" + crewTag + "] ";

        return Utils.formatText(crewTag)
                .append(Utils.formatText(rank))
                .append(Component.text(" "))
                .append(sourceDisplayName)
                .append(Component.text(": "))
                .append(message);
    }

}
