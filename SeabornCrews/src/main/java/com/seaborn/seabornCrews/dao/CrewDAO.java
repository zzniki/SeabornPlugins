package com.seaborn.seabornCrews.dao;

import com.seaborn.seabornCrews.model.Crew;
import com.seaborn.seabornCrews.model.CrewMember;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrewDAO {

    private final HikariDataSource dataSource;

    public CrewDAO(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Create a new crew
    public void createCrew(Crew crew) throws SQLException {
        String sql = "INSERT INTO crews (name, owner_uuid, reputation, description, tag, island_world, battle_wins, is_public, max_members) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, crew.getName());
            stmt.setString(2, crew.getOwnerUuid());
            stmt.setInt(3, crew.getReputation());
            stmt.setString(4, crew.getDescription());
            stmt.setString(5, crew.getTag());
            stmt.setString(6, crew.getIslandWorld());
            stmt.setInt(7, crew.getBattleWins());
            stmt.setBoolean(8, crew.isPublic());
            stmt.setInt(9, crew.getMaxMembers());

            stmt.executeUpdate();

            try (ResultSet keys = stmt.getGeneratedKeys()) {
                if (keys.next()) {
                    crew.setId(keys.getInt(1));
                }
            }
        }
    }

    // Get crew by id
    public Crew getCrewById(int id) throws SQLException {
        String sql = "SELECT * FROM crews WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapCrew(rs);
                }
            }
        }
        return null;
    }

    // List all crew members
    public List<CrewMember> getCrewMembers(int crewId) throws SQLException {
        List<CrewMember> members = new ArrayList<>();
        String sql = "SELECT * FROM crew_members WHERE crew_id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, crewId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    members.add(new CrewMember(
                            rs.getInt("crew_id"),
                            rs.getString("member_uuid"),
                            rs.getString("role")
                    ));
                }
            }
        }
        return members;
    }

    // Add member
    public void addMember(CrewMember member) throws SQLException {
        String sql = "INSERT INTO crew_members (crew_id, member_uuid, role) VALUES (?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, member.getCrewId());
            stmt.setString(2, member.getMemberUuid());
            stmt.setString(3, member.getRole());
            stmt.executeUpdate();
        }
    }

    // Remove member
    public void removeMember(int crewId, String memberUuid) throws SQLException {
        String sql = "DELETE FROM crew_members WHERE crew_id = ? AND member_uuid = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, crewId);
            stmt.setString(2, memberUuid);
            stmt.executeUpdate();
        }
    }

    // Map ResultSet to Crew
    private Crew mapCrew(ResultSet rs) throws SQLException {
        return new Crew(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("owner_uuid"),
                rs.getInt("reputation"),
                rs.getString("description"),
                rs.getString("tag"),
                rs.getString("island_world"),
                rs.getInt("battle_wins"),
                rs.getBoolean("is_public"),
                rs.getInt("max_members"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }

    // Get crew ID by member UUID (returns null if not in any crew)
    public Integer getCrewIdByMember(UUID memberUuid) {
        String query = "SELECT crew_id FROM crew_members WHERE member_uuid = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, memberUuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("crew_id");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveCrew(Crew crew) throws SQLException {
        String sql = "UPDATE crews SET name = ?, owner_uuid = ?, reputation = ?, description = ?, " +
                "tag = ?, island_world = ?, battle_wins = ?, is_public = ?, max_members = ? " +
                "WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, crew.getName());
            stmt.setString(2, crew.getOwnerUuid());
            stmt.setInt(3, crew.getReputation());
            stmt.setString(4, crew.getDescription());
            stmt.setString(5, crew.getTag());
            stmt.setString(6, crew.getIslandWorld());
            stmt.setInt(7, crew.getBattleWins());
            stmt.setBoolean(8, crew.isPublic());
            stmt.setInt(9, crew.getMaxMembers());
            stmt.setInt(10, crew.getId());

            stmt.executeUpdate();
        }
    }

    public void deleteCrew(int crewId) throws SQLException {
        String sql = "DELETE FROM crews WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, crewId);
            stmt.executeUpdate();
        }
    }

    // List all crew members
    public CrewMember getCrewMember(int crewId, UUID memberUuid) throws SQLException {
        List<CrewMember> members = new ArrayList<>();
        String sql = "SELECT * FROM crew_members WHERE crew_id = ? AND member_uuid = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, crewId);
            stmt.setString(2, memberUuid.toString());

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    return new CrewMember(
                            rs.getInt("crew_id"),
                            rs.getString("member_uuid"),
                            rs.getString("role")
                    );
                }
            }
        }
        return null;
    }

    public Crew getCrewByName(String name) throws SQLException {
        String sql = "SELECT * FROM crews WHERE name = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapCrew(rs);
            }
        }
        return null;
    }

    public Crew getCrewByTag(String tag) throws SQLException {
        String sql = "SELECT * FROM crews WHERE tag = ?";
        try (Connection conn = dataSource.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, tag);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapCrew(rs);
            }
        }
        return null;
    }

}
