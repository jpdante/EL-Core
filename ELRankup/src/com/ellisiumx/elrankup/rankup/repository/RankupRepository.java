package com.ellisiumx.elrankup.rankup.repository;

import com.ellisiumx.elcore.database.DBPool;
import com.ellisiumx.elcore.database.RepositoryBase;
import com.ellisiumx.elcore.utils.Pair;
import com.ellisiumx.elcore.utils.UtilLog;
import net.minecraft.server.v1_8_R3.Tuple;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class RankupRepository extends RepositoryBase {

    public RankupRepository(JavaPlugin plugin) {
        super(plugin, DBPool.getDataSource("rankup"));
    }

    @Override
    protected void initialize() {
    }

    @Override
    protected void update() {
    }

    public void updateRank(int accountId, String rank) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE ranks SET rank = ? WHERE accountId = ?;")
        ) {
            statement.setString(1, rank.toUpperCase());
            statement.setInt(2, accountId);
            statement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String getRank(int accountId, String defaultRank) {
        String rank = null;
        try (Connection connection = getConnection();) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT IGNORE INTO ranks (accountId, rank) VALUES (?, ?);")) {
                statement.setInt(1, accountId);
                statement.setString(2, defaultRank.toUpperCase());
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement("SELECT rank FROM ranks WHERE accountId = ? LIMIT 1;")) {
                statement.setInt(1, accountId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while(resultSet.next()) {
                        rank = resultSet.getString(1);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return rank;
    }
}
