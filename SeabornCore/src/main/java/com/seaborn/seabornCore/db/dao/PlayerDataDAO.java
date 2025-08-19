package com.seaborn.seabornCore.db.dao;

import com.seaborn.seabornCore.db.model.PlayerData;
import com.seaborn.seabornCore.serialization.SerializeCommon;
import com.seaborn.seabornCore.serialization.SerializeInventory;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Location;
import org.bukkit.inventory.PlayerInventory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerDataDAO {

    private final HikariDataSource ds;

    public PlayerDataDAO(HikariDataSource ds) {
        this.ds = ds;
    }

    public void save(PlayerData playerData) {
        try {
            Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO players (uuid, location, inventory, money, reputation) " +
                    "VALUES (?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                            "inventory = VALUES(inventory), " +
                            "location = VALUES(location), " +
                            "money = VALUES(money), " +
                            "reputation = VALUES(reputation)"
            );

            ps.setString(1, playerData.getUuid().toString());
            ps.setString(2, SerializeCommon.toBase64(playerData.getLastLocation()));
            ps.setString(3, SerializeInventory.playerInventoryToBase64(playerData.getInventory()));
            ps.setDouble(4, playerData.getMoney());
            ps.setDouble(5, playerData.getReputation());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public PlayerData load(UUID uuid, PlayerInventory inventory) {
        try {
            Connection conn = ds.getConnection();
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT inventory, location, money, reputation FROM players WHERE uuid = ?"
            );

            ps.setString(1, uuid.toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    PlayerData data = new PlayerData(uuid);

                    SerializeInventory.base64ToPlayerInventory(rs.getString("inventory"), inventory);
                    data.setInventory(inventory);

                    data.setLastLocation((Location) SerializeCommon.fromBase64(rs.getString("location")));
                    data.setMoney(rs.getDouble("money"));
                    data.setReputation(rs.getDouble("reputation"));

                    return data;

                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}
