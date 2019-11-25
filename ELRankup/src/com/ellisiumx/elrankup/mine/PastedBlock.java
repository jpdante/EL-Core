package com.ellisiumx.elrankup.mine;

import com.ellisiumx.elcore.utils.MapUtil;
import com.ellisiumx.elrankup.ELRankup;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

public class PastedBlock {

    private int x, y, z, id;
    private byte data;

    public PastedBlock(int x, int y, int z, int id, byte data) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
        this.data = data;
    }

    public static class BlockQueue {
        private Deque<PastedBlock> queue = new ConcurrentLinkedDeque<>();
        private static Map<World, BlockQueue> queueMap = new ConcurrentHashMap<>();

        public void add(PastedBlock block) {
            queue.add(block);
        }

        public BlockQueue(final World world) {
            Bukkit.getScheduler().scheduleSyncRepeatingTask(ELRankup.getContext(), () -> {
                PastedBlock block;
                boolean hasTime = true;
                long start = System.currentTimeMillis();
                while ((block = queue.poll()) != null && hasTime) {
                    hasTime = System.currentTimeMillis() - start < 10;
                    MapUtil.QuickChangeBlockAt(world, block.x, block.y, block.z, block.id, block.data);
                }
            }, 1, 1);
        }

        public static BlockQueue getQueue(World w) {
            if (!queueMap.containsKey(w)) {
                BlockQueue blockQueue = new BlockQueue(w);
                queueMap.put(w, blockQueue);
                return blockQueue;
            }
            return queueMap.get(w);
        }
    }
}
