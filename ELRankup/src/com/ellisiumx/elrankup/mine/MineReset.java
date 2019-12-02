package com.ellisiumx.elrankup.mine;

import com.ellisiumx.elcore.lang.LanguageDB;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elcore.preferences.UserPreferences;
import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import com.ellisiumx.elcore.utils.UtilPlayer;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.mine.command.MineResetCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;

public class MineReset implements Listener {

    private static MineReset context;

    public MineReset(JavaPlugin plugin) {
        context = this;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        for(LanguageDB languageDB : LanguageManager.getLanguages()) {
            languageDB.insertTranslation("MineResetingAlert", "&f[&aELRankup&f] &bReseting mine %MineName% in %TimeRemaining% seconds!");
            languageDB.insertTranslation("MineResetAlert", "&f[&aELRankup&f] &bMine %MineName% reseted!");
        }
        if(LanguageManager.saveLanguages()) LanguageManager.reloadLanguages();
        new MineResetCommand(plugin);
    }

    public static void start() {
        for(MineData mineData : RankupConfiguration.Mines) {
            mineData.enabled = true;
        }
    }

    public static void stop() {
        for(MineData mineData : RankupConfiguration.Mines) {
            mineData.enabled = false;
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (event.getType() != UpdateType.SEC) return;
        for(MineData mineData : RankupConfiguration.Mines) {
            if(!mineData.enabled) continue;
            mineData.currentDelay--;
            if(RankupConfiguration.AlertTimes.contains(mineData.currentDelay)) {
                if(mineData.alertArea != -1) {
                    sendMessage(UtilPlayer.getNearby(mineData.middle, mineData.alertArea), mineData, "MineResetingAlert");
                } else {
                    sendMessage(mineData, "MineResetingAlert");
                }
            }
            if(mineData.currentDelay <= 0) {
                mineData.currentDelay = mineData.delay;
                mineData.fillMine();
                if(mineData.alertArea != -1) {
                    sendMessage(UtilPlayer.getNearby(mineData.middle, mineData.alertArea), mineData, "MineResetAlert");
                } else {
                    sendMessage(mineData, "MineResetAlert");
                }
            }
        }
    }

    private static void sendMessage(MineData mineData, String key) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UserPreferences prefs = PreferencesManager.getPreferences(player);
            player.sendMessage(LanguageManager.getTranslation(prefs.getLanguage(), key)
                    .replaceAll("%MineName%", mineData.name)
                    .replaceAll("%TimeRemaining%", String.valueOf(mineData.currentDelay))
                    .replace('&', ChatColor.COLOR_CHAR));
        }
    }

    private static void sendMessage(List<Player> players, MineData mineData, String key) {
        for (Player player : players) {
            UserPreferences prefs = PreferencesManager.getPreferences(player);
            player.sendMessage(LanguageManager.getTranslation(prefs.getLanguage(), key)
                    .replaceAll("%MineName%", mineData.name)
                    .replaceAll("%TimeRemaining%", String.valueOf(mineData.currentDelay))
                    .replace('&', ChatColor.COLOR_CHAR));
        }
    }

}
