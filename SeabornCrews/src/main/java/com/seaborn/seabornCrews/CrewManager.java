package com.seaborn.seabornCrews;

import com.seaborn.seabornCrews.model.Crew;
import com.seaborn.seabornCrews.model.CrewMember;
import com.seaborn.seabornCrews.model.Invitation;
import com.seaborn.seabornCrews.service.CrewService;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.time.Duration;
import java.util.*;

public class CrewManager {

    private Map<Integer, List<Invitation>> invitations = new HashMap<>();

    public boolean invitationExists(Crew crew, Player player) {

        if (invitations.containsKey(crew.getId())) {
            for (Invitation inv : invitations.get(crew.getId())) {
                if (inv.getCrew().getId() == crew.getId() && inv.getInvited().equals(player)) return true;
            }
        }

        return false;

    }

    public void invitePlayer(Player inviter, Crew crew, Player player) {

        CrewService crewService = SeabornCrews.getCrewService();

        try {
            if (crew.getMaxMembers() == crewService.getCrewMembers(crew.getId()).size()) {
                inviter.sendMessage(Utils.formatText("<red>The crew is full!"));
                return;
            }

            if (crewService.isPlayerInAnyCrew(player.getUniqueId())) {
                inviter.sendMessage(Utils.formatText("<red>That player is already in a crew."));
                return;
            }

            CrewMember inviterMember = crewService.getCrewMember(crew.getId(), inviter.getUniqueId());
            if (!inviterMember.getRole().equals("quartermaster") && !inviterMember.getRole().equals("captain")) {
                inviter.sendMessage(Utils.formatText("<red>Only the captain or quartermasters can invite members to the crew."));
                return;
            }

            if (!this.invitations.containsKey(crew.getId())) this.invitations.put(crew.getId(), new ArrayList<>());
            this.invitations.get(crew.getId()).add(new Invitation(crew, player));

            inviter.sendMessage(Utils.formatText("<green>Invited " + player.getName()));

            player.sendMessage(Utils.formatText("<dark_aqua><st>-----------------------------------------"));
            player.sendMessage(Utils.formatText("<aqua><b>CREW INVITATION!"));
            player.sendMessage(Utils.formatText("You have been invited to join <yellow>" + crew.getName() + " <reset>(" + crew.getTag() + ")"));
            player.sendMessage("");
            player.sendMessage(Utils.formatText("<b>    <green><hover:show_text:Click to accept><click:run_command:/crew acceptinvite " + crew.getId() + ">ACCEPT</click></hover>      <hover:show_text:Click to reject><click:run_command:/crew rejectinvite " + crew.getId() + "><red>REJECT</click></hover>"));
            player.sendMessage("");
            player.sendMessage(Utils.formatText("<dark_aqua><st>-----------------------------------------"));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    public Invitation getInvitation(Crew crew, Player player) {

        if (invitations.containsKey(crew.getId())) {
            for (Invitation inv : invitations.get(crew.getId())) {
                if (inv.getCrew().getId() == crew.getId() && inv.getInvited().equals(player)) return inv;
            }
        }

        return null;

    }

    public void acceptInvitation(Integer crewId, Player player) {

        try {
            Crew crew = SeabornCrews.getCrewService().getCrew(crewId);

            if (crew == null) {
                player.sendMessage(Utils.formatText("<red>Could not find that crew."));
                return;
            }

            Invitation invitation = this.getInvitation(crew, player);

            if (invitation == null) {
                player.sendMessage(Utils.formatText("<red>You were not invited to that crew."));
                return;
            }

            List<CrewMember> members = SeabornCrews.getCrewService().getCrewMembers(crew.getId());
            for (CrewMember member : members) {
                Player memberPlayer = Bukkit.getPlayer(UUID.fromString(member.getMemberUuid()));
                if (memberPlayer == null) continue;

                if (memberPlayer.isOnline())
                    memberPlayer.sendMessage(Utils.formatText("<b><aqua>CREWS: </aqua></b><green>" + player.getName() + " has joined your crew!"));
            }

            SeabornCrews.getCrewService().addMember(crew.getId(), player.getUniqueId().toString(), "member");
            player.sendMessage(Utils.formatText("<green>You have joined " + crew.getName() + "!"));

            this.invitations.get(crew.getId()).remove(invitation);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void rejectInvitation(Integer crewId, Player player) {
        try {
            Crew crew = SeabornCrews.getCrewService().getCrew(crewId);

            if (crew == null) {
                player.sendMessage(Utils.formatText("<red>Could not find that crew."));
                return;
            }

            Invitation invitation = this.getInvitation(crew, player);

            if (invitation == null) {
                player.sendMessage(Utils.formatText("<red>You were not invited to that crew."));
                return;
            }

            List<CrewMember> members = SeabornCrews.getCrewService().getCrewMembers(crew.getId());
            for (CrewMember member : members) {
                Player memberPlayer = Bukkit.getPlayer(UUID.fromString(member.getMemberUuid()));
                if (memberPlayer == null) continue;

                if (memberPlayer.isOnline())
                    memberPlayer.sendMessage(Utils.formatText("<b><aqua>CREWS: </aqua></b><dark_aqua>" + player.getName() + " has rejected the invitation to join your crew!"));
            }

            player.sendMessage(Utils.formatText("<red>You have rejected the invitation to " + crew.getName() + "!"));

            this.invitations.get(crew.getId()).remove(invitation);


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void createCrew(Player player, String name, String tag) {
        try {
            if (SeabornCrews.getCrewService().isPlayerInAnyCrew(player.getUniqueId())) {
                player.sendMessage(Utils.formatText("<red>You are already in a crew. You can leave your current crew with /crew leave"));
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (tag.length() > 8) {
            player.sendMessage(Utils.formatText("<red>The crew's tag cannot be longer than 8 characters. Example: BESTCREW"));
            return;
        }

        if (name.length() > 64) {
            player.sendMessage(Utils.formatText("<red>The crew's name cannot be longer than 64 characters."));
            return;
        }

        // Create crew

        Crew crew = new Crew();
        crew.setName(name);
        crew.setOwnerUuid(player.getUniqueId().toString());
        crew.setPublic(false);
        crew.setReputation(0);
        crew.setTag(tag);
        crew.setMaxMembers(15);
        crew.setDescription("");
        crew.setBattleWins(0);

        try {
            crew = SeabornCrews.getCrewService().createCrew(crew);
        } catch (SQLException e) {
            player.sendMessage(Utils.formatText("<red>An error ocurred while creating the crew."));
            player.sendMessage(Utils.formatText("<red> + e.toString()"));
        }

        // Create world
        Crew finalCrew = crew;
        SeabornCrews.getIslandService().createIsland(crew.getId()).thenAccept((worldId) -> {

            if (worldId == null) {
                player.sendMessage(Utils.formatText("<red>An error ocurred while creating the crew's island."));
                try {
                    SeabornCrews.getCrewService().deleteCrew(finalCrew.getId());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return;
            }

            finalCrew.setIslandWorld(worldId);
            try {
                SeabornCrews.getCrewService().saveCrew(finalCrew);
            } catch (SQLException e) {
                player.sendMessage(Utils.formatText("<red>An error ocurred while creating the crew."));
                throw new RuntimeException(e);
            }

            player.showTitle(Title.title(
                    Utils.formatText("<green>Crew created!"),
                    Utils.formatText("<white>Walk through the portal"),
                    Title.Times.times(
                            Duration.ofSeconds(0),
                            Duration.ofSeconds(2),
                            Duration.ofSeconds(1)
                    )
            ));
            player.playSound(Sound.sound()
                    .type(Key.key("minecraft", "entity.experience_orb.pickup"))
                    .source(Sound.Source.PLAYER)
                    .build()
            );

        });
    }

}
