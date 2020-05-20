package com.ellisiumx.elrankup.economy.repository;

import com.ellisiumx.elcore.database.DBPool;
import com.ellisiumx.elcore.database.RepositoryBase;
import com.ellisiumx.elrankup.economy.PlayerMoney;
import net.minecraft.server.v1_8_R3.Tuple;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.Stack;

public class EconomyRepository extends RepositoryBase {

    public EconomyRepository(JavaPlugin plugin) {
        super(plugin, DBPool.getDataSource("rankup"));
    }

    @Override
    protected void initialize() {
    }

    @Override
    protected void update() {
    }

    public boolean hasAccountByUUID(String uuid) {
        boolean hasAccount = false;
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT id FROM economy WHERE uuid = ? LIMIT 1;")
        ) {
            statement.setString(1, uuid);
            try (ResultSet resultSet = statement.executeQuery()) {
                hasAccount = resultSet.next();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return hasAccount;
    }

    public boolean hasAccountByName(String playerName) {
        boolean hasAccount = false;
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT id FROM economy WHERE name LIKE ? LIMIT 1;")
        ) {
            statement.setString(1, playerName);
            try (ResultSet resultSet = statement.executeQuery()) {
                hasAccount = resultSet.next();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return hasAccount;
    }

    public Double getBalanceByName(String playerName) {
        Double balance = null;
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT balance FROM economy WHERE name LIKE ? LIMIT 1;")
        ) {
            statement.setString(1, playerName);
            try (ResultSet resultSet = statement.executeQuery()) {
                while(resultSet.next()) {
                    balance = resultSet.getDouble(1);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return balance;
    }

    public Double getBalanceByUUID(String uuid) {
        Double balance = null;
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT balance FROM economy WHERE uuid = ? LIMIT 1;")
        ) {
            statement.setString(1, uuid);
            try (ResultSet resultSet = statement.executeQuery()) {
                while(resultSet.next()) {
                    balance = resultSet.getDouble(1);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return balance;
    }

    public Double getCashByUUID(String uuid) {
        Double cash = null;
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT cash FROM economy WHERE uuid = ? LIMIT 1;")
        ) {
            statement.setString(1, uuid);
            try (ResultSet resultSet = statement.executeQuery()) {
                while(resultSet.next()) {
                    cash = resultSet.getDouble(1);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return cash;
    }

    public void depositAmountByName(String playerName, double amount) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE economy SET balance = balance + ? WHERE name LIKE ?;")
        ) {
            statement.setDouble(1, amount);
            statement.setString(2, playerName);
            statement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void depositAmountByUUID(String uuid, double amount) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE economy SET balance = balance + ? WHERE uuid = ?;")
        ) {
            statement.setDouble(1, amount);
            statement.setString(2, uuid);
            statement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void withdrawAmountByName(String playerName, double amount) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE economy SET balance = balance - ? WHERE name LIKE ?;")
        ) {
            statement.setDouble(1, amount);
            statement.setString(2, playerName);
            statement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void withdrawAmountByUUID(String uuid, double amount) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE economy SET balance = balance - ? WHERE uuid = ?;")
        ) {
            statement.setDouble(1, amount);
            statement.setString(2, uuid);
            statement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Tuple<Boolean, Boolean> payAmount(String fromUUID, String toPlayerName, double amount) {
        Tuple<Boolean, Boolean> response = null;
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try (Statement statement = connection.createStatement()) {
                boolean hasAccount = false;
                boolean hasBalance = false;

                try (ResultSet resultSet = statement.executeQuery("SELECT IF(EXISTS(SELECT id FROM economy WHERE name LIKE '" + toPlayerName + "'),1,0) AS result;")) {
                    while(resultSet.next()) {
                        hasAccount = resultSet.getBoolean(1);
                    }
                }

                try (ResultSet resultSet = statement.executeQuery("SELECT IF(EXISTS(SELECT id FROM economy WHERE uuid = '" + fromUUID + "' AND balance >= " + amount + "),1,0) AS result;")) {
                    while(resultSet.next()) {
                        hasBalance = resultSet.getBoolean(1);
                    }
                }

                if(hasAccount && hasBalance) {
                    statement.executeUpdate("UPDATE economy SET balance = balance - " + amount + " WHERE uuid = '" + fromUUID + "';");
                    statement.executeUpdate("UPDATE economy SET balance = balance + " + amount + " WHERE name LIKE '" + toPlayerName + "';");
                }

                connection.commit();
                response = new Tuple<>(hasAccount, hasBalance);
            } catch (Exception ex) {
                connection.rollback();
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return response;
    }

    public void createEconomyAccount(String uuid, String name) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT IGNORE INTO economy (uuid, name, balance) VALUES (?, ?, ?);")
        ) {
            statement.setString(1, uuid);
            statement.setString(2, name);
            statement.setDouble(3, 0.0D);
            statement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateAccounts(Stack<PlayerMoney> playerMonies) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE economy SET balance = ?, cash = ? WHERE uuid = ?;");
        ){
            while (!playerMonies.empty()) {
                PlayerMoney data = playerMonies.pop();
                statement.setDouble(1, data.money);
                statement.setDouble(2, data.cash);
                statement.setString(3, data.player.getUniqueId().toString());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
