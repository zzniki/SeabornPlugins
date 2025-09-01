package com.seaborn.seabornCrews.commands;

import com.seaborn.seabornCore.SeabornCore;
import com.seaborn.seabornCrews.SeabornCrews;
import com.seaborn.seabornCrews.Utils;
import com.seaborn.seabornCrews.menus.CrewMasterMenu;
import com.seaborn.seabornCrews.model.Crew;
import com.seaborn.seabornCrews.model.CrewMember;
import com.seaborn.seabornCrews.service.CrewService;
import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.*;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CrewCommand {

    private CommandAPICommand command;

    public CrewCommand() {
        this.command = new CommandAPICommand("crew")
                .withSubcommands(
                        //getJoin(),
                        getLeave(),
                        getHome(),
                        getKick(),
                        getDisband(),
                        getInfo(),
                        getInvite(),
                        getSetRole(),
                        getTransfer(),
                        getAcceptInvite(),
                        getRejectInvite()
                )
                .executes((sender, args) -> {

                    sender.sendMessage(Utils.formatText("<b><aqua>CREW COMMAND"));
                    sender.sendMessage(Utils.formatText("/crew info - Shows information about your crew."));
                    sender.sendMessage(Utils.formatText("/crew invite <player> <dark_aqua>- Invites a player to your crew."));
                    sender.sendMessage(Utils.formatText("/crew leave <dark_aqua>- Leaves your current crew."));
                    sender.sendMessage(Utils.formatText("/crew home <dark_aqua>- Travels to your crew's island. (Requires rank)"));

                    sender.sendMessage("");

                    sender.sendMessage(Utils.formatText("<aqua><u>Administration</u>"));
                    sender.sendMessage(Utils.formatText("/crew setrole <member> <role> <dark_aqua>- Promotes or demotes a member."));
                    sender.sendMessage(Utils.formatText("/crew kick <member> <dark_aqua>- Kicks a member from your crew."));
                    sender.sendMessage(Utils.formatText("/crew transfer <member> <dark_aqua>- Transfers the crew's ownership to another member."));
                    sender.sendMessage(Utils.formatText("/crew disband <dark_aqua>- Disbands your crew."));

                });
    }

    public CommandAPICommand getDisband() {

        return new CommandAPICommand("disband")
                .executes((sender, args) -> {
                    Player player = (Player) sender;
                    try {
                        Crew playerCrew = SeabornCrews.getCrewService().getPlayerCrew(player.getUniqueId());

                        if (playerCrew == null) {
                            sender.sendMessage(Utils.formatText("<red>You are not in a crew."));
                            return;
                        }

                        CrewMember owner = SeabornCrews.getCrewService().getOwnerAsCrewMember(playerCrew.getId());
                        if (!owner.getMemberUuid().equals(player.getUniqueId().toString())) {
                            sender.sendMessage(Utils.formatText("<red>You are not the crew's captain!"));
                            return;
                        }

                        SeabornCrews.getIslandService().unloadIsland(playerCrew.getIslandWorld());
                        Bukkit.getScheduler().runTaskLater(SeabornCrews.getInstance(), () -> {
                            SeabornCrews.getIslandService().deleteIsland(playerCrew.getIslandWorld()).thenAccept((result) -> {
                            });
                        }, 20);

                        SeabornCrews.getCrewService().deleteCrew(playerCrew.getId());
                        sender.sendMessage(Utils.formatText("<green>Crew disbanded!"));

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

    }
    public CommandAPICommand getHome() {
        return new CommandAPICommand("home")
                .withPermission("seaborn.crew.home")
                .executes((sender, args) -> {
                    Player player = (Player) sender;

                    try {
                        Crew crew = SeabornCrews.getCrewService().getPlayerCrew(player.getUniqueId());

                        if (crew == null) {
                            sender.sendMessage(Utils.formatText("<red>You are not in a crew."));
                            return;
                        }

                        SeabornCrews.getIslandService().teleportToIsland(player, crew.getIslandWorld());
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                });
    }

    public CommandAPICommand getInfo() {
        return new CommandAPICommand("info")
                .withOptionalArguments(new GreedyStringArgument("name"))
                .executes((sender, args) -> {

                    Player player = (Player) sender;

                    try {
                        Crew crew = SeabornCrews.getCrewService().getPlayerCrew(player.getUniqueId());

                        if (crew == null) {
                            sender.sendMessage(Utils.formatText("<red>You are not in a crew!"));
                            return;
                        }

                        List<CrewMember> members = SeabornCrews.getCrewService().getCrewMembers(crew.getId());
                        String membersStr = String.join(", ", members.stream().map(
                                m -> {
                                    String role = m.getRole();
                                    String name = m.asOfflinePlayer().getName();

                                    switch (role) {
                                        case "captain":
                                            return "<yellow>\uD83D\uDC51 " + name;
                                        case "quartermaster":
                                            return "<blue>⚔ " + name;
                                        default:
                                            return "<dark_aqua>" + name;
                                    }

                                }
                        ).toList());

                        sender.sendMessage(Utils.formatText("<aqua><b>CREW INFORMATION"));
                        sender.sendMessage(Utils.formatText("Name: <dark_aqua>" + crew.getName()));
                        sender.sendMessage(Utils.formatText("Tag: <dark_aqua>" + crew.getTag()));
                        sender.sendMessage(Utils.formatText("Description: <dark_aqua>" + crew.getDescription()));
                        sender.sendMessage(Utils.formatText("Captain: <dark_aqua>" + Bukkit.getOfflinePlayer(UUID.fromString(crew.getOwnerUuid())).getName()));
                        sender.sendMessage(Utils.formatText("Reputation: <dark_aqua>" + crew.getReputation()));
                        sender.sendMessage(Utils.formatText("Won battles: <dark_aqua>" + crew.getBattleWins()));
                        sender.sendMessage(Utils.formatText("Members (" + members.size()  + "): <dark_aqua>" + membersStr));

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                });
    }

    public CommandAPICommand getInvite() {
        return new CommandAPICommand("invite")
                .withArguments(new PlayerArgument("player"))
                .executes((sender, args) -> {

                    Player inviter = (Player) sender;
                    try {
                        Crew crew = SeabornCrews.getCrewService().getPlayerCrew(inviter.getUniqueId());

                        if (crew == null) {
                            sender.sendMessage(Utils.formatText("<red>You are not in a crew."));
                            return;
                        }

                        CrewMember member = SeabornCrews.getCrewService().getCrewMember(crew.getId(), inviter.getUniqueId());
                        if (!List.of("quartermaster", "captain").contains(member.getRole().toLowerCase())) {
                            inviter.sendMessage(Utils.formatText("<red>Only quartermasters and the captain can invite members. Ask your captain to change your role."));
                            return;
                        }

                        Player invited = (Player) Objects.requireNonNull(args.get("player"));

                        if (invited == null) {
                            sender.sendMessage(Utils.formatText("<red>That player does not exist."));
                            return;
                        }

                        if (!invited.isOnline()) {
                            sender.sendMessage(Utils.formatText("<red>That player is not online."));
                            return;
                        }

                        SeabornCrews.getCrewManager().invitePlayer((Player) sender, crew, invited);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                });
    }

    public CommandAPICommand getAcceptInvite() {
        return new CommandAPICommand("acceptinvite")
                .withArguments(new IntegerArgument("id"))
                .executes((sender, args) -> {
                    SeabornCrews.getCrewManager().acceptInvitation((int) Objects.requireNonNull(args.get("id")), (Player) sender);
                });
    }

    public CommandAPICommand getRejectInvite() {
        return new CommandAPICommand("rejectinvite")
                .withArguments(new IntegerArgument("id"))
                .executes((sender, args) -> {
                    SeabornCrews.getCrewManager().rejectInvitation((int) Objects.requireNonNull(args.get("id")), (Player) sender);
                });
    }

    public CommandAPICommand getMenu() {
        return new CommandAPICommand("menu")
                .executes((sender, args) -> {
                    new CrewMasterMenu((Player) sender).show();
                });
    }

    public CommandAPICommand getLeave() {

        return new CommandAPICommand("leave")
                .executes((sender, args) -> {
                    Player player = (Player) sender;

                    try {
                        Crew crew = SeabornCrews.getCrewService().getPlayerCrew(player.getUniqueId());

                        if (crew == null) {
                            player.sendMessage(Utils.formatText("<red>You are not in a crew!"));
                            return;
                        }

                        if (crew.getOwnerUuid().equals(player.getUniqueId().toString())) {
                            player.sendMessage(Utils.formatText("<red>You are the owner of the crew, you can disband it using /crew disband"));
                            return;
                        }

                        SeabornCrews.getIslandService().kickFromIsland(crew.getIslandWorld(), player.getUniqueId());

                        SeabornCrews.getCrewService().removeMember(crew.getId(), player.getUniqueId().toString());
                        player.sendMessage(Utils.formatText("<green>You left " + crew.getName()));


                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                });
    }

    public CommandAPICommand getSetRole() {

        ArgumentSuggestions<CommandSender> memberSuggestions = ArgumentSuggestions.strings(info -> {
            Player player = (Player) info.sender();
            try {
                Crew crew = SeabornCrews.getCrewService().getPlayerCrew(player.getUniqueId());
                if (crew == null) return new String[]{};

                List<CrewMember> members = SeabornCrews.getCrewService().getCrewMembers(crew.getId());
                return members.stream()
                        .map(m -> m.asOfflinePlayer().getName())
                        .filter(Objects::nonNull)
                        .filter(p -> !p.equals(player.getName()))
                        .toArray(String[]::new);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        ArgumentSuggestions<CommandSender> roleSuggestions = ArgumentSuggestions.strings("member", "quartermaster");

        return new CommandAPICommand("setrole")
                .withArguments(
                        new StringArgument("member").replaceSuggestions(memberSuggestions),
                        new StringArgument("role").replaceSuggestions(roleSuggestions)
                )
                .executes((sender, args) -> {
                    Player player = (Player) sender;
                    String memberName = (String) args.get("member");
                    String roleName = ((String) args.get("role")).toLowerCase();

                    if (!List.of("member", "quartermaster").contains(roleName)) {
                        player.sendMessage(Utils.formatText("<red>Role not found."));
                        return;
                    }

                    try {
                        Crew crew = SeabornCrews.getCrewService().getPlayerCrew(player.getUniqueId());

                        if (crew == null) {
                            player.sendMessage(Utils.formatText("<red>You are not in a crew."));
                            return;
                        }

                        // Check permissions
                        if (!crew.getOwnerUuid().equals(player.getUniqueId().toString())) {
                            player.sendMessage(Utils.formatText("<red>Only the crew's captain can assign roles."));
                            return;
                        }

                        List<CrewMember> members = SeabornCrews.getCrewService().getCrewMembers(crew.getId());
                        for (CrewMember member : members) {
                            OfflinePlayer offlineMember = member.asOfflinePlayer();
                            if (offlineMember == null) continue;
                            if (offlineMember.getName() == null) continue;
                            if (offlineMember.getName().equals(player.getName())) continue;

                            if (offlineMember.getName().equals(memberName)) {
                                SeabornCrews.getCrewService().changeMemberRole(crew.getId(), member.getMemberUuid(), roleName);
                                player.sendMessage(Utils.formatText("<green>" + offlineMember.getName() + " is now a " + roleName + "!"));
                                return;
                            }
                        }

                        player.sendMessage(Utils.formatText("<red>Member not found."));

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                });
    }

    public CommandAPICommand getKick() {

        ArgumentSuggestions<CommandSender> memberSuggestions = ArgumentSuggestions.strings(info -> {
            Player player = (Player) info.sender();
            try {
                Crew crew = SeabornCrews.getCrewService().getPlayerCrew(player.getUniqueId());
                if (crew == null) return new String[]{};

                List<CrewMember> members = SeabornCrews.getCrewService().getCrewMembers(crew.getId());
                return members.stream()
                        .map(m -> m.asOfflinePlayer().getName())
                        .filter(Objects::nonNull)
                        .filter(p -> !p.equals(player.getName()))
                        .toArray(String[]::new);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        return new CommandAPICommand("kick")
                .withArguments(
                        new StringArgument("member").replaceSuggestions(memberSuggestions)
                )
                .executes((sender, args) -> {
                    Player player = (Player) sender;
                    String memberName = (String) args.get("member");
                    try {
                        Crew crew = SeabornCrews.getCrewService().getPlayerCrew(player.getUniqueId());

                        if (crew == null) {
                            player.sendMessage(Utils.formatText("<red>You are not in a crew."));
                            return;
                        }

                        CrewMember senderMember = SeabornCrews.getCrewService().getCrewMember(crew.getId(), player.getUniqueId());
                        if (!List.of("quartermaster", "captain").contains(senderMember.getRole().toLowerCase())) {
                            player.sendMessage(Utils.formatText("<red>You have to be a quartermaster or the captain in order to kick members."));
                            return;
                        }

                        List<CrewMember> members = SeabornCrews.getCrewService().getCrewMembers(crew.getId());
                        for (CrewMember member : members) {
                            OfflinePlayer offlineMember = member.asOfflinePlayer();
                            if (offlineMember == null) continue;
                            if (offlineMember.getName() == null) continue;
                            if (offlineMember.getName().equals(player.getName())) continue;

                            if (offlineMember.getName().equals(memberName)) {
                                if (member.getMemberUuid().equals(crew.getOwnerUuid())) {
                                    player.sendMessage(Utils.formatText("<red>You cannot kick the crew's captain."));
                                    return;
                                }

                                SeabornCrews.getIslandService().kickFromIsland(crew.getIslandWorld(), UUID.fromString(member.getMemberUuid()));
                                SeabornCrews.getCrewService().removeMember(crew.getId(), member.getMemberUuid());
                                player.sendMessage(Utils.formatText("<green>Kicked " + offlineMember.getName() + "!"));
                                return;
                            }
                        }

                        player.sendMessage(Utils.formatText("<red>Member not found."));

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

    }

    public CommandAPICommand getTransfer() {

        ArgumentSuggestions<CommandSender> memberSuggestions = ArgumentSuggestions.strings(info -> {
            Player player = (Player) info.sender();
            try {
                Crew crew = SeabornCrews.getCrewService().getPlayerCrew(player.getUniqueId());
                if (crew == null) return new String[]{};

                List<CrewMember> members = SeabornCrews.getCrewService().getCrewMembers(crew.getId());
                return members.stream()
                        .map(m -> m.asOfflinePlayer().getName())
                        .filter(Objects::nonNull)
                        .filter(p -> !p.equals(player.getName()))
                        .toArray(String[]::new);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });

        return new CommandAPICommand("transfer")
                .withArguments(
                        new StringArgument("member").replaceSuggestions(memberSuggestions)
                )
                .executes((sender, args) -> {
                    Player player = (Player) sender;
                    String memberName = (String) args.get("member");

                    try {
                        Crew crew = SeabornCrews.getCrewService().getPlayerCrew(player.getUniqueId());
                        if (crew == null) {
                            player.sendMessage(Utils.formatText("<red>You are not in a crew."));
                            return;
                        }

                        if (!crew.getOwnerUuid().equals(player.getUniqueId().toString())) {
                            player.sendMessage(Utils.formatText("<red>Only the captain can transfer the crew ownership."));
                            return;
                        }

                        List<CrewMember> members = SeabornCrews.getCrewService().getCrewMembers(crew.getId());
                        for (CrewMember member : members) {
                            OfflinePlayer offlineMember = member.asOfflinePlayer();
                            if (offlineMember == null) continue;
                            if (offlineMember.getName() == null) continue;
                            if (offlineMember.getName().equals(player.getName())) continue;

                            if (offlineMember.getName().equals(memberName)) {
                                crew.setOwnerUuid(member.getMemberUuid());
                                SeabornCrews.getCrewService().saveCrew(crew);
                                SeabornCrews.getCrewService().changeMemberRole(crew.getId(), player.getUniqueId().toString(), "quartermaster");
                                SeabornCrews.getCrewService().changeMemberRole(crew.getId(), member.getMemberUuid(), "captain");
                                player.sendMessage(Utils.formatText("<green>Transferred the crew's ownership to " + offlineMember.getName()));
                                return;
                            }
                        }

                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    public CommandAPICommand getCommand() { return command; }

}
