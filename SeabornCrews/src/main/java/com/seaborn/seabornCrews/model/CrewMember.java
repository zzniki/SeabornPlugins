package com.seaborn.seabornCrews.model;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class CrewMember {
    private int crewId;
    private String memberUuid;
    private String role; // "member", "admin", "owner"

    public CrewMember() {}

    public CrewMember(int crewId, String memberUuid, String role) {
        this.crewId = crewId;
        this.memberUuid = memberUuid;
        this.role = role;
    }

    public OfflinePlayer asOfflinePlayer() {
        return Bukkit.getOfflinePlayer(this.memberUuid);
    }

    public int getCrewId() {
        return crewId;
    }

    public void setCrewId(int crewId) {
        this.crewId = crewId;
    }

    public String getMemberUuid() {
        return memberUuid;
    }

    public void setMemberUuid(String memberUuid) {
        this.memberUuid = memberUuid;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}