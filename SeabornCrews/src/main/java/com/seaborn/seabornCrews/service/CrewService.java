package com.seaborn.seabornCrews.service;

import com.seaborn.seabornCrews.dao.CrewDAO;
import com.seaborn.seabornCrews.model.Crew;
import com.seaborn.seabornCrews.model.CrewMember;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CrewService {

    private final CrewDAO crewDAO;

    public CrewService(CrewDAO crewDAO) {
        this.crewDAO = crewDAO;
    }

    public Crew createCrew(Crew crew) throws SQLException {
        crewDAO.createCrew(crew);
        // Add owner as member with role 'owner'
        crewDAO.addMember(new CrewMember(crew.getId(), crew.getOwnerUuid(), "owner"));
        return crew;
    }

    public Crew getCrew(int id) throws SQLException {
        return crewDAO.getCrewById(id);
    }

    public List<CrewMember> getCrewMembers(int crewId) throws SQLException {
        return crewDAO.getCrewMembers(crewId);
    }

    public void addMember(int crewId, String playerUuid, String role) throws SQLException {
        crewDAO.addMember(new CrewMember(crewId, playerUuid, role));
    }

    public void removeMember(int crewId, String playerUuid) throws SQLException {
        crewDAO.removeMember(crewId, playerUuid);
    }

    public CrewMember getOwnerAsCrewMember(int crewId) throws SQLException {
        Crew crew = crewDAO.getCrewById(crewId);
        CrewMember owner = new CrewMember();
        owner.setCrewId(crew.getId());
        owner.setMemberUuid(crew.getOwnerUuid());
        owner.setRole("owner");
        return owner;
    }

    public boolean isPlayerInCrew(int crewId, UUID playerUuid) throws SQLException {
        return getCrewMembers(crewId).stream()
                .anyMatch(member -> member.getMemberUuid().equals(playerUuid.toString()));
    }

    public boolean isPlayerInAnyCrew(UUID playerUuid) {
        return crewDAO.getCrewIdByMember(playerUuid) != null;
    }

    public Crew getPlayerCrew(UUID playerUuid) throws SQLException {
        Integer crewId = crewDAO.getCrewIdByMember(playerUuid);
        if (crewId == null) return null;
        return getCrew(crewId);
    }
}
