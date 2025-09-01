package com.seaborn.seabornCrews.listeners;

import com.seaborn.seabornCrews.SeabornCrews;
import com.seaborn.seabornCrews.Utils;
import com.seaborn.seabornCrews.model.Crew;
import com.seaborn.seabornCrews.model.CrewMember;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        if (event.getMessage().startsWith("!")) {
            try {
                Crew crew = SeabornCrews.getCrewService().getPlayerCrew(event.getPlayer().getUniqueId());
                if (crew != null) {
                    event.setCancelled(true);

                    CrewMember senderMember = SeabornCrews.getCrewService().getCrewMember(crew.getId(), event.getPlayer().getUniqueId());
                    String formattedName = event.getPlayer().getName();
                    switch (senderMember.getRole()) {
                        case "captain":
                            formattedName = "<yellow>\uD83D\uDC51 " + formattedName + "</yellow>";
                            break;
                        case "quartermaster":
                            formattedName = "<blue>⚔ " + formattedName + "</blue>";
                            break;
                        default:
                            formattedName = formattedName;
                    }

                    Component message = Utils.formatText("<aqua>[CREW] " + formattedName + ": <dark_aqua>" + event.getMessage().replaceFirst("!", ""));
                    List<CrewMember> members = SeabornCrews.getCrewService().getCrewMembers(crew.getId());
                    for (CrewMember member : members) {
                        Player player = Bukkit.getPlayer(UUID.fromString(member.getMemberUuid()));
                        if (player == null) continue;
                        if (!player.isOnline()) continue;

                        player.sendMessage(message);

                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
