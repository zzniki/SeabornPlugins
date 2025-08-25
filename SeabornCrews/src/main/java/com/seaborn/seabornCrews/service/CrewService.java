package com.seaborn.seabornCrews.service;

import com.seaborn.seabornCrews.cache.CrewCache;
import com.seaborn.seabornCrews.dao.CrewDAO;
import com.seaborn.seabornCrews.model.Crew;
import com.seaborn.seabornCrews.model.CrewMember;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class CrewService {

    private final CrewDAO crewDAO;
    private final CrewCache crewCache;

    public CrewService(CrewDAO crewDAO) {
        this.crewDAO = crewDAO;
        this.crewCache = new CrewCache();
    }

    public Crew createCrew(Crew crew) throws SQLException {
        crewDAO.createCrew(crew);
        CrewMember owner = new CrewMember(crew.getId(), crew.getOwnerUuid(), "captain");
        crewDAO.addMember(owner);

        // cache
        crewCache.putCrew(crew);
        crewCache.addCrewMember(crew.getId(), owner);
        return crew;
    }

    public Crew getCrew(int id) throws SQLException {
        Crew cached = crewCache.getCrew(id);
        if (cached != null) return cached;

        Crew db = crewDAO.getCrewById(id);
        if (db != null) crewCache.putCrew(db);
        return db;
    }

    public List<CrewMember> getCrewMembers(int crewId) throws SQLException {
        List<CrewMember> cached = crewCache.getCrewMembers(crewId);
        if (cached != null) return cached;

        List<CrewMember> db = crewDAO.getCrewMembers(crewId);
        crewCache.putCrewMembers(crewId, db);
        return db;
    }

    public void addMember(int crewId, String playerUuid, String role) throws SQLException {
        CrewMember member = new CrewMember(crewId, playerUuid, role);
        crewDAO.addMember(member);
        crewCache.addCrewMember(crewId, member);
    }

    public void removeMember(int crewId, String playerUuid) throws SQLException {
        crewDAO.removeMember(crewId, playerUuid);
        crewCache.removeCrewMember(crewId, playerUuid);
    }

    public CrewMember getOwnerAsCrewMember(int crewId) throws SQLException {
        Crew crew = getCrew(crewId); // hits cache first
        return new CrewMember(crew.getId(), crew.getOwnerUuid(), "captain");
    }

    public boolean isPlayerInCrew(int crewId, UUID playerUuid) throws SQLException {
        return getCrewMembers(crewId).stream()
                .anyMatch(m -> m.getMemberUuid().equals(playerUuid.toString()));
    }

    public boolean isPlayerInAnyCrew(UUID playerUuid) throws SQLException {
        // cache first
        Integer cached = crewCache.getCrewIdForMember(playerUuid.toString());
        if (cached != null) return true;

        Integer db = crewDAO.getCrewIdByMember(playerUuid);
        if (db != null) {
            // Warm member index by caching the full crew member list (keeps cache consistent)
            List<CrewMember> members = crewDAO.getCrewMembers(db);
            crewCache.putCrewMembers(db, members);
            return true;
        }
        return false;
    }

    public Crew getPlayerCrew(UUID playerUuid) throws SQLException {
        Integer crewId = crewCache.getCrewIdForMember(playerUuid.toString());
        if (crewId == null) {
            crewId = crewDAO.getCrewIdByMember(playerUuid);
            if (crewId == null) return null;

            // Warm both crew + members (helps chat prefix lookups etc.)
            Crew crew = crewDAO.getCrewById(crewId);
            if (crew != null) crewCache.putCrew(crew);
            List<CrewMember> members = crewDAO.getCrewMembers(crewId);
            crewCache.putCrewMembers(crewId, members);
        }
        return getCrew(crewId);
    }

    public void saveCrew(Crew crew) throws SQLException {
        crewDAO.saveCrew(crew);
        crewCache.putCrew(crew); // refresh cache
    }

    public void deleteCrew(int crewId) throws SQLException {
        crewDAO.deleteCrew(crewId);
        crewCache.removeCrew(crewId);
    }

    public CrewMember getCrewMember(int crewId, UUID memberUuid) throws SQLException {
        CrewMember cached = crewCache.getCrewMember(crewId, memberUuid.toString());
        if (cached != null) return cached;

        CrewMember db = crewDAO.getCrewMember(crewId, memberUuid);
        if (db != null) crewCache.addCrewMember(crewId, db);
        return db;
    }

    public boolean isCrewNameTaken(String name) throws SQLException {
        // Name/Tag lookups usually don't benefit from caching unless you add a map; keep DB for simplicity
        return crewDAO.getCrewByName(name) != null;
    }

    public boolean isCrewTagTaken(String tag) throws SQLException {
        return crewDAO.getCrewByTag(tag) != null;
    }

    public void clearCache() {
        crewCache.clear();
    }

    public CrewCache getCache() { return this.crewCache; }

}
