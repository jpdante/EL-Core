package com.ellisiumx.elcore.hologram;

import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import net.minecraft.server.v1_8_R3.Packet;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HologramManager implements Listener {

    public static HologramManager context;

    private ArrayList<Hologram> activeHolograms;

    public HologramManager(JavaPlugin plugin) {
        context = this;
        activeHolograms = new ArrayList<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static void addHologram(Hologram hologram) {
        context.activeHolograms.add(hologram);
    }

    public static void removeHologram(Hologram hologram) {
        context.activeHolograms.remove(hologram);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTick(UpdateEvent event) {
        if (event.getType() != UpdateType.TICK || activeHolograms.isEmpty()) return;
        List<World> worlds = Bukkit.getWorlds();
        Iterator<Hologram> iterator = activeHolograms.iterator();
        while (iterator.hasNext()) {
            Hologram hologram = iterator.next();
            if (!worlds.contains(hologram.getLocation().getWorld())) {
                iterator.remove();
                hologram.stop();
            } else {
                if (hologram.getEntityFollowing() != null) {
                    Entity following = hologram.getEntityFollowing();
                    if (hologram.isRemoveOnEntityDeath() && !following.isValid()) {
                        iterator.remove();
                        hologram.stop();
                        continue;
                    }
                    if (!hologram.relativeToEntity.equals(following.getLocation().subtract(hologram.getLocation()).toVector())) {
                        Vector vec = hologram.relativeToEntity.clone();
                        hologram.setLocation(following.getLocation().add(hologram.relativeToEntity));
                        hologram.relativeToEntity = vec;
                        continue;
                    }
                }
                ArrayList<Player> canSee = hologram.getNearbyPlayers();
                Iterator<Player> iterator2 = hologram.getPlayersTracking().iterator();
                while (iterator2.hasNext()) {
                    Player player = iterator2.next();
                    if (!canSee.contains(player)) {
                        iterator2.remove();
                        if (player.getWorld() == hologram.getLocation().getWorld()) {
                            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(hologram.getDestroyPacket());
                        }
                    }
                }
                for (Player player : canSee) {
                    if (!hologram.getPlayersTracking().contains(player)) {
                        hologram.getPlayersTracking().add(player);
                        for (Packet packet : hologram.getSpawnPackets()) {
                            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                        }
                    }
                }
            }
        }
    }
}
