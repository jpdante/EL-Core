package com.ellisiumx.elrankup.warp;

import com.ellisiumx.elcore.permissions.Rank;
import org.bukkit.Location;

public class Warp {

    private String name;
    private Location location;
    private Rank rank;

    public Warp(String name, Location location, Rank rank) {
        this.name = name;
        this.location = location;
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public Rank getRank() {
        return rank;
    }
}
