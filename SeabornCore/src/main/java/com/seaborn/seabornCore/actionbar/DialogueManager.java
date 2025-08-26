package com.seaborn.seabornCore.actionbar;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class DialogueManager implements Listener {

    private ActionbarManager actionbarManager;
    private Map<UUID, Dialogue> dialogues = new HashMap<>();


    private class Dialogue {
        private ActionBar actionbar;
        private Function<Void, Void> onComplete;
        private boolean completed = false;

        public Dialogue(ActionBar actionbar, Function<Void, Void> onComplete) {
            this.actionbar = actionbar;
            this.onComplete = onComplete;
        }

        public boolean isCompleted() { return completed; }

        public void setCompleted(boolean completed) {
            this.completed = completed;
        }

        public void finish() {
            actionbar.getPlayer().playSound(Sound.sound().type(Key.key("minecraft", "entity.experience_orb.pickup")).volume(.5f).source(Sound.Source.NEUTRAL).build());
            actionbar.setLocked(false);
            actionbar.setFinishingDialog(false);
            onComplete.apply(null);
        }

        public void skip() {
            actionbar.skipDialogue();
        }

    }

    public DialogueManager(ActionbarManager actionbarManager) {
        this.actionbarManager = actionbarManager;
    }

    public void showDialogue(Player player, String text, Function<Void, Void> onComplete) {
        this.showDialogue(player, text, TextColor.color(255, 255, 255), onComplete);
    }

    public void showDialogue(Player player, String text, TextColor color, Function<Void, Void> onComplete) {
        ActionBar actionBar = this.actionbarManager.getActionBar(player);

        if (actionBar == null) {
            onComplete.apply(null);
            return;
        }

        Dialogue dialogue = new Dialogue(actionBar, onComplete);
        this.dialogues.put(player.getUniqueId(), dialogue);

        actionBar.sendDialogue(text, color, (a) -> {

            dialogue.setCompleted(true);

            return null;
        });
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.LEFT_CLICK_AIR || event.getAction() != Action.LEFT_CLICK_BLOCK);

        Player player = event.getPlayer();
        if (this.dialogues.containsKey(player.getUniqueId())) {
            Dialogue dialogue = this.dialogues.get(player.getUniqueId());

            if (dialogue.isCompleted()) {
                dialogue.finish();
                this.dialogues.remove(player.getUniqueId());
            } else {
                dialogue.skip();
            }

        }


    }

}
