package com.seabornmc.seabornDialogs.dialogs;

import com.seabornmc.seabornDialogs.Display;
import com.seabornmc.seabornDialogs.SeabornDialogs;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class OptionDialog extends Dialog {
    private List<String> options = new ArrayList<>();
    private Function<Integer, Void> onSelectFunc;

    public OptionDialog(Location location, Player player, List<String> options) {
        this(location, player);
        this.options = options;
    }

    public OptionDialog(Location location, Player player) {
        super(location, player);
    }

    private String addSpaces(String string,int spaces){
        StringBuilder stringBuilder = new StringBuilder(string);
        for (int i = 0; i < spaces; i++) {
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    @Override
    public void spawn() {
        double yOffset = 0;
        int index = 0;
        int maxLength = 0;

        for (String option : options) {
            if (option.length() > maxLength) maxLength = option.length();
        }

        for (String option : options) {

            String finalText = addSpaces(option, maxLength - option.length());

            Location current = this.getLocation().clone().add(new Vector(0, yOffset, 0));

            Display display = SeabornDialogs.getDisplayManager().createDisplay(current, finalText);
            display.setInteractable(true);
            display.setDialog(this);

            int finalIndex = index;
            display.onInteract((player) -> {
                this.callOnSelect(finalIndex);
                return null;
            });

            this.getDisplays().add(display);

            SeabornDialogs.getDisplayManager().showDisplayToPlayer(display, this.getPlayer());

            yOffset -= .15;
            index++;
        }
    }

    public void addOption(String option) {
        this.options.add(option);
    }

    public OptionDialog onSelect(Function<Integer, Void> func) {
        this.onSelectFunc = func;
        return this;
    }

    public void callOnSelect(int index) {
        this.onSelectFunc.apply(index);
    }


}
