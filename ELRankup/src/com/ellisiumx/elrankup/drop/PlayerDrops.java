package com.ellisiumx.elrankup.drop;

import org.bukkit.entity.Player;

public class PlayerDrops {

    private Player player;
    private long drops;

    public PlayerDrops(long drops) {
        this.drops = drops;
    }

    public Player getPlayer() { return player; }

    public void setPlayer(Player player) { this.player = player; }

    public long getDrops() { return drops; }

    public void setDrops(long drops) { this.drops = drops; }
}
