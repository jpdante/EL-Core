package com.ellisiumx.elrankup.kit;

import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.HashMap;

public class PlayerKit {

    private Player player;
    private HashMap<Kit, Timestamp> kitDelay;

    public PlayerKit(Player player) {
        this.player = player;
        kitDelay = new HashMap<>();
    }

    public Player getPlayer() {
        return player;
    }

    public HashMap<Kit, Timestamp> getKitDelay() {
        return kitDelay;
    }
}
