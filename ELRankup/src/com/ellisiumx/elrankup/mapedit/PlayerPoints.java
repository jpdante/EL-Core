package com.ellisiumx.elrankup.mapedit;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PlayerPoints {

    private Player player;
    private Location point1;
    private Location point2;

    public PlayerPoints(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getPoint1() {
        return point1;
    }

    public void setPoint1(Location point1) {
        this.point1 = point1;
    }

    public Location getPoint2() {
        return point2;
    }

    public void setPoint2(Location point2) {
        this.point2 = point2;
    }
}
