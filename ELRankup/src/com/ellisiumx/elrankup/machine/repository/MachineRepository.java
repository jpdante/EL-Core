package com.ellisiumx.elrankup.machine.repository;

import com.ellisiumx.elcore.database.DBPool;
import com.ellisiumx.elcore.database.RepositoryBase;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.machine.Machine;
import com.ellisiumx.elrankup.machine.MachineFriend;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.Stack;

public class MachineRepository extends RepositoryBase {

    public MachineRepository(JavaPlugin plugin) {
        super(plugin, DBPool.getDataSource("rankup"));
    }

    @Override
    protected void initialize() {
    }

    @Override
    protected void update() {
    }

    public ArrayList<Machine> getMachines() {
        ArrayList<Machine> machines = new ArrayList<>();
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT id, type, account_id, level, drops, fuel, last_menu_open, last_refuel FROM machines;");
                ResultSet resultSet = statement.executeQuery()
        ) {
            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String type = resultSet.getString(2);
                int owner = resultSet.getInt(3);
                int level = resultSet.getInt(4);
                int drops = resultSet.getInt(5);
                int fuel = resultSet.getInt(6);
                Timestamp lastMenuOpen = resultSet.getTimestamp(7);
                Timestamp lastRefuel = resultSet.getTimestamp(8);
                Machine machine = new Machine(id, RankupConfiguration.getMachineTypeByName(type), owner, level, drops, fuel, lastMenuOpen, lastRefuel);
                machines.add(machine);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return machines;
    }

    public ArrayList<MachineFriend> getMachineFriends(int accountId) {
        ArrayList<MachineFriend> machineFriends = new ArrayList<>();
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT machine_friends.friend_id, elcore.accounts.name FROM machine_friends INNER JOIN elcore.accounts ON elcore.accounts.id = account_id WHERE account_id = ?;")
        ) {
            statement.setInt(1, accountId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int id = resultSet.getInt(1);
                    String name = resultSet.getString(2);
                    machineFriends.add(new MachineFriend(id, name));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return machineFriends;
    }

    public void addMachineFriend(int id, int friendId) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT IGNORE INTO machine_friends (account_id, friend_id) VALUES (?, ?);");
        ) {
            statement.setInt(1, id);
            statement.setInt(2, friendId);
            statement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void removeMachineFriend(int id, int friendId) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("DELETE FROM machine_friends WHERE account_id = ? AND friend_id = ?;");
        ) {
            statement.setInt(1, id);
            statement.setInt(2, friendId);
            statement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Machine getMachine(int id) {
        Machine machine = null;
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT type, account_id, level, drops, fuel, location, last_menu_open, last_refuel FROM machines WHERE id = ? LIMIT 1;");
        ) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String type = resultSet.getString(1);
                    int owner = resultSet.getInt(2);
                    int level = resultSet.getInt(3);
                    int drops = resultSet.getInt(4);
                    int fuel = resultSet.getInt(5);
                    Timestamp lastMenuOpen = resultSet.getTimestamp(6);
                    Timestamp lastRefuel = resultSet.getTimestamp(7);
                    machine = new Machine(id, RankupConfiguration.getMachineTypeByName(type), owner, level, drops, fuel, lastMenuOpen, lastRefuel);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return machine;
    }

    public void createMachine(Machine machine) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT IGNORE INTO machines (type, account_id, level, drops, fuel, last_menu_open, last_refuel) VALUES (?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
        ) {
            statement.setString(1, machine.getType().getKey());
            statement.setInt(2, machine.getOwner());
            statement.setInt(3, machine.getLevel());
            statement.setInt(4, machine.getDrops());
            statement.setInt(5, machine.getFuel());
            statement.setTimestamp(6, machine.getLastMenuOpen());
            statement.setTimestamp(7, machine.getLastRefuel());
            statement.executeUpdate();
            try (ResultSet resultSet = statement.getGeneratedKeys()) {
                while (resultSet.next()) {
                    machine.setId(resultSet.getInt(1));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void deleteMachine(Machine machine) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("DELETE FROM machines WHERE id = ?");
        ) {
            statement.setInt(1, machine.getId());
            statement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateMachine(Machine machine) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE machines SET level = ?, drops = ?, fuel = ?, last_menu_open = ?, last_refuel = ? WHERE id = ?");
        ) {
            statement.setInt(1, machine.getLevel());
            statement.setInt(2, machine.getDrops());
            statement.setInt(3, machine.getFuel());
            statement.setTimestamp(4, machine.getLastMenuOpen());
            statement.setTimestamp(5, machine.getLastRefuel());
            statement.setInt(6, machine.getId());
            statement.executeUpdate();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void updateMachines(Stack<Machine> machines) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("UPDATE machines SET level = ?, drops = ?, fuel = ?, last_menu_open = ?, last_refuel = ? WHERE id = ?");
        ) {
            while (!machines.empty()) {
                Machine machine = machines.pop();
                statement.setInt(1, machine.getLevel());
                statement.setInt(2, machine.getDrops());
                statement.setInt(3, machine.getFuel());
                statement.setTimestamp(4, machine.getLastMenuOpen());
                statement.setTimestamp(5, machine.getLastRefuel());
                statement.setInt(6, machine.getId());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*public void createMachines(Stack<Machine> machines) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT IGNORE INTO machines (type, owner, level, drops, fuel, location, last_menu_open, lastRefuel) VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
        ){
            while (!machines.empty()) {
                Machine machine = machines.pop();
                statement.setInt(1, machine.getType().getId());
                statement.setInt(2, machine.getOwner());
                statement.setInt(3, machine.getLevel());
                statement.setInt(4, machine.getDrops());
                statement.setInt(5, machine.getFuel());
                statement.setString(6, UtilConvert.getStringFromLocation(machine.getLocation()));
                statement.setTimestamp(7, machine.getLastMenuOpen());
                statement.setTimestamp(8, machine.getLastRefuel());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }*/

}
