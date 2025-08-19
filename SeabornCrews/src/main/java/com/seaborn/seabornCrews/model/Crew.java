package com.seaborn.seabornCrews.model;

import java.time.LocalDateTime;

public class Crew {
    private int id;
    private String name;
    private String ownerUuid;
    private int reputation;
    private String description;
    private String tag;
    private String islandWorld;
    private int battleWins;
    private boolean isPublic;
    private int maxMembers;
    private LocalDateTime createdAt;

    // Constructors
    public Crew() {
    }

    public Crew(int id, String name, String ownerUuid, int reputation, String description, String tag,
                String islandWorld, int battleWins, boolean isPublic, int maxMembers, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.ownerUuid = ownerUuid;
        this.reputation = reputation;
        this.description = description;
        this.tag = tag;
        this.islandWorld = islandWorld;
        this.battleWins = battleWins;
        this.isPublic = isPublic;
        this.maxMembers = maxMembers;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerUuid() {
        return ownerUuid;
    }

    public void setOwnerUuid(String ownerUuid) {
        this.ownerUuid = ownerUuid;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getIslandWorld() {
        return islandWorld;
    }

    public void setIslandWorld(String islandWorld) {
        this.islandWorld = islandWorld;
    }

    public int getBattleWins() {
        return battleWins;
    }

    public void setBattleWins(int battleWins) {
        this.battleWins = battleWins;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
