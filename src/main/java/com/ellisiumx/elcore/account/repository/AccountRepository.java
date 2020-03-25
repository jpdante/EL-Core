package com.ellisiumx.elcore.account.repository;

import com.ellisiumx.elcore.account.CoreClient;
import com.ellisiumx.elcore.account.ILoginProcessor;
import com.ellisiumx.elcore.database.DBPool;
import com.ellisiumx.elcore.database.RepositoryBase;
import com.ellisiumx.elcore.permissions.Rank;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AccountRepository extends RepositoryBase {

    public AccountRepository(JavaPlugin plugin) {
        super(plugin, DBPool.Core);
    }

    @Override
    protected void initialize() {
    }

    @Override
    protected void update() {
    }

    public void updateName(String uuid, String playerName) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE accounts SET name = ? WHERE uuid = ?;")
        ){
            statement.setString(1, playerName);
            statement.setString(2, uuid);
            statement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean login(final CoreClient client, HashMap<String, ILoginProcessor> loginProcessors, String uuid) {
        try (Connection connection = getConnection()){
            try (PreparedStatement statement = connection.prepareStatement("SELECT id, rank FROM accounts WHERE accounts.uuid = ? LIMIT 1;")) {
                statement.setString(1, uuid);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        client.setAccountId(resultSet.getInt(1));
                        client.setRank(Rank.valueOf(resultSet.getString(2)));
                    }
                }
            }

            if (client.getAccountId() == -1) {
                try (PreparedStatement statement = connection.prepareStatement("INSERT INTO accounts (uuid, name, creation_date) values(?, ?, now());", Statement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, uuid);
                    statement.setString(2, client.getPlayerName());
                    statement.executeUpdate();
                    try (ResultSet resultSet = statement.getGeneratedKeys()) {
                        while (resultSet.next()) {
                            client.setAccountId(resultSet.getInt(1));
                        }
                        client.setRank(Rank.ALL);
                    }
                }
            }

            StringBuilder sql = new StringBuilder("UPDATE accounts SET name = '" + client.getPlayerName() + "', last_login = now() WHERE id = '" + client.getAccountId() + "';");
            for (ILoginProcessor loginProcessor : loginProcessors.values()) {
                sql.append(loginProcessor.getQuery(client.getAccountId(), uuid, client.getPlayerName()));
            }
            try (PreparedStatement statement = connection.prepareStatement(sql.toString())) {
                statement.execute();
                statement.getUpdateCount();
                statement.getMoreResults();
                for (ILoginProcessor loginProcessor : loginProcessors.values()) {
                    loginProcessor.processLoginResultSet(client.getPlayerName(), client.getAccountId(), statement.getResultSet());
                    statement.getMoreResults();
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    /*public CoreClient executeLogin(String uuid, String playerName) {
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
    }*/
}