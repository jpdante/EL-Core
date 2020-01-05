package com.ellisiumx.elrankup.chat;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.lang.LanguageDB;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elcore.timing.TimingManager;
import com.ellisiumx.elcore.utils.UtilPlayer;
import com.ellisiumx.elrankup.clan.ClanPlayer;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.economy.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.LinkedList;

public class ChatManager implements Listener {

    private static ChatManager context;
    private HashMap<String, PlayerChat> playerChats;

    public ChatManager(Plugin plugin) {
        context = this;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        for (LanguageDB languageDB : LanguageManager.getLanguages()) {
            languageDB.insertTranslation("ChatNoPlayerClose", "&cNo players are nearby to hear your message.");
            languageDB.insertTranslation("ChatNoMoneyAmount", "&cYou don't have the minimum amount of money to talk on this channel!.");
        }
        if (LanguageManager.saveLanguages()) LanguageManager.reloadLanguages();
    }

    public static void regenerateTags(Player player) {
        if (!context.playerChats.containsKey(player.getName())) return;
        PlayerChat playerChat = context.playerChats.get(player.getName());
        playerChat.formatedTags = playerChat.currentChannel.format;
    }

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        PlayerChat playerChat = playerChats.get(event.getPlayer().getName());
        if(playerChat.currentChannel.minPrice > 0) {
            if(!EconomyManager.economy.has(playerChat.player, playerChat.currentChannel.minPrice)) {
                event.setCancelled(true);
                playerChat.player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(playerChat.player).getLanguage(), "ChatNoMoneyAmount"));
                return;
            }
        }
        if (playerChat.formatedTags == null) regenerateTags(playerChat.player);
        if (playerChat.currentChannel.distance > 0) {
            event.setCancelled(true);
            LinkedList<Player> players = UtilPlayer.getNearby(playerChat.player.getLocation(), playerChat.currentChannel.distance);
            if (players.size() <= 0) {
                playerChat.player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(playerChat.player).getLanguage(), "ChatNoPlayerClose"));
                return;
            }
            for (Player player : UtilPlayer.getNearby(playerChat.player.getLocation(), playerChat.currentChannel.distance)) {
                player.sendMessage(playerChat.formatedTags + event.getMessage());
            }
            return;
        }
        event.setMessage(playerChat.formatedTags + event.getMessage());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        playerChats.put(event.getPlayer().getName(), new PlayerChat(event.getPlayer(), RankupConfiguration.defaultChatChannel));
        regenerateTags(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerChats.remove(event.getPlayer().getName());
    }

}
