package com.ellisiumx.elrankup.antilogout;

import org.bukkit.entity.Player;

public class PlayerCombat {

    private Player player;
    private int delay;

    public PlayerCombat(Player player, int delay) {
        this.player = player;
        this.delay = delay;
    }

    public Player getPlayer() { return this.player; }

    public void setDelay(int delay) { this.delay = delay; }

    public int getDelay() { return this.delay; }

    public boolean reduceTimeout() {
        this.delay--;
        return this.delay <= 0;
    }

}
