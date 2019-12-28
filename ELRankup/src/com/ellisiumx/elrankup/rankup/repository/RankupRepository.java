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
                PreparedStatement statement = connection.prepareStatement("UPDATE ranks SET rank = ? WHERE account-id = ?;")
        ) {
            statement.setString(1, rank.toUpperCase());
            statement.setInt(2, accountId);
            statement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ArrayList<Pair<Pair<String, Integer>, String>> getRanks(Stack<Pair<String, Integer>> buffer, String defaultRank) {
        ArrayList<Pair<Pair<String, Integer>, String>> ranks = new ArrayList<>();
        try (Connection connection = getConnection();) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT IGNORE INTO ranks (account-id, rank) VALUES (?, ?);")) {
                while(!buffer.isEmpty()) {
                    Pair<String, Integer> account =  buffer.pop();
                    ranks.add(new Pair<>(account, null));
                    statement.setInt(1, account.getRight());
                    statement.setString(2, defaultRank.toUpperCase());
                    statement.addBatch();
                }
                statement.executeUpdate();
            }
            for(int i = 0; i < ranks.size(); i++) {
                try (PreparedStatement statement = connection.prepareStatement("SELECT rank FROM ranks WHERE account-id = ? LIMIT 1;")) {
                    statement.setInt(1, ranks.get(i).getLeft().getRight());
                    try (ResultSet resultSet = statement.executeQuery()) {
                        while(resultSet.next()) {
                            ranks.get(i).setRight(resultSet.getString(1));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ranks;
    }
}
