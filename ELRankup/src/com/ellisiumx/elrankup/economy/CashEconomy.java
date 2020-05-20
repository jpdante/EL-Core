package com.ellisiumx.elrankup.economy;

import org.bukkit.OfflinePlayer;

public class CashEconomy {

    public double getCash(OfflinePlayer offPlayer){
        if (EconomyManager.context.playerMonies.containsKey(offPlayer.getName()))
            return EconomyManager.context.playerMonies.get(offPlayer.getName()).cash;
        else {
            Double cash = EconomyManager.repository.getCashByUUID(offPlayer.getUniqueId().toString());
            if (cash != null) return cash;
            else return -1;
        }
    }

    public double addCash(OfflinePlayer offPlayer, Double cash){
        return 0.0;
    }

}
