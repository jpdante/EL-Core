package com.ellisiumx.elrankup.warp;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.account.CoreClient;
import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.lang.LanguageDB;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilConvert;
import com.ellisiumx.elcore.utils.UtilMessage;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.warp.command.WarpCommand;
import com.ellisiumx.elrankup.warp.command.WarpsCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;

public class WarpManager implements Listener {

    public static WarpManager context;
    public final HashMap<String, PlayerWarp> playerWarps;

    public WarpManager(JavaPlugin plugin) {
        context = this;
        playerWarps = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        for (LanguageDB languageDB : LanguageManager.getLanguages()) {
            // Errors
            languageDB.insertTranslation("AlreadyWarping", "&f[&3Warp&f] &cYou are already teleporting, please wait!");
            languageDB.insertTranslation("WarpingCancelled", "&f[&3Warp&f] &cTeleport canceled, you moved!");
            languageDB.insertTranslation("WarpDontExist", "&f[&3Warp&f] &cThis warp does not exist!");
            languageDB.insertTranslation("WarpDontHasRank", "&f[&3Warp&f] &cYou don't have enough rank (%rank%) to teleport!");
            // Messages
            languageDB.insertTranslation("Warping", "&f[&3Warp&f] &aTeleporting in %delay% seconds");
            languageDB.insertTranslation("WarpCommand", " &6/warp <warp name> - Teleport to the specified warp");
            languageDB.insertTranslation("WarpSetCommand", " &6/warp set <warp name> <rank> - Create new warp or set");
            languageDB.insertTranslation("WarpDelCommand", " &6/warp del <warp name> - Delete warp");
            languageDB.insertTranslation("WarpsCommand", " &6/warps - Show all available warps");
            languageDB.insertTranslation("WarpsMessage", "&6Warps: &f%warps%");
        }
        if (LanguageManager.saveLanguages()) LanguageManager.reloadLanguages();
        new WarpCommand(plugin);
        new WarpsCommand(plugin);
    }

    public void warpPlayer(Player player, String warp) {
        if (playerWarps.containsKey(player.getName())) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "AlreadyWarping").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if (!RankupConfiguration.Warps.containsKey(warp)) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "WarpDontExist").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        Warp warpLocation = RankupConfiguration.Warps.get(warp);
        Rank playerRank = CoreClientManager.get(player).getRank();
        if (!playerRank.has(warpLocation.getRank())) {
            player.sendMessage(
                    LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "WarpDontHasRank")
                            .replaceAll("%rank%", warpLocation.getRank().getTag(true, true))
                            .replace('&', ChatColor.COLOR_CHAR)
            );
            return;
        }
        int delay = 0;
        if(RankupConfiguration.WarpDelay.containsKey(playerRank)) delay = RankupConfiguration.WarpDelay.get(playerRank);
        playerWarps.put(player.getName(), new PlayerWarp(player, player.getLocation(), warpLocation.getLocation(), delay));
        player.sendMessage(
                LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "Warping")
                        .replaceAll("%delay%", String.valueOf(delay))
                        .replace('&', ChatColor.COLOR_CHAR)
        );
    }

    public void setWarp(Player player, String warp, Rank rank) {
        RankupConfiguration.Warps.remove(warp);
        RankupConfiguration.Warps.put(warp, new Warp(player.getLocation(), rank));
        RankupConfiguration.save();
        player.sendMessage(UtilMessage.main("Warp", UtilChat.cGreen + "Warp '" + warp + "' set!"));
    }

    public void deleteWarp(Player player, String warp) {
        RankupConfiguration.Warps.remove(warp);
        RankupConfiguration.save();
        player.sendMessage(UtilMessage.main("Warp", UtilChat.cGreen + "Warp '" + warp + "' deleted!"));
    }

    @EventHandler
    public void onTimerElapsed(UpdateEvent event) {
        if (event.getType() == UpdateType.SEC) {
            synchronized(playerWarps) {
                for (String player : playerWarps.keySet()) {
                    PlayerWarp playerWarp = playerWarps.get(player);
                    playerWarp.setDelay(playerWarp.getDelay());
                    if (playerWarp.getDelay() <= 0) {
                        playerWarps.remove(player);
                        Location l1 = playerWarp.getPlayer().getLocation();
                        Location l2 = playerWarp.getFrom();
                        if (l1.getBlockX() != l2.getBlockX() || l1.getBlockY() != l2.getBlockY() || l1.getBlockZ() != l2.getBlockZ()) {
                            playerWarp.getPlayer().sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "WarpingCancelled").replace('&', ChatColor.COLOR_CHAR));
                            return;
                        }
                        playerWarp.getPlayer().teleport(playerWarp.getTo());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerWarps.remove(event.getPlayer().getName());
    }
}
