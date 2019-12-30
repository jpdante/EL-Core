package com.ellisiumx.elrankup.mine;

import com.ellisiumx.elrankup.mapedit.BlockData;
import com.ellisiumx.elrankup.mapedit.CompositionEntry;
import com.ellisiumx.elrankup.mapedit.MapEditor;
import com.ellisiumx.elrankup.mapedit.PastedBlock;
import net.minecraft.server.v1_8_R3.ExceptionWorldConflict;
import net.minecraft.server.v1_8_R3.Tuple;
import org.bukkit.Bukkit;
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

    public String key;
    public String name;
    public Location middle;
    public int alertArea;
    public boolean enabled;
    public int delay;
    public int currentDelay;

    public MineData(String key, String name, boolean enabled, int alertArea, Location point1, Location point2, int delay) {
        this.key = key;
        this.name = name;
        this.enabled = enabled;
        this.alertArea = alertArea;
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

    public void setPoints(Location point1, Location point2) {
        try {
            Tuple<Location, Location> points = MapEditor.getSetterPoints(point1, point2);
            Location p1 = points.a();
            Location p2 = points.b();

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

            this.middle = new Location(p1.getWorld(), (minX - maxX) / 2F, (minY - maxY) / 2F, (minZ - maxZ) / 2F);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isInside(Player p) {
        Location l = p.getLocation();
        return l.getWorld().equals(world)
                && (l.getX() >= minX && l.getX() <= maxX)
                && (l.getY() >= minY && l.getY() <= maxY)
                && (l.getZ() >= minZ && l.getZ() <= maxZ);
    }

    public void reset() {
        List<CompositionEntry> probabilityMap = CompositionEntry.mapComposition(blocks);
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

}
