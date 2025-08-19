package com.seaborn.seabornCrews.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Crew {
    private final int id;
    private final String name;
    private final UUID owner;
    private final List<UUID> members = new ArrayList<>();
    private int reputation;
    private String islandWorldName; // slime world name / identifier

    public Crew(int id, String name, UUID owner, int reputation, String islandWorldName) {
        this.id = id;
        this.name = name;
        this.owner = owner;
        this.reputation = reputation;
        this.islandWorldName = islandWorldName;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public UUID getOwner() { return owner; }
    public List<UUID> getMembers() { return members; }
    public int getReputation() { return reputation; }
    public void setReputation(int r) { reputation = r; }
    public String getIslandWorldName() { return islandWorldName; }
    public void setIslandWorldName(String n) { islandWorldName = n; }
}
