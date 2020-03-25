package com.ellisiumx.elrankup.kit;

import com.ellisiumx.elcore.account.CoreClientManager;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.HashMap;

public class PlayerKit {

    private Player player;
    private int accountId;
    private HashMap<Kit, Long> kitDelay;

    public PlayerKit(Player player) {
        this.player = player;
        this.accountId = CoreClientManager.get(player).getAccountId();
        this.kitDelay = new HashMap<>();
    }

    public Player getPlayer() {
        return player;
    }

    public int getAccountId() {
        return accountId;
    }

    public HashMap<Kit, Long> getKitDelay() {
        return kitDelay;
    }
}
