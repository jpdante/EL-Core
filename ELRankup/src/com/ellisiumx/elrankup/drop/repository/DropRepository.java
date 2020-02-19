package com.ellisiumx.elrankup.drop.repository;

import com.ellisiumx.elcore.database.DBPool;
import com.ellisiumx.elcore.database.RepositoryBase;
import com.ellisiumx.elcore.utils.UtilGson;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.drop.PlayerDrops;
import com.ellisiumx.elrankup.kit.KitStamp;
import com.ellisiumx.elrankup.kit.PlayerKit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Map;
import java.util.Stack;

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

    public PlayerDrops getPlayerDrops(int accountId) {
        PlayerDrops playerDrops = null;
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT IGNORE INTO drops (accountId, drops) VALUES (?, 0);")) {
                statement.setInt(1, accountId);
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement("SELECT drops FROM drops WHERE accountId = ? LIMIT 1;")) {
                statement.setInt(1, accountId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while(resultSet.next()) {
                        playerDrops = new PlayerDrops(accountId, resultSet.getLong(1));
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return playerDrops;
    }

    public void updatePlayerDrops(Stack<PlayerDrops> playerDrops) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE drops SET drops = ? WHERE accountId = ?;");
        ) {
            while (!playerDrops.isEmpty()) {
                PlayerDrops drops = playerDrops.pop();
                statement.setLong(1, drops.getDrops());
                statement.setInt(2, drops.getAccountId());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
