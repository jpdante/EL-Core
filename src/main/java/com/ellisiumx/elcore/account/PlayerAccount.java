package com.ellisiumx.elcore.account;

import com.ellisiumx.elcore.permissions.Rank;
import org.bukkit.entity.Player;

public class PlayerAccount {
    private int accountId;
    private Player player;
    private String playerName;
    private Rank rank;
    private PlayerPreferences playerPreferences;
    private boolean updated = false;

    public PlayerAccount(Player player) {
        this.player = player;
    }

    public void load() {

    }

    public void save() {
        if(!wasUpdated()) return;
        resetUpdateTrigger();
    }

    public boolean wasUpdated() { return this.updated || this.playerPreferences.wasUpdated(); }

    public void resetUpdateTrigger() {
        this.updated = false;
        this.playerPreferences.resetUpdateTrigger();
    }
}
