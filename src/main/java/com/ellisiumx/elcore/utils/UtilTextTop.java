package com.ellisiumx.elcore.utils;

import com.ellisiumx.elcore.entity.DummyEntity;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class UtilTextTop {
    //Base Commands
    public static void display(String text, Player... players) {
        displayProgress(text, 1, players);
    }

    public static void displayProgress(String text, double progress, Player... players) {
        for (Player player : players)
            displayTextBar(player, progress, text);
    }

    //Logic
    public static final int EntityDragonId = 777777;
    public static final int EntityWitherId = 777778;

    //Display
    public static void displayTextBar(final Player player, double healthPercent, String text) {
        deleteOld(player);
        healthPercent = Math.min(1, healthPercent);
        /*
        {
            Location loc = player.getLocation().subtract(0, 200, 0);

            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(getDragonPacket(text, healthPercent, true, loc));
        }*/
        Location loc = player.getEyeLocation().add(player.getLocation().getDirection().multiply(24));
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(getWitherPacket(text, healthPercent, true, loc));
        //Remove
        Bukkit.getServer().getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugins()[0], new Runnable() {
            public void run() {
                deleteOld(player);
            }
        }, 20);
    }

    private static void deleteOld(Player player) {
        PacketPlayOutEntityDestroy destroyDragonPacket = new PacketPlayOutEntityDestroy(EntityDragonId);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroyDragonPacket);
        PacketPlayOutEntityDestroy destroyWitherPacket = new PacketPlayOutEntityDestroy(EntityWitherId);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(destroyWitherPacket);
    }

    public static PacketPlayOutSpawnEntityLiving getDragonPacket(String text, double healthPercent, boolean halfHealth, Location loc) {
        EntityEnderDragon entityEnderDragon = new EntityEnderDragon(((CraftWorld) loc.getWorld()).getHandle());
        entityEnderDragon.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        double health = healthPercent * 199.9F + 0.1F;
        entityEnderDragon.setHealth((float) health);
        entityEnderDragon.setCustomName(text);
        entityEnderDragon.setCustomNameVisible(true);

        PacketPlayOutSpawnEntityLiving mobPacket = new PacketPlayOutSpawnEntityLiving(entityEnderDragon);
        //DataWatcher watcher = getWatcher(entityEnderDragon.getDataWatcher(), text, health, loc.getWorld());
        //mobPacket. = watcher;
        return mobPacket;
    }

    public static PacketPlayOutSpawnEntityLiving getWitherPacket(String text, double healthPercent, boolean halfHealth, Location loc) {
        EntityWither entityWither = new EntityWither(((CraftWorld) loc.getWorld()).getHandle());
        entityWither.setLocation(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
        double health = healthPercent * 199.9F + 0.1F;
        entityWither.setHealth((float) health);
        entityWither.setCustomName(text);
        entityWither.setCustomNameVisible(true);

        PacketPlayOutSpawnEntityLiving mobPacket = new PacketPlayOutSpawnEntityLiving(entityWither);
        //DataWatcher watcher = getWatcher(entityEnderDragon.getDataWatcher(), text, health, loc.getWorld());
        //mobPacket. = watcher;
        return mobPacket;
    }

    public static DataWatcher getWatcher(DataWatcher watcher, String text, double health, World world) {
        watcher.a(0, (Byte) (byte) 0);            //Flags, 0x20 = invisible
        watcher.a(6, (Float) (float) health);
        watcher.a(2, (String) text);            //Entity name
        watcher.a(10, (String) text);            //Entity name
        watcher.a(3, (Byte) (byte) 0);            //Show name, 1 = show, 0 = don't show
        watcher.a(11, (Byte) (byte) 0);        //Show name, 1 = show, 0 = don't show
        watcher.a(16, (Integer) (int) health);    //Health
        watcher.a(20, (Integer) (int) 881);        //Inv
        int i1 = watcher.getInt(0);
        watcher.watch(0, (byte) (i1 | 1 << 5));
        return watcher;
    }
}