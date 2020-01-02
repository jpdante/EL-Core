package com.ellisiumx.elrankup.clan.repository;

import com.ellisiumx.elcore.database.DBPool;
import com.ellisiumx.elcore.database.RepositoryBase;
import com.ellisiumx.elrankup.clan.Clan;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;

public class ClanRepository extends RepositoryBase {
    public ClanRepository(JavaPlugin plugin) {
        super(plugin, DBPool.getDataSource("rankup"));
    }

    @Override
    protected void initialize() {

    }

    @Override
    protected void update() {

    }

    public ArrayList<Clan> getClans() {
        ArrayList<Clan> clans = new ArrayList<>();
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT id, verified, tag, colorTag, name, friendlyFire, kills, deaths FROM clans;");
                ResultSet resultSet = statement.executeQuery()
        ){
            while(resultSet.next()) {
                int id = resultSet.getInt(1);
                boolean verified = resultSet.getBoolean(2);
                String tag = resultSet.getString(3);
                String colorTag = resultSet.getString(4);
                String name = resultSet.getString(5);
                boolean friendlyFire = resultSet.getBoolean(6);
                int kills = resultSet.getInt(7);
                int deaths = resultSet.getInt(8);
                Clan clan = new Clan(id, verified, tag, colorTag, name, friendlyFire, kills, deaths);
                clans.add(clan);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return clans;
    }
}
