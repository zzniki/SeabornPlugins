package com.seaborn.seabornCore.scoreboard;

import com.seaborn.seabornCore.SeabornCore;
import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import rocks.gravili.notquests.paper.NotQuests;
import rocks.gravili.notquests.paper.structs.ActiveQuest;
import rocks.gravili.notquests.paper.structs.QuestPlayer;

import java.util.*;

import static org.bukkit.Bukkit.getServer;

public class ScoreboardManager implements Listener {

    private final Map<Player, FastBoard> boards = new HashMap<>();
    private MiniMessage miniMessage;

    public ScoreboardManager() {
        this.miniMessage = MiniMessage.miniMessage();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        FastBoard board = new FastBoard(player);

        board.updateTitle(miniMessage.deserialize("<b><gradient:#C7CBFF:#9365FF>SEABORN</gradient></b>MC.COM"));

        this.boards.put(player, board);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        FastBoard board = this.boards.remove(player);

        if (board != null) {
            board.delete();
        }
    }

    private void updateBoard(Player player) {

        List<Component> lines = new ArrayList<>();
        FastBoard board = this.boards.get(player);

        lines.add(miniMessage.deserialize("<dark_gray>Private Alpha"));
        lines.add(Component.text(""));
        lines.add(miniMessage.deserialize("<b>PROFILE"));
        lines.add(miniMessage.deserialize("<aqua>Crew: <#549CD5>None"));
        lines.add(miniMessage.deserialize("<aqua>Catches: <#549CD5>0"));
        lines.add(miniMessage.deserialize("<aqua>Catch Rate: <#549CD5>1"));

        QuestPlayer questPlayer = NotQuests.getInstance().getQuestPlayerManager().getActiveQuestPlayer(player.getUniqueId());
        if (questPlayer != null && !questPlayer.getActiveQuests().isEmpty()) {
            ActiveQuest quest = questPlayer.getActiveQuests().getFirst();
            lines.add(Component.text(""));
            lines.add(miniMessage.deserialize("<b>QUEST"));
            lines.add(miniMessage.deserialize("<aqua>" + quest.getQuest().getQuestDisplayName()));
            if (!quest.getActiveObjectives().isEmpty()) {
                List<String> description = quest.getActiveObjectives().getFirst().getObjective().getDescriptionLines(25);
                for (String line : description)
                    lines.add(miniMessage.deserialize("<gray>" + line));
            }


        }

        board.updateLines(lines);
    }

    public void update() {
        for (Player board : this.boards.keySet()) {
            updateBoard(board);
        }
    }

}
