package com.ellisiumx.elrankup.economy;

import org.bukkit.entity.Player;

public class PlayerMoney {
    public Player player;
    public double money;
    public boolean updated;
    public double cash;

    public PlayerMoney(Player player, double money, double cash) {
        this.player = player;
        this.money = money;
        this.updated = false;
        this.cash = cash;
    }


}
