package com.ellisiumx.elcore.hologram;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import java.util.*;

public class Hologram {

    public enum HologramTarget {
        BLACKLIST, WHITELIST;
    }

    private Location location;
    private HashSet<String> playersInList = new HashSet<>();
    private ArrayList<Player> playersTracking = new ArrayList<>();
    private String[] hologramText = new String[0];
    private Entity followEntity;
    private ArrayList<EntityArmorStand> entities;
    private boolean removeEntityDeath;
    private int viewDistance = 70;
    private HologramTarget target = HologramTarget.BLACKLIST;
    private boolean spawned;
    protected Vector relativeToEntity;
    private Vector lastMovement;
    private boolean makeDestroyPackets = true;
    private boolean makeSpawnPackets = true;
    private Packet destroyPacket;
    private Packet[] spawnPackets;

    public Hologram(Location location, String... text) {
        this.location = location.clone();
        setText(text);
    }

    public Hologram addPlayer(Player player) {
        return addPlayer(player.getName());
    }

    public Hologram addPlayer(String player) {
        this.playersInList.add(player);
        return this;
    }

    public boolean containsPlayer(Player player) {
        return this.playersInList.contains(player.getName());
    }

    public boolean containsPlayer(String player) {
        return this.playersInList.contains(player);
    }

    public Hologram removePlayer(Player player) {
        return addPlayer(player.getName());
    }

    public Hologram removePlayer(String player) {
        this.playersInList.remove(player);
        return this;
    }

    public Location getLocation() {
        return this.location.clone();
    }

    public Hologram setLocation(Location newLocation) {
        makeSpawnPackets = true;
        Location oldLocation = getLocation();
        location = newLocation.clone();
        if (getEntityFollowing() != null) {
            relativeToEntity = location.clone().subtract(getEntityFollowing().getLocation()).toVector();
        }
        if (isInUse()) {
            ArrayList<Player> canSee = getNearbyPlayers();
            Iterator<Player> iterator = playersTracking.iterator();
            while (iterator.hasNext()) {
                Player player = iterator.next();
                if (!canSee.contains(player)) {
                    iterator.remove();
                    if (player.getWorld() == getLocation().getWorld()) {
                        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(getDestroyPacket());
                    }
                }
            }
            iterator = canSee.iterator();
            while (iterator.hasNext()) {
                Player player = iterator.next();
                if (!playersTracking.contains(player)) {
                    playersTracking.add(player);
                    iterator.remove();
                    for (Packet packet : getSpawnPackets()) {
                        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                    }
                }
            }
            if (!canSee.isEmpty()) {
                lastMovement.add(new Vector(newLocation.getX() - oldLocation.getX(),newLocation.getY() - oldLocation.getY(),newLocation.getZ() - oldLocation.getZ()));
                int x = (int) Math.floor(32 * lastMovement.getX());
                int y = (int) Math.floor(32 * lastMovement.getY());
                int z = (int) Math.floor(32 * lastMovement.getZ());
                Packet[] packets1_8 = new Packet[hologramText.length];
                int i = 0;
                if (x >= -128 && x <= 127 && y >= -128 && y <= 127 && z >= -128 && z <= 127) {
                    lastMovement.subtract(new Vector(x / 32D, y / 32D, z / 32D));
                    for (EntityArmorStand entity : this.entities) {
                        PacketPlayOutEntity.PacketPlayOutRelEntityMove relMove = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(entity.getId(), (byte) x,(byte) y,(byte) z,true);
                        packets1_8[i] = relMove;
                        i++;
                    }
                } else {
                    x = (int) Math.floor(32 * newLocation.getX());
                    z = (int) Math.floor(32 * newLocation.getZ());
                    lastMovement = new Vector(newLocation.getX() - (x / 32D), 0, newLocation.getZ() - (z / 32D));
                    for (EntityArmorStand entity : this.entities) {
                        int id = entity.getId();
                        int y2 = (int) Math.floor((oldLocation.getY() + 54.6 + ((double) i * 0.285)) * 32);
                        PacketPlayOutEntityTeleport teleportPacket = new PacketPlayOutEntityTeleport(id, x, y2, z, (byte) 0, (byte) 0,true);
                        packets1_8[i] = teleportPacket;
                        i++;
                    }
                }
                for (Player player : canSee) {
                    for (Packet packet : packets1_8) {
                        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                    }
                }
            }
        }
        return this;
    }

    public Entity getEntityFollowing() {
        return followEntity;
    }

    public Hologram setFollowEntity(Entity entityToFollow) {
        followEntity = entityToFollow;
        relativeToEntity = entityToFollow == null ? null : this.location.clone().subtract(entityToFollow.getLocation()).toVector();
        return this;
    }

    public int getViewDistance() {
        return this.viewDistance;
    }

    public Hologram setViewDistance(int newDistance) {
        this.viewDistance = newDistance;
        return setLocation(getLocation());
    }

    public HologramTarget getHologramTarget() {
        return target;
    }

    public Hologram setHologramTarget(HologramTarget newTarget) {
        this.target = newTarget;
        return this;
    }

    protected ArrayList<Player> getNearbyPlayers() {
        ArrayList<Player> nearbyPlayers = new ArrayList<>();
        for (Player player : getLocation().getWorld().getPlayers()) {
            if (isVisible(player)) {
                nearbyPlayers.add(player);
            }
        }
        return nearbyPlayers;
    }

    public boolean isVisible(Player player) {
        if (getLocation().getWorld() == player.getWorld()) {
            if ((getHologramTarget() == HologramTarget.WHITELIST) == containsPlayer(player)) {
                if (getLocation().distance(player.getLocation()) < getViewDistance()) {
                    return true;
                }
            }
        }
        return false;
    }

    protected ArrayList<Player> getPlayersTracking() {
        return playersTracking;
    }

    public boolean isRemoveOnEntityDeath() {
        return removeEntityDeath;
    }

    public Hologram setRemoveOnEntityDeath(boolean value) {
        removeEntityDeath = value;
        return this;
    }

    public boolean isInUse() {
        return lastMovement != null;
    }

    public Hologram setText(String... newLines) {
        if (newLines.equals(this.hologramText)) return this;
        makeSpawnPackets = true;
        if (isInUse()) {
            if (hologramText.length != newLines.length) {
                makeDestroyPackets = true;
            }
            int[] destroyIDs = new int[0];
            ArrayList<Packet> packets = new ArrayList<>();
            for (int i = 0; i < Math.max(hologramText.length, newLines.length); i++) {
                if (i >= hologramText.length) {
                    // Add
                    EntityArmorStand entity = new EntityArmorStand(((CraftWorld) this.location.getWorld()).getHandle(), this.location.getX(), this.location.getY(), this.location.getZ());
                    entity.setCustomName(newLines[i]);
                    entity.setCustomNameVisible(true);
                    entity.setInvisible(true);
                    entity.setGravity(false);
                    this.location.subtract(0, i * 0.285D, 0);
                    this.entities.add(entity);
                    packets.add(new PacketPlayOutSpawnEntityLiving(entity));
                } else if (i > newLines.length) {
                    // Remove
                    destroyIDs = Arrays.copyOf(destroyIDs, destroyIDs.length + 1);
                    destroyIDs[i] = this.entities.get(i).getId();
                    this.entities.remove(i);
                } else if (!newLines[i].equals(this.hologramText[i])) {
                    // Update
                    EntityArmorStand entity = this.entities.get(i);
                    entity.setCustomName(newLines[i]);
                    packets.add(new PacketPlayOutEntityMetadata(entity.getId(), entity.getDataWatcher(), true));
                }
            }
            if (destroyIDs.length > 0) {
                packets.add(new PacketPlayOutEntityDestroy(destroyIDs));
            }
            for (Player player : this.playersTracking) {
                for (Packet packet : packets) {
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                }
            }
        }
        this.hologramText = newLines;
        return this;
    }

    public String[] getText() {
        String[] reversed = new String[hologramText.length];
        for (int i = 0; i < reversed.length; i++) {
            reversed[i] = hologramText[reversed.length - (i + 1)];
        }
        return reversed;
    }

    public void makeDestroyPacket() {
        int[] destroyEntities = new int[this.entities.size()];
        for (int i = 0; i < this.entities.size(); i++) {
            destroyEntities[i] = this.entities.get(i).getId();
        }
        this.destroyPacket = new PacketPlayOutEntityDestroy(destroyEntities);
    }

    public void makeSpawnPackets() {
        Packet[] spawnPackets = new Packet[this.entities.size()];
        for (int i = 0; i < this.entities.size(); i++) {
            spawnPackets[i] = new PacketPlayOutSpawnEntityLiving(this.entities.get(i));
        }
        this.spawnPackets = spawnPackets;
    }

    public Packet getDestroyPacket() {
        if (makeDestroyPackets) {
            makeDestroyPacket();
            this.makeDestroyPackets = false;
        }
        return destroyPacket;
    }

    public Packet[] getSpawnPackets() {
        if (makeSpawnPackets) {
            makeSpawnPackets();
            this.makeSpawnPackets = false;
        }
        return spawnPackets;
    }

    public Hologram start() {
        if (!isInUse()) {
            HologramManager.addHologram(this);
            playersTracking.addAll(getNearbyPlayers());
            for (Player player : playersTracking) {
                for (Packet packet : getSpawnPackets()) {
                    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
                }
            }
            lastMovement = new Vector();
        }
        return this;
    }

    public void stop() {
        Packet packet = getDestroyPacket();
        for (Player player : this.playersTracking) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
        HologramManager.removeHologram(this);
    }

}

