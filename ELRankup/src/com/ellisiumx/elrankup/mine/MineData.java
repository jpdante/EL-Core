package com.ellisiumx.elrankup.mine;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.utils.MapUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.*;

public class MineData {
    public static Random rand = new Random();

    private int minX;
    private int minY;
    private int minZ;
    private int maxX;
    private int maxY;
    private int maxZ;
    private World world;
    private Map<BlockData, Double> blocks;
    private Location point1;
    private Location point2;

    public boolean enabled;
    public int delay;
    public int currentDelay;

    public MineData(Location point1, Location point2, int delay) {
        setPoints(point1, point2);
        if(delay <= 4) this.delay = 5;
        else this.delay = delay;
        this.currentDelay = delay;
        this.enabled = false;
        this.blocks = new HashMap<>();
    }

    public Map<BlockData, Double> getBlocks() {
        return blocks;
    }

    public Location getPoint1() {
        return point1;
    }

    public Location getPoint2() {
        return point2;
    }

    public void setPoints(Location p1, Location p2) {
        if (p1.getX() > p2.getX()) {
            double x = p1.getX();
            p1.setX(p2.getX());
            p2.setX(x);
        }
        if (p1.getY() > p2.getY()) {
            double y = p1.getY();
            p1.setY(p2.getY());
            p2.setY(y);
        }
        if (p1.getZ() > p2.getZ()) {
            double z = p1.getZ();
            p1.setZ(p2.getZ());
            p2.setZ(z);
        }

        this.point1 = p1;
        this.world = p1.getWorld();
        this.minX = p1.getBlockX();
        this.minY = p1.getBlockY();
        this.minZ = p1.getBlockZ();

        this.point2 = p2;
        this.world = p2.getWorld();
        this.maxX = p2.getBlockX();
        this.maxY = p2.getBlockY();
        this.maxZ = p2.getBlockZ();
    }

    public boolean isInside(Player p) {
        Location l = p.getLocation();
        return l.getWorld().equals(world)
                && (l.getX() >= minX && l.getX() <= maxX)
                && (l.getY() >= minY && l.getY() <= maxY)
                && (l.getZ() >= minZ && l.getZ() <= maxZ);
    }

    public void fillMine() {
        Bukkit.broadcastMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Resetting!!!");
        List<CompositionEntry> probabilityMap = mapComposition(blocks);
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    /*if (y == maxY) {
                        world.getBlockAt(x, y, z).setTypeIdAndData(166, (byte) 0, false);
                        continue;
                    }*/
                    double r = rand.nextDouble();
                    for (CompositionEntry ce : probabilityMap) {
                        if (r <= ce.chance) {
                            PastedBlock.BlockQueue.getQueue(world).add(new PastedBlock(x, y, z, ce.block.id, ce.block.data));
                            break;
                        }
                    }
                }
            }
        }
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            Location location = player.getLocation();
            if (isInside(player)) {
                player.teleport(new Location(world, location.getX(), maxY + 2D, location.getZ()));
            }
        }
    }

    public static class CompositionEntry {
        public BlockData block;
        public double chance;

        public CompositionEntry(BlockData block, double chance) {
            this.block = block;
            this.chance = chance;
        }
    }

    public static ArrayList<CompositionEntry> mapComposition(Map<BlockData, Double> compositionIn) {
        ArrayList<CompositionEntry> probabilityMap = new ArrayList<CompositionEntry>();
        Map<BlockData, Double> composition = new HashMap<BlockData, Double>(compositionIn);
        double max = 0;
        for (Map.Entry<BlockData, Double> entry : composition.entrySet()) {
            max += entry.getValue();
        }
        //Pad the remaining percentages with air
        if (max < 1) {
            composition.put(new BlockData(0), 1 - max);
            max = 1;
        }
        double i = 0;
        for (Map.Entry<BlockData, Double> entry : composition.entrySet()) {
            double v = entry.getValue() / max;
            i += v;
            probabilityMap.add(new CompositionEntry(entry.getKey(), i));
        }
        return probabilityMap;
    }
        /*public void fillMine() {
        List<CompositionEntry> probabilityMap = mapComposition(blocks);
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            Location l = p.getLocation();
            if (isInside(p)) {
                p.teleport(new Location(world, l.getX(), maxY + 2D, l.getZ()));
            }
        }
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    if (world.getBlockTypeIdAt(x, y, z) == 0) {
                        if (y == maxY) {
                            MapUtil.QuickChangeBlockAt(world, x, y, z, 166, (byte) 0);
                            continue;
                        }
                        double r = rand.nextDouble();
                        for (CompositionEntry ce : probabilityMap) {
                            if (r <= ce.chance) {
                                MapUtil.QuickChangeBlockAt(world, x, y, z, ce.block.id, ce.block.data);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }*/

}
