package com.ellisiumx.elrankup.configuration;

import com.ellisiumx.elcore.utils.Pair;
import com.ellisiumx.elrankup.ELRankup;
import com.ellisiumx.elrankup.machine.MachineType;
import com.ellisiumx.elrankup.mapedit.BlockData;
import com.ellisiumx.elrankup.mine.MineData;
import com.ellisiumx.elcore.utils.UtilConvert;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class RankupConfiguration {

    public static boolean MinesEnabled;
    public static List<Integer> AlertTimes;
    public static List<MineData> Mines;

    public static ItemStack Fuel;
    public static List<MachineType> MachineTypes;

    public RankupConfiguration() {
        FileConfiguration config = ELRankup.getContext().getConfig();

        MinesEnabled = config.getBoolean("mine-reseter.enabled");
        AlertTimes = config.getIntegerList("mine-reseter.alert-times");
        Mines = new ArrayList<>();
        for (String key : config.getConfigurationSection("mine-reseter.mines").getKeys(false)) {
            String name = config.getString("mine-reseter.mines." + key + ".name");
            boolean enabled = config.getBoolean("mine-reseter.mines." + key + ".enabled");
            int alertArea = config.getInt("mine-reseter.mines." + key + ".alert-area");
            Location point1 = stringToLocation(config.getString("mine-reseter.mines." + key + ".point1"));
            Location point2 = stringToLocation(config.getString("mine-reseter.mines." + key + ".point2"));
            int delay = config.getInt("mine-reseter.mines." + key + ".delay");
            MineData mineData = new MineData(name, enabled, alertArea, point1, point2, delay);
            for (String ore : config.getStringList("mine-reseter.mines." + key + ".ores")) {
                String[] datas = ore.split(",", 2);
                String[] item = datas[1].split(":", 2);
                mineData.getBlocks().put(new BlockData(Integer.parseInt(item[0]), Byte.parseByte(item[1])), Double.parseDouble(datas[0]));
            }
            Mines.add(mineData);
        }

        Fuel = UtilConvert.getItemStackFromConfig(config.getConfigurationSection("machines.fuel"));
        MachineTypes = new ArrayList<>();
        for (String key : config.getConfigurationSection("machines.types").getKeys(false)) {
            try {
                String name = config.getString("machines.types." + key + ".name").replace('&', ChatColor.COLOR_CHAR);
                double price = config.getDouble("machines.types." + key + ".price");
                ItemStack drop = UtilConvert.getItemStackFromConfig(config.getConfigurationSection("machines.types." + key + ".drop"));
                double dropPrice = config.getDouble("machines.types." + key + ".drop.price");
                ArrayList<Pair<Integer, Integer>> levels = new ArrayList<>();
                for(String key2 : config.getConfigurationSection("machines.types." + key + ".levels").getKeys(false)) {
                    int delay = config.getInt("machines.types." + key + ".levels." + key2 + ".delay");
                    int quantity = config.getInt("machines.types." + key + ".levels." + key2 + ".quantity");
                    levels.add(new Pair<>(delay, quantity));
                }
                MachineTypes.add(new MachineType(key, name, price, drop, dropPrice, levels));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void save() {
        ELRankup.getContext().getConfig().set("mine-reseter.mines", null);
        int index = 0;
        for (MineData mine : Mines) {
            ELRankup.getContext().getConfig().set("mine-reseter.mines." + index + ".name", mine.name);
            ELRankup.getContext().getConfig().set("mine-reseter.mines." + index + ".enabled", mine.enabled);
            ELRankup.getContext().getConfig().set("mine-reseter.mines." + index + ".alert-area", mine.alertArea);
            ELRankup.getContext().getConfig().set("mine-reseter.mines." + index + ".point1", locationToString(mine.getPoint1()));
            ELRankup.getContext().getConfig().set("mine-reseter.mines." + index + ".point2", locationToString(mine.getPoint2()));
            ELRankup.getContext().getConfig().set("mine-reseter.mines." + index + ".delay", mine.delay);
            List<String> ores = new ArrayList<>();
            for (BlockData blockData : mine.getBlocks().keySet()) {
                ores.add(mine.getBlocks().get(blockData) + "," + blockData.id + ":" + blockData.data);
            }
            ELRankup.getContext().getConfig().set("mine-reseter.mines." + index + ".ores", ores);
            index++;
        }
        ELRankup.getContext().saveConfig();
    }

    public static String locationToString(Location location) {
        return location.getWorld().getName() +
                ", " + location.getX() +
                ", " + location.getY() +
                ", " + location.getZ() +
                ", " + location.getPitch() +
                ", " + location.getYaw();
    }

    public static Location stringToLocation(String data) {
        String[] datas = data.replaceAll(" ", "").split(",");
        World world = Bukkit.getWorld(datas[0]);
        double x = Double.parseDouble(datas[1]);
        double y = Double.parseDouble(datas[2]);
        double z = Double.parseDouble(datas[3]);
        float pitch = Float.parseFloat(datas[3]);
        float yaw = Float.parseFloat(datas[3]);
        return new Location(world, x, y, z, pitch, yaw);
    }

    public static MachineType getMachineTypeByName(String name) {
        for (MachineType machineType : MachineTypes) {
            if(machineType.getKey().equals(name)) return machineType;
        }
        return null;
    }

}
