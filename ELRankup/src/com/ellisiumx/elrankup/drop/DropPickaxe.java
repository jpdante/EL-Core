package com.ellisiumx.elrankup.drop;

import org.bukkit.entity.Player;

public class DropPickaxe {

    private Player player;
    private String pickaxeID;
    private double xp;

    public DropPickaxe(Player player, String pickaxeID, double xp) {
        this.player = player;
        this.pickaxeID = pickaxeID;
        this.xp = xp;
    }

    public int getLevel() {
        return (int)(Math.log(xp) / 0.30102999566D);
    }

    public Player getPlayer() {
        return player;
    }

    public String getPickaxeID() {
        return pickaxeID;
    }

    public void setPickaxeID(String pickaxeID) {
        this.pickaxeID = pickaxeID;
    }

    public double getXP() {
        return xp;
    }

    public void setXP(double xp) {
        this.xp = xp;
    }
}
