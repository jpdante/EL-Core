package com.ellisiumx.elrankup.warp;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerWarp {

    private Player player;
    private Location from;
    private Location to;
    private int delay;

    public PlayerWarp(Player player, Location from, Location to, int delay) {
        this.player = player;
        this.from = from;
        this.to = to;
        this.delay = delay;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getFrom() {
        return from;
    }

    public Location getTo() {
        return to;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }
}
