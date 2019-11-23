package com.ellisiumx.elcore.account;

import com.ellisiumx.elcore.permissions.Rank;
import org.bukkit.entity.Player;

public class CoreClient {
    private int accountId = -1;
    private Player player = null;
    private String name = null;
    private Rank rank = Rank.ALL;

    public CoreClient(Player player) {
        this.player = player;
    }

    public CoreClient(String name) {
        this.name = name;
    }

    public CoreClient(ClientCache cache) {
        this.accountId = cache.accountId;
        this.name = cache.name;
        this.rank = cache.rank;
    }

    public int getAccountId() {
        return accountId;
    }

    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getPlayerName() {
        return name;
    }

    public void setPlayerName(String playerName) {
        this.name = playerName;
    }

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public void delete() {
        name = null;
        player = null;
    }
}
