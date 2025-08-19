package com.seaborn.seabornCrews;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;
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

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to run database migrations!", e);
        }
    }
}