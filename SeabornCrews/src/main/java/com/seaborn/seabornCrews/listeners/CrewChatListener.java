package com.seaborn.seabornCrews.listeners;

import com.seaborn.seabornCrews.CrewService;
import com.seaborn.seabornCrews.model.Crew;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Optional;

public class CrewChatListener implements Listener {
    private final CrewService service;

    public CrewChatListener(CrewService service) {
        this.service = service;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        String msg = e.getMessage();
        if (!msg.startsWith("!")) return;
        e.setCancelled(true);
        Player p = e.getPlayer();
        Optional<Crew> oc = service.getCrewByMember(p.getUniqueId());
        if (oc.isEmpty()) { p.sendMessage("You are not in a crew."); return; }
        Crew crew = oc.get();
        String out = "[Crew " + crew.getName() + "] " + p.getName() + ": " + msg.substring(1);
        // send to all online members
        crew.getMembers().forEach(uuid -> {
            Player member = p.getServer().getPlayer(uuid);
            if (member != null && member.isOnline()) member.sendMessage(out);
        });
    }
}
