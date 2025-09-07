package com.seabornmc.seabornDuel;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InviteManager {

    public Map<UUID, DuelInvite> invites = new HashMap<>();

    public void createInvite(Player inviter, Player target) {
        DuelInvite invite = new DuelInvite(inviter, target);
        this.invites.put(target.getUniqueId(), invite);
        invite.send();
    }

    public void accept(Player target) {
        if (this.invites.containsKey(target.getUniqueId())) {
            this.invites.get(target.getUniqueId()).accept();
            this.invites.remove(target.getUniqueId());
        } else {
            target.sendMessage(Utils.formatText("<red>You don't have any pending duel challenges."));
        }
    }

    public void reject(Player target) {
        if (this.invites.containsKey(target.getUniqueId())) {
            this.invites.get(target.getUniqueId()).reject();
            this.invites.remove(target.getUniqueId());
        } else {
            target.sendMessage(Utils.formatText("<red>You don't have any pending duel challenges."));
        }
    }


}
