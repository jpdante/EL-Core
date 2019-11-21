package com.ellisiumx.elcore.account.repository;

import com.ellisiumx.elcore.account.CoreClient;
import com.ellisiumx.elcore.database.DBPool;
import com.ellisiumx.elcore.database.RepositoryBase;
import com.ellisiumx.elcore.permissions.Rank;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;

public class AccountRepository extends RepositoryBase {

    public AccountRepository(JavaPlugin plugin) {
        super(plugin, DBPool.Default);
    }

    @Override
    protected void initialize() { }

    @Override
    protected void update() { }

    public CoreClient executeLogin(String uuid, String playerName) {
        CoreClient client;
        try {
            client = new CoreClient(playerName);
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT id, rank FROM accounts WHERE accounts.uuid = ? LIMIT 1;");
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            if(resultSet.next()) {
                client.setAccountId(resultSet.getInt(1));
                client.setRank(Rank.valueOf(resultSet.getString(2)));
            }
            resultSet.close();
            if(client.getAccountId() == -1) {
                statement = connection.prepareStatement("INSERT INTO accounts (uuid, name, creationDate) values(?, ?, now());", Statement.RETURN_GENERATED_KEYS);
                statement.setString(1, uuid);
                statement.setString(2, playerName);
                statement.executeUpdate();
                resultSet = statement.getGeneratedKeys();
                if(resultSet.next()) {
                    client.setAccountId(resultSet.getInt(1));
                }
                client.setRank(Rank.ALL);
            }
            statement = connection.prepareStatement("UPDATE accounts SET name = ?, lastLogin = now() WHERE id = ?;");
            statement.setString(1, playerName);
            statement.setInt(2, client.getAccountId());
            statement.executeUpdate();
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
        return client;
    }
}