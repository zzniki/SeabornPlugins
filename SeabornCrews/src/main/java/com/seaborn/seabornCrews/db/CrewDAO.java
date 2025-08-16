package com.seaborn.seabornCrews.db;

import com.seaborn.seabornCrews.model.Crew;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CrewDAO {
    private final DataSource ds;
    private final Logger logger;

    public CrewDAO(DataSource ds, Logger logger) {
        this.ds = ds;
        this.logger = logger;
    }

    public void createTables() throws SQLException {
        try (Connection c = ds.getConnection(); Statement s = c.createStatement()) {
            s.executeUpdate("CREATE TABLE IF NOT EXISTS crews (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(64) UNIQUE NOT NULL, owner_uuid VARCHAR(36) NOT NULL, reputation INT NOT NULL DEFAULT 0, island_world VARCHAR(128) DEFAULT NULL)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS crew_members (crew_id INT NOT NULL, member_uuid VARCHAR(36) NOT NULL, PRIMARY KEY (crew_id, member_uuid), FOREIGN KEY (crew_id) REFERENCES crews(id) ON DELETE CASCADE)");
        }
    }

    public Optional<Crew> createCrew(String name, UUID owner) {
        String insert = "INSERT INTO crews (name, owner_uuid) VALUES (?, ?)";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(insert, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.setString(2, owner.toString());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    // Remove owner from all other crews before adding
                    removeMemberFromAllCrews(owner);
                    addMember(id, owner);
                    // Reload the crew from DB to ensure all fields (including correct id) are set
                    return findByName(name);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "createCrew error", e);
        }
        return Optional.empty();
    }

    public boolean addMember(int crewId, UUID member) throws SQLException {
        String ins = "INSERT INTO crew_members (crew_id, member_uuid) VALUES (?, ?)";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(ins)) {
            ps.setInt(1, crewId);
            ps.setString(2, member.toString());
            ps.executeUpdate();
            return true;
        }
    }

    public boolean removeMember(int crewId, UUID member) throws SQLException {
        String del = "DELETE FROM crew_members WHERE crew_id = ? AND member_uuid = ?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(del)) {
            ps.setInt(1, crewId);
            ps.setString(2, member.toString());
            return ps.executeUpdate() > 0;
        }
    }

    public void removeMemberFromAllCrews(UUID member) {
        String del = "DELETE FROM crew_members WHERE member_uuid = ?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(del)) {
            ps.setString(1, member.toString());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "removeMemberFromAllCrews error", e);
        }
    }

    public Optional<Crew> findByName(String name) {
        String q = "SELECT id, name, owner_uuid, reputation, island_world FROM crews WHERE name = ?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String nm = rs.getString("name");
                    UUID owner = UUID.fromString(rs.getString("owner_uuid"));
                    int rep = rs.getInt("reputation");
                    String island = rs.getString("island_world");
                    Crew crew = new Crew(id, nm, owner, rep, island);
                    // load members
                    String mQ = "SELECT member_uuid FROM crew_members WHERE crew_id = ?";
                    try (PreparedStatement mps = c.prepareStatement(mQ)) {
                        mps.setInt(1, id);
                        try (ResultSet mrs = mps.executeQuery()) {
                            while (mrs.next()) {
                                crew.getMembers().add(UUID.fromString(mrs.getString("member_uuid")));
                            }
                        }
                    }
                    return Optional.of(crew);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "findByName error", e);
        }
        return Optional.empty();
    }

    public Optional<Crew> findByMember(UUID player) {
        String q = "SELECT c.id, c.name, c.owner_uuid, c.reputation, c.island_world FROM crews c JOIN crew_members m ON c.id = m.crew_id WHERE m.member_uuid = ?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, player.toString());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String nm = rs.getString("name");
                    UUID owner = UUID.fromString(rs.getString("owner_uuid"));
                    int rep = rs.getInt("reputation");
                    String island = rs.getString("island_world");
                    Crew crew = new Crew(id, nm, owner, rep, island);
                    // load members
                    String mQ = "SELECT member_uuid FROM crew_members WHERE crew_id = ?";
                    try (PreparedStatement mps = c.prepareStatement(mQ)) {
                        mps.setInt(1, id);
                        try (ResultSet mrs = mps.executeQuery()) {
                            while (mrs.next()) {
                                crew.getMembers().add(UUID.fromString(mrs.getString("member_uuid")));
                            }
                        }
                    }
                    return Optional.of(crew);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "findByMember error", e);
        }
        return Optional.empty();
    }

    public boolean setIslandWorldName(int crewId, String worldName) {
        String u = "UPDATE crews SET island_world = ? WHERE id = ?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(u)) {
            ps.setString(1, worldName);
            ps.setInt(2, crewId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "setIslandWorldName", e);
            return false;
        }
    }

    public boolean changeReputation(int crewId, int delta) {
        String u = "UPDATE crews SET reputation = reputation + ? WHERE id = ?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(u)) {
            ps.setInt(1, delta);
            ps.setInt(2, crewId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "changeReputation", e);
            return false;
        }
    }

    public Optional<Integer> getMemberCount(int crewId) {
        String q = "SELECT COUNT(*) AS cnt FROM crew_members WHERE crew_id = ?";
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setInt(1, crewId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(rs.getInt("cnt"));
            }
        } catch (SQLException e) { logger.log(Level.SEVERE, "getMemberCount", e); }
        return Optional.empty();
    }

    public void deleteCrew(int crewId) {
        try (Connection c = ds.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM crews WHERE id = ?")) {
            ps.setInt(1, crewId);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "deleteCrew error", e);
        }
    }
}
