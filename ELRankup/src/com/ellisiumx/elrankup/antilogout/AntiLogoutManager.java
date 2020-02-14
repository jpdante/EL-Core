package com.ellisiumx.elrankup.antilogout;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.lang.LanguageDB;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import com.ellisiumx.elcore.utils.UtilAction;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilPlayer;
import com.ellisiumx.elcore.utils.UtilTextBottom;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class AntiLogoutManager implements Listener {

    public static AntiLogoutManager context;
    private final HashMap<String, Integer> combatDelays;

    public AntiLogoutManager(JavaPlugin plugin) {
        context = this;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        combatDelays = new HashMap<>();
        for (LanguageDB languageDB : LanguageManager.getLanguages()) {
            // TODO: Insert messages
            /*languageDB.insertTranslation("MachineTransactionFailure", "&f[&aMachines&f] &cFailed to transfer, please try again later. %ErrorMessage%");
            languageDB.insertTranslation("MachineNotEnoughMoney", "&f[&aMachines&f] &cYou don't have enough money to buy %MachineType%&c, it costs %Cost%");
            languageDB.insertTranslation("FuelNotEnoughMoney", "&f[&aMachines&f] &cYou don't have enough money to buy Fuel, it costs &a%Cost%");
            languageDB.insertTranslation("MachineLimitReached", "&f[&aMachines&f] &cMachine limit for %MachineType%&c has been reached!");
            languageDB.insertTranslation("MachineBought", "&f[&aMachines&f] &aMachine %MachineType%&a bought successfully!");
            languageDB.insertTranslation("MachineFuelBought", "&f[&aMachines&f] &aFuel bought successfully!");
            languageDB.insertTranslation("MachineUpgraded", "&f[&aMachines&f] &aMachine successfully upgraded!");
            languageDB.insertTranslation("MachineFullDrop", "&f[&aMachines&f] &cThe machine is already full and can no longer work, sell the drops to get it back to work.");
            languageDB.insertTranslation("MachineTankAlreadyFull", "&f[&aMachines&f] &cThe fuel tank of the machine is already full!");
            languageDB.insertTranslation("MachineTankReFull", "&f[&aMachines&f] &aThe machine has been replenished!");
            languageDB.insertTranslation("MachineDropsSold", "&f[&aMachines&f] &a%DropsAmount% drops were sold for %TotalPrice%, your new balance is %Balance%.");
            languageDB.insertTranslation("MachineNoMenu", "&f[&aMachines&f] &cYou need to buy at least one machine to be able to see your machines.");*/
        }
        if (LanguageManager.saveLanguages()) LanguageManager.reloadLanguages();
    }

    public boolean isInCombat(Player player) {
        return isInCombat(player.getName());
    }

    public boolean isInCombat(String playerName) {
        synchronized (combatDelays) {
            return combatDelays.containsKey(playerName);
        }
    }

    @EventHandler
    public void onTimerElapsed(UpdateEvent event) {
        if (event.getType() == UpdateType.SEC) {
            synchronized (combatDelays) {
                for(String key : combatDelays.keySet()) {
                    int delay = combatDelays.get(key);
                    if(combatDelays.get(key) <= 20) {
                        // TODO: Fix message
                        UtilTextBottom.display(UtilChat.cRed + "You're in combat for " + UtilChat.cDPurple + delay + UtilChat.cRed + " seconds.", UtilPlayer.searchExact(key));
                        if(combatDelays.get(key) <= 0) {
                            combatDelays.remove(key);
                            // TODO: Remove from combat
                        }
                    }
                    combatDelays.replace(key, combatDelays.get(key) - 1);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        combatDelays.remove(event.getEntity().getName());
        // TODO: Tell exited combat
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if(combatDelays.containsKey(event.getPlayer().getName())) {
            combatDelays.remove(event.getPlayer().getName());
            // TODO: Kill and Punish
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Victim
        if(!combatDelays.containsKey(event.getEntity().getName())) {
            combatDelays.put(event.getEntity().getName(), 30);
        } else {
            combatDelays.replace(event.getEntity().getName(), 30);
        }
        // Damager
        if(!combatDelays.containsKey(event.getDamager().getName())) {
            combatDelays.put(event.getDamager().getName(), 30);
        } else {
            combatDelays.replace(event.getDamager().getName(), 30);
        }
    }

}
