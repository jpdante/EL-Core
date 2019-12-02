package com.ellisiumx.elcore.punish.repository;

import com.ellisiumx.elcore.database.DBPool;
import com.ellisiumx.elcore.database.RepositoryBase;
import com.ellisiumx.elcore.utils.Pair;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;

public class PunishRepository extends RepositoryBase {

    public PunishRepository(JavaPlugin plugin) {
        super(plugin, DBPool.Core);
    }

    @Override
    protected void initialize() {
    }

    @Override
    protected void update() {
    }

    public Pair<String, Timestamp> isBanned(String uuid, String name) {
        Connection connection = getConnection();
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("SELECT reason, expirationDate FROM bans WHERE active = 1 AND (uuid = ? OR name = ?) LIMIT 1;");
            statement.setString(1, uuid);
            statement.setString(2, name);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                return new Pair<>(resultSet.getString(0), resultSet.getTimestamp(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
