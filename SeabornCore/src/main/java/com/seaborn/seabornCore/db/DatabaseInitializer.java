package com.seaborn.seabornCore.db;

import com.seaborn.seabornCore.SeabornCore;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.stream.Collectors;

public class DatabaseInitializer {

    public static void runMigrations(DatabaseManager dbManager) {
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {

            // Load db.sql from resources
            InputStream input = DatabaseInitializer.class.getClassLoader().getResourceAsStream("db.sql");
            if (input == null) {
                throw new RuntimeException("db.sql not found in resources!");
            }

            String sql = new BufferedReader(new InputStreamReader(input))
                    .lines()
                    .collect(Collectors.joining("\n"));

            // Split on semicolons if multiple statements
            for (String query : sql.split(";")) {
                if (!query.trim().isEmpty()) {
                    stmt.execute(query.trim());
                }
            }

            // Step 2: Run incremental migrations
            ensureColumnExists(conn, "players", "last_world_name", "VARCHAR(128) DEFAULT NULL");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to run database migrations!", e);
        }

    }

    private static void ensureColumnExists(Connection conn, String table, String column, String definition) throws SQLException {
        String checkSql = "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS " +
                "WHERE TABLE_NAME = ? AND COLUMN_NAME = ? AND TABLE_SCHEMA = DATABASE()";
        try (PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setString(1, table);
            ps.setString(2, column);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    String alterSql = "ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition;
                    try (Statement alter = conn.createStatement()) {
                        alter.executeUpdate(alterSql);
                        SeabornCore.getInstance().getLogger().info("[DB] Added column " + column + " to " + table);
                    }
                }
            }
        }
    }
}
