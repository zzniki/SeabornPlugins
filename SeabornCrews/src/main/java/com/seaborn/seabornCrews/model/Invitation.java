package com.seaborn.seabornCrews.model;

import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public class Invitation {

    private Crew crew;
    private Player invited;
    private LocalDateTime time;

    public Invitation(Crew crew, Player invited) {
        this.crew = crew;
        this.invited = invited;
        this.time = LocalDateTime.now();
    }

    public Crew getCrew() {
        return crew;
    }

    public Player getInvited() {
        return invited;
    }
}
