package com.ellisiumx.elrankup.drop.repository;

import com.ellisiumx.elcore.database.DBPool;
import com.ellisiumx.elcore.database.RepositoryBase;
import com.ellisiumx.elrankup.drop.PlayerDrops;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;

public class DropRepository extends RepositoryBase {

    public DropRepository(JavaPlugin plugin) {
        super(plugin, DBPool.getDataSource("rankup"));
    }

    @Override
    protected void initialize() {
    }

    @Override
    protected void update() {
    }

    public PlayerDrops getPlayerDrops(int id) {
        PlayerDrops playerDrops = null;
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT IGNORE INTO drops (accountId, drops) VALUES (?, ?);")) {
                statement.setInt(1, id);
                statement.setLong(2, 0L);
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement("SELECT drops FROM drops WHERE accountId = ? LIMIT 1;")) {
                statement.setInt(1, id);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while(resultSet.next()) {
                        long drops = resultSet.getLong(1);
                        playerDrops = new PlayerDrops(drops);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return playerDrops;
    }
}
