package com.ellisiumx.elrankup.antilogout;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.lang.LanguageDB;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import com.ellisiumx.elcore.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class AntiLogoutManager implements Listener {

    public static AntiLogoutManager context;
    private final HashMap<String, PlayerCombat> combatDelays;

    public AntiLogoutManager(JavaPlugin plugin) {
        context = this;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        combatDelays = new HashMap<>();
        for (LanguageDB languageDB : LanguageManager.getLanguages()) {
            languageDB.insertTranslation("AntiLogoutBottomText", "&cYou're in combat for &b%Delay% &cseconds.");
            languageDB.insertTranslation("AntiLogoutExitCombat", "&aYou got out of combat, now you can log out!");
            languageDB.insertTranslation("AntiLogoutEnterCombat", "&cYou went into combat, if you log out you will be punished!");
            languageDB.insertTranslation("AntiLogoutBroadcastPunish", "&f[&3AntiLogout&f] &cPlayer '&b%PlayerName%&c' logged out in PVP and was punished!");
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

    public PlayerCombat get(Player player) { return get(player.getName()); }

    public PlayerCombat get(String playerName) {
        synchronized (combatDelays) {
            return combatDelays.get(playerName);
        }
    }

    @EventHandler
    public void onTimerElapsed(UpdateEvent event) {
        if (event.getType() == UpdateType.SEC) {
            synchronized (combatDelays) {
                for (String key : combatDelays.keySet()) {
                    PlayerCombat playerCombat = get(key);
                    if (playerCombat.getDelay() <= 20) {
                        UtilTextBottom.display(
                                LanguageManager.getTranslation(PreferencesManager.get(playerCombat.getPlayer()).getLanguage(), "AntiLogoutBottomText")
                                        .replaceAll("%Delay%", String.valueOf(playerCombat.getDelay()))
                                        .replace('&', ChatColor.COLOR_CHAR),
                                playerCombat.getPlayer()
                        );
                    }
                    if (playerCombat.reduceTimeout()) {
                        combatDelays.remove(key);
                        playerCombat.getPlayer().sendMessage(LanguageManager.getTranslation(PreferencesManager.get(playerCombat.getPlayer()).getLanguage(), "AntiLogoutExitCombat").replace('&', ChatColor.COLOR_CHAR));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if(combatDelays.containsKey(event.getEntity().getName())) {
            combatDelays.remove(event.getEntity().getName());
            event.getEntity().sendMessage(LanguageManager.getTranslation(PreferencesManager.get((Player) event.getEntity()).getLanguage(), "AntiLogoutExitCombat").replace('&', ChatColor.COLOR_CHAR));
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (combatDelays.containsKey(event.getPlayer().getName())) {
            combatDelays.remove(event.getPlayer().getName());
            event.getPlayer().setHealth(0);
            for (Player player : UtilServer.getPlayers()) {
                player.sendMessage(
                        LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "AntiLogoutBroadcastPunish")
                                .replace("%PlayerName%", event.getPlayer().getDisplayName())
                                .replace('&', ChatColor.COLOR_CHAR)
                );
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) return;
        // Victim
        if (!combatDelays.containsKey(event.getEntity().getName())) {
            combatDelays.put(event.getEntity().getName(), new PlayerCombat((Player) event.getEntity(), 30));
            LanguageManager.getTranslation(PreferencesManager.get((Player) event.getEntity()).getLanguage(), "AntiLogoutEnterCombat").replace('&', ChatColor.COLOR_CHAR);
        } else {
            get(event.getEntity().getName()).setDelay(30);
        }
        // Damager
        if (!combatDelays.containsKey(event.getDamager().getName())) {
            combatDelays.put(event.getEntity().getName(), new PlayerCombat((Player) event.getDamager(), 30));
            LanguageManager.getTranslation(PreferencesManager.get((Player) event.getDamager()).getLanguage(), "AntiLogoutEnterCombat").replace('&', ChatColor.COLOR_CHAR);
        } else {
            get(event.getDamager().getName()).setDelay(30);
        }
    }

}
