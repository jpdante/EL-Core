package com.ellisiumx.elrankup.drop.repository;

import com.ellisiumx.elcore.database.DBPool;
import com.ellisiumx.elcore.database.RepositoryBase;
import com.ellisiumx.elcore.utils.UtilGson;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
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

    public PlayerKit getPlayerKit(int accountId, Player player) {
        PlayerKit playerKit = null;
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT IGNORE INTO kits (accountId, kitstamps) VALUES (?, ?);")) {
                statement.setInt(1, accountId);
                statement.setString(2, UtilGson.serialize(new KitStamp()));
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement("SELECT kitstamps FROM kits WHERE accountId = ? LIMIT 1;")) {
                statement.setInt(1, accountId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while(resultSet.next()) {
                        playerKit = new PlayerKit(player);
                        KitStamp kitStamps = UtilGson.deserialize(resultSet.getString(1), KitStamp.class);
                        for(Map.Entry<String, Timestamp> stamp : kitStamps.kitDelay.entrySet()) {
                            if(RankupConfiguration.Kits.containsKey(stamp.getKey())) {
                                playerKit.getKitDelay().put(RankupConfiguration.Kits.get(stamp.getKey()), stamp.getValue());
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return playerKit;
    }

    public void updatePlayerKit(Stack<PlayerKit> playerKits) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE kits SET kitstamps = ? WHERE accountId = ?;");
        ) {
            while (!playerKits.isEmpty()) {
                PlayerKit playerKit = playerKits.pop();
                statement.setString(1, UtilGson.serialize(new KitStamp(playerKit)));
                statement.setInt(2, playerKit.getAccountId());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
