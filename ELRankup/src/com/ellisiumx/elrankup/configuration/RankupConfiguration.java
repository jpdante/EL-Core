package com.ellisiumx.elrankup.configuration;

import com.ellisiumx.elrankup.ELRankup;
import com.ellisiumx.elrankup.mine.BlockData;
import com.ellisiumx.elrankup.mine.MineData;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class RankupConfiguration {

    public static boolean Mines_Enabled;
    public static List<MineData> Mines;

    public RankupConfiguration() {
        Mines_Enabled = ELRankup.getContext().getConfig().getBoolean("mines.enabled");
        Mines = new ArrayList<>();
        for(String key : ELRankup.getContext().getConfig().getConfigurationSection("mine-reseter.mines").getKeys(false)) {
            Location point1 = stringToLocation(ELRankup.getContext().getConfig().getString("mine-reseter.mines." + key + ".point1"));
            Location point2 = stringToLocation(ELRankup.getContext().getConfig().getString("mine-reseter.mines." + key + ".point2"));
            int delay = ELRankup.getContext().getConfig().getInt("mine-reseter.mines." + key + ".delay");
            MineData mineData = new MineData(point1, point2, delay);
            for(String ore : ELRankup.getContext().getConfig().getStringList("mine-reseter.mines." + key + ".ores")) {
                String[] datas = ore.split(",", 2);
                String[] item = datas[1].split(":", 2);
                mineData.getBlocks().put(new BlockData(Integer.parseInt(item[0]), Byte.parseByte(item[1])), Double.parseDouble(datas[0]));
            }
            Mines.add(mineData);
        }
    }

    public static void save() {

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
