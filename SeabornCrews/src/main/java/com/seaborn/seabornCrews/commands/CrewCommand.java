package com.seaborn.seabornCrews.commands;

import dev.jorel.commandapi.CommandAPICommand;
import com.seaborn.seabornCrews.CrewService;
import dev.jorel.commandapi.arguments.PlayerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class CrewCommand {

    public CommandAPICommand register(CrewService service) {

        // /crew create <name>
        return new CommandAPICommand("crew")
                .withSubcommand(new CommandAPICommand("create")
                        .withArguments(new StringArgument("name"))
                        .executesPlayer((player, args) -> {
                            String name = (String) args.get("name");
                            service.createCrewAsync(name, player.getUniqueId()).thenAccept(opt -> {
                                if (opt.isPresent()) {
                                    player.sendMessage("Crew created: " + name + " - creating island...");
                                    service.createIslandForCrewAsync(opt.get()).thenAccept(islandOpt -> {
                                        if (islandOpt.isPresent()) {
                                            player.sendMessage("Island created: " + islandOpt.get());
                                        } else {
                                            player.sendMessage("Island creation failed or API unavailable.");
                                        }
                                    });
                                } else {
                                    player.sendMessage("Could not create crew (name taken?).");
                                }
                            });
                        })
                )

                // /crew join <name>
                .withSubcommand(new CommandAPICommand("join")
                        .withArguments(new StringArgument("name"))
                        .executesPlayer((player, args) -> {
                            service.getCrewByName((String) args.get("name")).ifPresentOrElse(crew -> {
                                boolean ok = service.addMember(crew, player.getUniqueId());
                                player.sendMessage(ok ? "Joined crew " + crew.getName() : "Could not join (maybe full)");
                            }, () -> player.sendMessage("Crew not found."));
                        })
                )

                // /crew leave
                .withSubcommand(new CommandAPICommand("leave")
                        .executesPlayer((player, args) -> {
                            service.getCrewByMember(player.getUniqueId()).ifPresentOrElse(crew -> {
                                boolean ok = service.removeMember(crew, player.getUniqueId());
                                player.sendMessage(ok ? "You left the crew " + crew.getName() : "Could not leave the crew");
                            }, () -> player.sendMessage("You are not in a crew."));
                        })
                )

                // /crew kick <player>
                .withSubcommand(new CommandAPICommand("kick")
                        .withArguments(new PlayerArgument("target"))
                        .executesPlayer((player, args) -> {
                            Player target = (Player) args.get("target");
                            service.getCrewByMember(player.getUniqueId()).ifPresentOrElse(crew -> {
                                if (!crew.getOwner().equals(player.getUniqueId()) && !player.hasPermission("seaborncrews.admin")) {
                                    player.sendMessage("Only crew owner may kick");
                                    return;
                                }
                                boolean ok = service.removeMember(crew, target.getUniqueId());
                                player.sendMessage(ok ? "Kicked " + target.getName() : "Could not kick");
                            }, () -> player.sendMessage("You are not in a crew."));
                        })
                )

                // /crew home
                .withSubcommand(new CommandAPICommand("home")
                        .executesPlayer((player, args) -> {
                            service.getCrewByMember(player.getUniqueId()).ifPresentOrElse(crew -> {
                                service.teleportToIsland(player, crew);
                            }, () -> player.sendMessage("You are not in a crew."));
                        })
                )

                // /crew info <name>
                .withSubcommand(new CommandAPICommand("info")
                        .withArguments(new StringArgument("name"))
                        .executesPlayer((player, args) -> {
                            service.getCrewByName((String) args.get("name")).ifPresentOrElse(crew -> {
                                player.sendMessage("Crew: " + crew.getName());
                                player.sendMessage("Owner: " + crew.getOwner());
                                player.sendMessage("Members: " + crew.getMembers().size() + "/25");
                                player.sendMessage("Reputation: " + crew.getReputation());
                            }, () -> player.sendMessage("Crew not found."));
                        })
                )

                // /crew disband
                .withSubcommand(new CommandAPICommand("disband")
                        .executesPlayer((player, args) -> {
                            service.getCrewByMember(player.getUniqueId()).ifPresentOrElse(crew -> {
                                if (!crew.getOwner().equals(player.getUniqueId()) && !player.hasPermission("seaborncrews.admin")) {
                                    player.sendMessage("Only crew owner may disband");
                                    return;
                                }
                                service.disbandCrew(crew);
                                player.sendMessage("Crew disbanded and island deleted.");
                            }, () -> player.sendMessage("You are not in a crew."));
                        })
                );
    }
}
