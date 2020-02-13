package com.ellisiumx.elcore.preferences;

import com.ellisiumx.elcore.database.DBPool;
import com.ellisiumx.elcore.database.RepositoryBase;
import com.ellisiumx.elcore.utils.UtilGson;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class PreferencesRepository extends RepositoryBase {

    public PreferencesRepository(JavaPlugin plugin) {
        super(plugin, DBPool.Core);
    }

    @Override
    protected void initialize() {
    }

    @Override
    protected void update() {
    }

    public void saveUserPreferences(Stack<UserPreferences> buffer) {
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO accountPreferences (accountId, preferences) VALUES (?, ?) ON DUPLICATE KEY UPDATE preferences = ?;");
        ){
            while (!buffer.empty()) {
                UserPreferences preferences = buffer.pop();
                preparedStatement.setInt(1, preferences.getAccountId());
                String json = UtilGson.serialize(preferences);
                preparedStatement.setString(2, json);
                preparedStatement.setString(3, json);
                preparedStatement.addBatch();
            }
            preparedStatement.executeBatch();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public UserPreferences loadClientInformation(String uuid) {
        UserPreferences userPreferences = null;
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT preferences FROM accountPreferences WHERE uuid = ? LIMIT 1;");
        ){
            preparedStatement.setString(1, uuid);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    userPreferences = UtilGson.deserialize(resultSet.getString(1), UserPreferences.class);
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        if(userPreferences == null) userPreferences = new UserPreferences();
        return userPreferences;
    }
}
