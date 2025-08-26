package com.seaborn.seabornCore.actionbar;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class ActionBar {

    private boolean locked = false;
    private Player player;
    private Component text;

    private boolean execDialog = false;
    private boolean finishingDialog = false;
    private String dialogueTarget;
    private int dialogueIndex;
    private TextColor dialogueColor;
    private int dialogueTicks = 0;
    private Function<Void, Void> onDialogueFinish;

    public ActionBar(Player player, Component text) {
        this.player = player;
        this.text = text;
    }

    public ActionBar(Player player) {
        this.player = player;
    }

    public void update() {

        if (this.execDialog && this.dialogueIndex == this.dialogueTarget.length() - 1) {
            this.execDialog = false;
            this.finishingDialog = true;
            this.player.sendActionBar(Component.text(dialogueTarget.substring(0, dialogueIndex + 1)).color(dialogueColor));
            this.onDialogueFinish.apply(null);
        }

        if (this.finishingDialog) {
            this.player.sendActionBar(Component.text(dialogueTarget.substring(0, dialogueIndex + 1)).color(dialogueColor));
        } else if (this.execDialog) {
            this.player.sendActionBar(Component.text(dialogueTarget.substring(0, dialogueIndex + 1)).color(dialogueColor));
            this.dialogueIndex++;
            this.dialogueTicks++;
            if (this.dialogueTicks % 2 == 0) this.player.playSound(Sound.sound().type(Key.key("minecraft", "block.note_block.pling")).source(Sound.Source.PLAYER).volume(.5f).pitch((float) Math.random() * 1.5f + .5f).build());
        } else {
            this.player.sendActionBar(text);
        }
    }

    public void sendText(Component text) {
        this.text = text;
        this.update();
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void sendDialogue(String dialogue, TextColor color, Function<Void, Void> onFinish) {
        this.dialogueTarget = dialogue;
        this.locked = true;
        this.execDialog = true;
        this.dialogueTicks = 0;
        this.dialogueIndex = 0;
        this.onDialogueFinish = onFinish;
    }

    public void skipDialogue() {
        if (this.execDialog) {
            this.dialogueIndex = this.dialogueTarget.length() - 1;
        }
    }

    public boolean isExecDialog() {
        return execDialog;
    }

    public void setExecDialog(boolean execDialog) {
        this.execDialog = execDialog;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isFinishingDialog() {
        return finishingDialog;
    }

    public void setFinishingDialog(boolean finishingDialog) {
        this.finishingDialog = finishingDialog;
    }
}
