package com.seaborn.seabornCrews.commands;

import com.seaborn.seabornCrews.SeabornCrews;
import com.seaborn.seabornCrews.Utils;
import dev.jorel.commandapi.CommandAPICommand;
import org.bukkit.entity.Player;

public class CrewAdminCommand {

    private CommandAPICommand command;

    public CrewAdminCommand() {
        this.command = new CommandAPICommand("crewadmin")
                .withPermission("seaborn.crew.admin")
                .withSubcommands(
                        getSetLocation()
                );
    }

    public CommandAPICommand getSetLocation() {
        return new CommandAPICommand("setlocation")
                .withPermission("seaborn.crew.admin.setlocation")
                .executes((sender, args) -> {
                    Player player = (Player) sender;

                    SeabornCrews.getInstance().getConfig().set("exit-location", player.getLocation());
                    sender.sendMessage(Utils.formatText("<green>Set!"));
                });
    }

    public CommandAPICommand getCommand() { return this.command; }

}
