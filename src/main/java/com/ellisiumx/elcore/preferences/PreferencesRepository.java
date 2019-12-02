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

    public void saveUserPreferences(HashMap<String, UserPreferences> preferences) {
        try (
                Connection connection = getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO accountPreferences (uuid, preferences) VALUES (?, ?) ON DUPLICATE KEY UPDATE preferences = ?;");
        ){
            for (Map.Entry<String, UserPreferences> entry : preferences.entrySet()) {
                preparedStatement.setString(1, entry.getKey());
                String json = UtilGson.serialize(entry.getValue());
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
