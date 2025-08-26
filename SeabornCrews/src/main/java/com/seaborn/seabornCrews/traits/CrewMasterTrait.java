package com.seaborn.seabornCrews.traits;

import com.seaborn.seabornCrews.SeabornCrews;
import com.seaborn.seabornCrews.menus.CrewMasterMenu;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.sql.SQLException;

@TraitName("crewmaster")
public class CrewMasterTrait extends Trait {

    public CrewMasterTrait() {
        super("crewmaster");
    }

    @EventHandler
    public void click(NPCRightClickEvent event) {
        if (event.getNPC().hasTrait(this.getClass())) {

            Player player = event.getClicker();

            try {
                if (SeabornCrews.getCrewService().isPlayerInAnyCrew(player.getUniqueId())) {
                    player.sendMessage("you're in a crew");
                } else {
                    new CrewMasterMenu(event.getClicker()).show();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
