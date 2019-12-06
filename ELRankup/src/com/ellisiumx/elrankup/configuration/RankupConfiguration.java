package com.ellisiumx.elrankup.configuration;

import com.ellisiumx.elrankup.ELRankup;
import com.ellisiumx.elrankup.mapedit.BlockData;
import com.ellisiumx.elrankup.mine.MineData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class RankupConfiguration {

    public static boolean MinesEnabled;
    public static List<Integer> AlertTimes;
    public static List<MineData> Mines;

    public RankupConfiguration() {
        MinesEnabled = ELRankup.getContext().getConfig().getBoolean("mine-reseter.enabled");
        AlertTimes = ELRankup.getContext().getConfig().getIntegerList("mine-reseter.alert-times");
        Mines = new ArrayList<>();
        for(String key : ELRankup.getContext().getConfig().getConfigurationSection("mine-reseter.mines").getKeys(false)) {
            String name = ELRankup.getContext().getConfig().getString("mine-reseter.mines." + key + ".name");
            boolean enabled = ELRankup.getContext().getConfig().getBoolean("mine-reseter.mines." + key + ".enabled");
            int alertArea = ELRankup.getContext().getConfig().getInt("mine-reseter.mines." + key + ".alert-area");
            Location point1 = stringToLocation(ELRankup.getContext().getConfig().getString("mine-reseter.mines." + key + ".point1"));
            Location point2 = stringToLocation(ELRankup.getContext().getConfig().getString("mine-reseter.mines." + key + ".point2"));
            int delay = ELRankup.getContext().getConfig().getInt("mine-reseter.mines." + key + ".delay");
            MineData mineData = new MineData(name, enabled, alertArea, point1, point2, delay);
            for(String ore : ELRankup.getContext().getConfig().getStringList("mine-reseter.mines." + key + ".ores")) {
                String[] datas = ore.split(",", 2);
                String[] item = datas[1].split(":", 2);
                mineData.getBlocks().put(new BlockData(Integer.parseInt(item[0]), Byte.parseByte(item[1])), Double.parseDouble(datas[0]));
            }
            Mines.add(mineData);
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
            for(BlockData blockData : mine.getBlocks().keySet()) {
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

}
