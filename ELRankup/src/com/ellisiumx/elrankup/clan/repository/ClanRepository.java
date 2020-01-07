package com.ellisiumx.elrankup.clan.repository;

import com.ellisiumx.elcore.database.DBPool;
import com.ellisiumx.elcore.database.RepositoryBase;
import com.ellisiumx.elrankup.clan.Clan;
import com.ellisiumx.elrankup.clan.ClanPlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Stack;

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
                PreparedStatement statement = connection.prepareStatement("SELECT id, verified, leader, tag, colorTag, name, friendlyFire, kills, deaths FROM clans;");
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                boolean verified = resultSet.getBoolean(2);
                int leader = resultSet.getInt(3);
                String tag = resultSet.getString(4);
                String colorTag = resultSet.getString(5);
                String name = resultSet.getString(6);
                boolean friendlyFire = resultSet.getBoolean(7);
                int kills = resultSet.getInt(8);
                int deaths = resultSet.getInt(9);
                Clan clan = new Clan(id, verified, leader, tag, colorTag, name, friendlyFire, kills, deaths);
                clans.add(clan);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return clans;
    }

    public ArrayList<String> getClanMembers(int clanId) {
        ArrayList<String> members = new ArrayList<>();
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT name FROM clanPlayers WHERE clan = ?;");
        ) {
            statement.setInt(1, clanId);
            try(ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    members.add(resultSet.getString(1));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return members;
    }

    public ClanPlayer getClanPlayer(int accountId, String name, ArrayList<Clan> clans) {
        Date date = new Date();
        ClanPlayer player = null;
        try (Connection connection = getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT IGNORE INTO clanPlayers (accountId, name, clan, friendlyFire, neutralKills, rivalKills, civilianKills, deaths, lastSeen, joinDate) VALUES (?, ?, NULL, ?, ?, ?, ?, ?, ?, ?);")) {
                statement.setInt(1, accountId);
                statement.setString(2, name);
                statement.setBoolean(3, false);
                statement.setInt(4, 0);
                statement.setInt(5, 0);
                statement.setInt(6, 0);
                statement.setInt(7, 0);
                statement.setTimestamp(8, new Timestamp(date.getTime()));
                statement.setTimestamp(9, new Timestamp(date.getTime()));
                statement.executeUpdate();
            }
            try (PreparedStatement statement = connection.prepareStatement("SELECT id, clan, friendlyFire, neutralKills, rivalKills, civilianKills, deaths, lastSeen, joinDate FROM clanPlayers WHERE accountId = ? LIMIT 1;")) {
                statement.setInt(1, accountId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    while(resultSet.next()) {
                        int id = resultSet.getInt(1);
                        int clanId = resultSet.getInt(2);
                        boolean friendlyFire = resultSet.getBoolean(3);
                        int neutralKills = resultSet.getInt(4);
                        int rivalKills = resultSet.getInt(5);
                        int civilianKills = resultSet.getInt(6);
                        int deaths = resultSet.getInt(7);
                        Timestamp lastSeen = resultSet.getTimestamp(8);
                        Timestamp joinDate = resultSet.getTimestamp(9);
                        Clan clan = null;
                        for (Clan lclan : clans) {
                            if (clanId == lclan.id) {
                                clan = lclan;
                                break;
                            }
                        }
                        player = new ClanPlayer(id, accountId, clan, friendlyFire, neutralKills, rivalKills, civilianKills, deaths, lastSeen, joinDate);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return player;
    }

    public void updateClanPlayers(Stack<ClanPlayer> clanPlayers) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE clanPlayers SET clan = ?, friendlyFire = ?, neutralKills = ?, rivalKills = ?, civilianKills = ?, deaths = ?, lastSeen = ?, joinDate = ? WHERE id = ?;");
        ) {
            while (!clanPlayers.isEmpty()) {
                ClanPlayer clanPlayer = clanPlayers.pop();
                if(clanPlayer.clan != null) statement.setInt(1, clanPlayer.clan.id);
                else statement.setNull(1, Types.VARCHAR);
                statement.setBoolean(2, clanPlayer.friendlyFire);
                statement.setInt(3, clanPlayer.neutralKills);
                statement.setInt(4, clanPlayer.rivalKills);
                statement.setInt(5, clanPlayer.civilianKills);
                statement.setInt(6, clanPlayer.deaths);
                statement.setTimestamp(7, clanPlayer.lastSeen);
                statement.setTimestamp(8, clanPlayer.joinDate);
                statement.setInt(9, clanPlayer.id);
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setClanNullForMembers(ArrayList<String> members) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE clanPlayers SET clan = NULL WHERE name = ?;");
        ) {
            for(String member : members) {
                statement.setString(1, member);
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void createClan(Clan clan) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT IGNORE INTO clans (verified, leader, tag, colorTag, name, friendlyFire, kills, deaths) VALUES (?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
        ) {
            statement.setBoolean(1, clan.verified);
            statement.setInt(2, clan.leader);
            statement.setString(3, clan.tag);
            statement.setString(4, clan.colorTag);
            statement.setString(5, clan.name);
            statement.setBoolean(6, clan.friendFire);
            statement.setInt(7, clan.kills);
            statement.setInt(8, clan.deaths);
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                while (resultSet.next()) {
                    clan.id = resultSet.getInt(1);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*public void updateClan(Clan clan) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE clans SET verified = ?, leader = ?, tag = ?, colorTag = ?, name = ?, friendlyFire = ? , kills = ?, deaths = ? WHERE id = ?");
        ) {
            statement.setBoolean(1, clan.verified);
            statement.setInt(2, clan.leader);
            statement.setString(3, clan.tag);
            statement.setString(4, clan.colorTag);
            statement.setString(5, clan.name);
            statement.setBoolean(6, clan.friendFire);
            statement.setInt(7, clan.kills);
            statement.setInt(8, clan.deaths);
            statement.setInt(9, clan.id);
            statement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }*/

    public void updateClans(Stack<Clan> clans) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE clans SET verified = ?, leader = ?, tag = ?, colorTag = ?, name = ?, friendlyFire = ? , kills = ?, deaths = ? WHERE id = ?");
        ) {
            while (!clans.isEmpty()) {
                Clan clan = clans.pop();
                statement.setBoolean(1, clan.verified);
                statement.setInt(2, clan.leader);
                statement.setString(3, clan.tag);
                statement.setString(4, clan.colorTag);
                statement.setString(5, clan.name);
                statement.setBoolean(6, clan.friendFire);
                statement.setInt(7, clan.kills);
                statement.setInt(8, clan.deaths);
                statement.setInt(9, clan.id);
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteClan(Clan clan) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("DELETE FROM clans WHERE id = ?");
        ) {
            statement.setInt(1, clan.id);
            statement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
