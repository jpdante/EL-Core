package com.ellisiumx.elrankup.machine;

import org.bukkit.Location;

import java.sql.Timestamp;

public class Machine {

    private int id;
    private MachineType type;
    private int owner;
    private int level;
    private int drops;
    private int fuel;
    private Timestamp lastMenuOpen;
    private Timestamp lastRefuel;

    public Machine(int id, MachineType type, int owner, int level, int drops, int fuel, Timestamp lastMenuOpen, Timestamp lastRefuel) {
        this.id = id;
        this.type = type;
        this.owner = owner;
        this.level = level;
        this.drops = drops;
        this.fuel = fuel;
        this.lastMenuOpen = lastMenuOpen;
        this.lastRefuel = lastRefuel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOwner() {
        return owner;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getDrops() {
        return drops;
    }

    public void setDrops(int drops) {
        this.drops = drops;
    }

    public int getFuel() {
        return fuel;
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }

    public MachineType getType() {
        return type;
    }

    public Timestamp getLastMenuOpen() {
        return lastMenuOpen;
    }

    public Timestamp getLastRefuel() {
        return lastRefuel;
    }
}
