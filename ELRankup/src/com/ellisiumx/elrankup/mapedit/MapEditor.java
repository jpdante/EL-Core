package com.ellisiumx.elrankup.mapedit;

import com.ellisiumx.elcore.utils.MapUtil;
import net.minecraft.server.v1_8_R3.ExceptionWorldConflict;
import net.minecraft.server.v1_8_R3.Tuple;
import org.bukkit.Location;

import java.util.*;

public final class MapEditor {
    public static Random rand = new Random();

    public static void setRetangle(Location p1, Location p2, BlockData... blockData) throws NullLocationException, NullBlockDataException, ExceptionWorldConflict {
        setRetangle(p1, p2, true, blockData);
    }

    public static void setRetangle(Location point1, Location point2, boolean async, BlockData... blockData) throws NullLocationException, NullBlockDataException, ExceptionWorldConflict {
        Tuple<Location, Location> points = getSetterPoints(point1, point2);
        if(blockData == null) throw new NullBlockDataException("blockData");
        Location p1 = points.a();
        Location p2 = points.b();
        if(blockData.length == 1) {
            for (int x = p1.getBlockX(); x <= p2.getBlockX(); ++x) {
                for (int y = p1.getBlockY(); y <= p2.getBlockY(); ++y) {
                    for (int z = p1.getBlockZ(); z <= p2.getBlockZ(); ++z) {
                        if(async) PastedBlock.BlockQueue.getQueue(p1.getWorld()).add(new PastedBlock(x, y, z, blockData[0].id, blockData[0].data));
                        else MapUtil.QuickChangeBlockAt(p1.getWorld(), x, y, z, blockData[0].id, blockData[0].data);
                    }
                }
            }
        } else {
            HashMap<BlockData, Double> blocks = new HashMap<>();
            for(BlockData data : blockData) {
                blocks.put(data, 1.0D / blockData.length);
            }
            List<CompositionEntry> probabilityMap = CompositionEntry.mapComposition(blocks);
            for (int x = p1.getBlockX(); x <= p2.getBlockX(); ++x) {
                for (int y = p1.getBlockY(); y <= p2.getBlockY(); ++y) {
                    for (int z = p1.getBlockZ(); z <= p2.getBlockZ(); ++z) {
                        double r = rand.nextDouble();
                        for (CompositionEntry ce : probabilityMap) {
                            if (r <= ce.chance) {
                                if(async) PastedBlock.BlockQueue.getQueue(p1.getWorld()).add(new PastedBlock(x, y, z, ce.block.id, ce.block.data));
                                else MapUtil.QuickChangeBlockAt(p1.getWorld(), x, y, z, blockData[0].id, blockData[0].data);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public static Tuple<Location, Location> getSetterPoints(Location point1, Location point2) throws NullLocationException, ExceptionWorldConflict {
        if(point1 == null) throw new NullLocationException("p1");
        if(point2 == null) throw new NullLocationException("p2");
        if(point1.getWorld() != point2.getWorld()) throw new ExceptionWorldConflict("The world of locations are different!");
        Location p1 = point1.clone();
        Location p2 = point2.clone();
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
        return new Tuple<>(p1, p2);
    }

    public static class NullBlockDataException extends Exception {
        private String message;

        public NullBlockDataException(String input) {
            message = "The block data '" + input + "' is null!";
        }

        @Override
        public String getMessage() {
            return message;
        }
    }

    public static class NullLocationException extends Exception {
        private String message;

        public NullLocationException(String input) {
            message = "The location '" + input + "' is null!";
        }

        @Override
        public String getMessage() {
            return message;
        }
    }
}