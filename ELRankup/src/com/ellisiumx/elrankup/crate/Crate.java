package com.ellisiumx.elrankup.crate;

import org.bukkit.block.Chest;
import org.bukkit.entity.Player;

public class Crate {

    private Player player;
    private Chest chest;
    private CrateType crateType;
    private int timer;

    public Crate(Player player, Chest chest, CrateType crateType) {
        this.player = player;
        this.chest = chest;
        this.crateType = crateType;
    }

    public void animate() {
        timer++;
    }

}
