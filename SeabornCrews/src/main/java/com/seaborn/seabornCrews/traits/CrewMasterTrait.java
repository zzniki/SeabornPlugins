package com.seaborn.seabornCrews.traits;

import com.seaborn.seabornCore.SeabornCore;
import com.seaborn.seabornCrews.SeabornCrews;
import com.seaborn.seabornCrews.menus.CrewMasterMenu;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@TraitName("crewmaster")
public class CrewMasterTrait extends Trait {

    private List<Player> showingDialog = new ArrayList<>();

    public CrewMasterTrait() {
        super("crewmaster");
    }

    @EventHandler
    public void click(NPCRightClickEvent event) {
        if (event.getNPC().hasTrait(this.getClass())) {

            Player player = event.getClicker();

            try {
                if (SeabornCrews.getCrewService().isPlayerInAnyCrew(player.getUniqueId())) {
                    SeabornCore.getDialogueManager().showDialogue(
                            event.getClicker(),
                            "You are already in a crew, I am not of any help.",
                            (none) -> {return null;}
                    );
                } else {
                    if (!this.showingDialog.contains(player)) {
                        this.showingDialog.add(player);
                        SeabornCore.getDialogueManager().showDialogue(
                                event.getClicker(),
                                "Let's get you started with crews!",
                                (none) -> {
                                    SeabornCore.getDialogueManager().showDialogue(
                                            event.getClicker(),
                                            "All crews have a private island and can participate in crew battles.",
                                            (none2) -> {
                                                this.showingDialog.remove(player);
                                                new CrewMasterMenu(event.getClicker()).show();
                                                return null;
                                            }
                                    );
                                    return null;
                                }
                        );
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
