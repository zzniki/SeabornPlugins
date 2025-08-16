package com.seaborn.seabornFishing.traits;

import com.seaborn.seabornFishing.SeabornFishing;
import com.seaborn.seabornFishing.dialogs.FishmongerDialog;
import com.seabornmc.seabornDialogs.SeabornDialogs;
import com.seabornmc.seabornDialogs.dialogs.OptionDialog;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

import java.util.List;

@TraitName("fishmonger")
public class FishmongerTrait extends Trait {

    SeabornFishing plugin;

    public FishmongerTrait() {
        super("fishmonger");
        this.plugin = SeabornFishing.getInstance();
    }

    @EventHandler
    public void click(NPCRightClickEvent event) {
        if (event.getNPC().hasTrait(this.getClass())) {
            FishmongerDialog.create(event.getClicker(), event.getNPC());
        }
    }

}
