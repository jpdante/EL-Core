package com.ellisiumx.elrankup.machine.repository;

import com.ellisiumx.elcore.database.DBPool;
import com.ellisiumx.elcore.database.RepositoryBase;
import com.ellisiumx.elcore.utils.UtilConvert;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.machine.Machine;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

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
                PreparedStatement statement = connection.prepareStatement("SELECT id, type, owner, level, drops, fuel, location, lastMenuOpen, lastRefuel FROM machines;");
                ResultSet resultSet = statement.executeQuery()
        ){
            while(resultSet.next()) {
                int id = resultSet.getInt(1);
                String type = resultSet.getString(2);
                int owner = resultSet.getInt(3);
                int level = resultSet.getInt(4);
                int drops = resultSet.getInt(5);
                int fuel = resultSet.getInt(6);
                Location location = UtilConvert.getLocationFromString(resultSet.getString(7));
                Timestamp lastMenuOpen = resultSet.getTimestamp(8);
                Timestamp lastRefuel = resultSet.getTimestamp(9);
                Machine machine = new Machine(id, RankupConfiguration.getMachineTypeByName(type), owner, level, drops, fuel, location, lastMenuOpen, lastRefuel);
                machines.add(machine);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return machines;
    }

    public Machine getMachine(int id) {
        Machine machine = null;
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("SELECT type, owner, level, drops, fuel, location, lastMenuOpen, lastRefuel FROM machines WHERE id = ? LIMIT 1;");
        ){
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                while(resultSet.next()) {
                    String type = resultSet.getString(1);
                    int owner = resultSet.getInt(2);
                    int level = resultSet.getInt(3);
                    int drops = resultSet.getInt(4);
                    int fuel = resultSet.getInt(5);
                    String locationRaw = resultSet.getString(6);
                    Timestamp lastMenuOpen = resultSet.getTimestamp(7);
                    Timestamp lastRefuel = resultSet.getTimestamp(8);
                    machine = new Machine(id, RankupConfiguration.getMachineTypeByName(type), owner, level, drops, fuel, UtilConvert.getLocationFromString(locationRaw), lastMenuOpen, lastRefuel);
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
                PreparedStatement statement = connection.prepareStatement("INSERT IGNORE INTO machines (type, owner, level, drops, fuel, location, lastMenuOpen, lastRefuel) VALUES (?, ?, ?, ?, ?, ?, ?, ?);", Statement.RETURN_GENERATED_KEYS);
        ){
            statement.setString(1, machine.getType().getKey());
            statement.setInt(2, machine.getOwner());
            statement.setInt(3, machine.getLevel());
            statement.setInt(4, machine.getDrops());
            statement.setInt(5, machine.getFuel());
            statement.setString(6, UtilConvert.getStringFromLocation(machine.getLocation()));
            statement.setTimestamp(7, machine.getLastMenuOpen());
            statement.setTimestamp(8, machine.getLastRefuel());
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

    public void deleteMachine(int id) {

    }

    /*public void createMachines(Stack<Machine> machines) {
        try (
                Connection connection = getConnection();
                PreparedStatement statement = connection.prepareStatement("INSERT IGNORE INTO machines (type, owner, level, drops, fuel, location, lastMenuOpen, lastRefuel) VALUES (?, ?, ?, ?, ?, ?, ?, ?);");
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
