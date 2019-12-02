package com.ellisiumx.elrankup.economy.repository;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.database.DBPool;
import com.ellisiumx.elcore.database.RepositoryBase;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.preferences.UserPreferences;
import net.minecraft.server.v1_8_R3.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import javax.xml.transform.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

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
        try {
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT id FROM economy WHERE uuid = ? LIMIT 1;");
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            hasAccount = resultSet.next();
            connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return hasAccount;
    }

    public boolean hasAccountByName(String playerName) {
        boolean hasAccount = false;
        try {
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT id FROM economy WHERE name LIKE ? LIMIT 1;");
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            hasAccount = resultSet.next();
            connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return hasAccount;
    }

    public Double getBalanceByName(String playerName) {
        Double balance = null;
        try {
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT balance FROM economy WHERE name LIKE ? LIMIT 1;");
            statement.setString(1, playerName);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                balance = resultSet.getDouble(1);
            }
            resultSet.close();
            connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return balance;
    }

    public Double getBalanceByUUID(String uuid) {
        Double balance = null;
        try {
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("SELECT balance FROM economy WHERE uuid = ? LIMIT 1;");
            statement.setString(1, uuid);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                balance = resultSet.getDouble(1);
            }
            resultSet.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return balance;
    }

    public void depositAmountByName(String playerName, double amount) {
        try {
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE economy SET balance = balance + ? WHERE name LIKE ?;");
            statement.setDouble(1, amount);
            statement.setString(2, playerName);
            statement.executeUpdate();
            connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void depositAmountByUUID(String uuid, double amount) {
        try {
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE economy SET balance = balance + ? WHERE uuid = ?;");
            statement.setDouble(1, amount);
            statement.setString(2, uuid);
            statement.executeUpdate();
            connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void withdrawAmountByName(String playerName, double amount) {
        try {
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE economy SET balance = balance - ? WHERE name LIKE ?;");
            statement.setDouble(1, amount);
            statement.setString(2, playerName);
            statement.executeUpdate();
            connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void withdrawAmountByUUID(String uuid, double amount) {
        try {
            Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement("UPDATE economy SET balance = balance - ? WHERE uuid = ?;");
            statement.setDouble(1, amount);
            statement.setString(2, uuid);
            statement.executeUpdate();
            connection.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Tuple<Boolean, Boolean> payAmount(String fromUUID, String toPlayerName, double amount) {
        Connection connection = getConnection();
        try {
            connection.setAutoCommit(false);
            PreparedStatement statement = connection.prepareStatement(
                    "SET @hasAccount = EXISTS(SELECT id FROM economy WHERE name LIKE ?);" +
                    "SET @hasBalance = EXISTS(SELECT id FROM economy WHERE uuid = ? AND balance >= ?);" +
                    "UPDATE economy SET balance = balance - ? WHERE @hasAccount = 1 AND @hasBalance = 1 AND uuid = ?;" +
                    "UPDATE economy SET balance = balance + ? WHERE @hasAccount = 1 AND @hasBalance = 1 AND name LIKE ?;" +
                    "SELECT @hasAccount, @hasBalance;"
            );
            statement.setString(1, toPlayerName);
            statement.setString(2, fromUUID);
            statement.setDouble(3, amount);
            statement.setDouble(4, amount);
            statement.setString(5, fromUUID);
            statement.setDouble(6, amount);
            statement.setString(7, toPlayerName);
            ResultSet resultSet = statement.executeQuery();
            connection.commit();
            boolean hasAccount = false;
            boolean hasBalance = false;
            if (resultSet.next()) {
                hasAccount = resultSet.getBoolean(1);
                hasBalance = resultSet.getBoolean(2);
            }
            connection.close();
            return new Tuple<>(hasAccount, hasBalance);
        } catch (Exception ex) {
            try {
                connection.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ex.printStackTrace();
        }
        return null;
    }

    public Connection getInternalConnection() {
        return getConnection();
    }
}
