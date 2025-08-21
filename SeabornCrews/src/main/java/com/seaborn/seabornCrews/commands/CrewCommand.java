package com.seaborn.seabornCrews.commands;

import com.seaborn.seabornCrews.SeabornCrews;
import com.seaborn.seabornCrews.Utils;
import com.seaborn.seabornCrews.model.Crew;
import com.seaborn.seabornCrews.model.CrewMember;
import com.seaborn.seabornCrews.service.CrewService;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.GreedyStringArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class CrewCommand {

    private CommandAPICommand command;

    public CrewCommand() {
        this.command = new CommandAPICommand("crew")
                .withSubcommands(
                        getCreate(),
                        //getJoin(),
                        //getLeave(),
                        getHome(),
                        //getKick(),
                        getDisband()
                        //getInfo()
                )
                .executes((sender, args) -> {});
    }

    public CommandAPICommand getCreate() {
        return new CommandAPICommand("create")
                .withArguments(new StringArgument("tag"), new GreedyStringArgument("name"))
                .executes((sender, args) -> {

                    if (!(sender instanceof Player)) return;

                    Player player = (Player) sender;

                    if (SeabornCrews.getCrewService().isPlayerInAnyCrew(player.getUniqueId())) {
                        player.sendMessage(Utils.formatText("<red>You are already in a crew. You can leave your current crew with /crew leave"));
                        return;
                    }

                    String tag = ((String) Objects.requireNonNull(args.get("tag"))).toUpperCase();
                    String name = (String) args.get("name");

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
                        sender.sendMessage(Utils.formatText("<red>An error ocurred while creating the crew."));
                        sender.sendMessage(Utils.formatText("<red> + e.toString()"));
                    }

                    // Create world
                    Crew finalCrew = crew;
                    SeabornCrews.getIslandService().createIsland(crew.getId()).thenAccept((worldId) -> {

                        if (worldId == null) {
                            sender.sendMessage(Utils.formatText("<red>An error ocurred while creating the crew's island."));
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
                            sender.sendMessage(Utils.formatText("<red>An error ocurred while creating the crew."));
                            throw new RuntimeException(e);
                        }
                        sender.sendMessage(Utils.formatText("<green>Crew created!"));
                    });

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
    //public CommandAPICommand getInfo() {}
    //public CommandAPICommand getJoin() {}
    //public CommandAPICommand getKick() {}
    //public CommandAPICommand getLeave() {}

    public CommandAPICommand getCommand() { return command; }

}
