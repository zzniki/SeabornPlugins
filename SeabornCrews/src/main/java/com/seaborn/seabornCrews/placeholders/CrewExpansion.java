package com.seaborn.seabornCrews.placeholders;

import com.seaborn.seabornCrews.SeabornCrews;
import com.seaborn.seabornCrews.model.Crew;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;

import java.sql.SQLException;

public class CrewExpansion extends PlaceholderExpansion {

    @Override
    public String getAuthor() {
        return "Seaborn";
    }

    @Override
    public String getIdentifier() {
        return "crew";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onRequest(OfflinePlayer player, String params) {
        if (params.equalsIgnoreCase("name")) {
            try {
                Crew crew = SeabornCrews.getCrewService().getPlayerCrew(player.getUniqueId());
                if (crew == null) return "None";

                return crew.getName();
            } catch (SQLException e) {
                return "None";
            }
        }

        if (params.equalsIgnoreCase("tag")) {
            try {
                Crew crew = SeabornCrews.getCrewService().getPlayerCrew(player.getUniqueId());
                if (crew == null) return "None";

                return crew.getTag();
            } catch (SQLException e) {
                return "None";
            }
        }

        return null;
    }

}
