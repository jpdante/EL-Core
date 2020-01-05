package com.ellisiumx.elrankup.economy;

import org.bukkit.entity.Player;

public class PlayerMoney {
    public Player player;
    public double money;
    public boolean updated;

    public PlayerMoney(Player player, double money) {
        this.player = player;
        this.money = money;
        this.updated = false;
    }
}
