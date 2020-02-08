package com.ellisiumx.elrankup.warp;

import com.ellisiumx.elcore.permissions.Rank;
import org.bukkit.Location;

public class Warp {

    private Location location;
    private Rank rank;

    public Warp(Location location, Rank rank) {
        this.location = location;
        this.rank = rank;
    }

    public Location getLocation() {
        return location;
    }

    public Rank getRank() {
        return rank;
    }
}
