package com.seabornmc.seabornDialogs.dialogs;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogManager {

    private List<Dialog> dialogs = new ArrayList<>();
    private Map<Player, Dialog> playerDialogs = new HashMap<>();

    public Dialog getDialog(Player player) {
        if (!this.playerDialogs.containsKey(player)) return null;
        return this.playerDialogs.get(player);
    }

    public void registerDialog(Dialog dialog) {
        this.dialogs.add(dialog);

        if (this.playerDialogs.containsKey(dialog.getPlayer())) {
            this.deleteDialog(this.playerDialogs.get(dialog.getPlayer()));
        }

        this.playerDialogs.put(dialog.getPlayer(), dialog);
    }

    public void deleteDialog(Dialog dialog) {

        if (dialog == null) return;
        this.playerDialogs.remove(dialog.getPlayer());
        this.dialogs.remove(dialog);
        dialog.delete();

    }

    public void cleanup() {
        for (Dialog d : this.dialogs) {
            this.deleteDialog(d);
        }
    }

}
