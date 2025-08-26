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
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
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
                        //getKick(),
                        getDisband(),
                        getInfo(),
                        getInvite(),
                        getAcceptInvite(),
                        getRejectInvite(),
                        getMenu(),
                        getTest()
                )
                .executes((sender, args) -> {

                    sender.sendMessage(Utils.formatText("<b><aqua>CREW COMMAND"));
                    sender.sendMessage(Utils.formatText("/crew invite <player> <dark_aqua>- Invites a player to your crew."));
                    sender.sendMessage(Utils.formatText("/crew leave <dark_aqua>- Leaves your current crew."));
                    sender.sendMessage(Utils.formatText("/crew home <dark_aqua>- Travels to your crew's island. (Requires rank)"));
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
                            sender.sendMessage(Utils.formatText("<red>You are not the crew's owner!"));
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
                    // TODO: Consider name

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

    //public CommandAPICommand getJoin() {}
    //public CommandAPICommand getKick() {}
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

                        SeabornCrews.getCrewService().removeMember(crew.getId(), player.getUniqueId().toString());
                        player.sendMessage(Utils.formatText("<green>You left " + crew.getName()));


                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }

                });
    }

    public CommandAPICommand getTest() {
        return new CommandAPICommand("test")
                .executes((sender, args) -> {
                    SeabornCore.getDialogueManager().showDialogue((Player) sender, "This is a test dialog", (none) -> {
                        ((Player) sender).sendMessage("ended dialog");

                        return null;
                    });
                });
    }

    public CommandAPICommand getCommand() { return command; }

}
