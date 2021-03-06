package com.ellisiumx.elrankup.clan;

import org.bukkit.entity.Player;

public class ClanPlayerInvite {

    private Player player;
    private Clan clan;
    private int timeout;

    public ClanPlayerInvite(Player player, Clan clan, int timeout) {
        this.player = player;
        this.clan = clan;
        this.timeout = timeout;
    }

    public Player getPlayer() {
        return player;
    }

    public Clan getClan() {
        return clan;
    }

    public int getTimeout() {
        return timeout;
    }

    public void reduceTimeout() {
        this.timeout--;
    }
}
