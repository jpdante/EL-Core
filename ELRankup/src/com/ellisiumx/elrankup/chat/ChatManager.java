package com.ellisiumx.elrankup.chat;

import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.jsonchat.HoverEvent;
import com.ellisiumx.elcore.jsonchat.JsonMessage;
import com.ellisiumx.elcore.jsonchat.JsonColor;
import com.ellisiumx.elcore.lang.LanguageDB;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elcore.utils.UtilPlayer;
import com.ellisiumx.elrankup.chat.command.ChangeChannelCommand;
import com.ellisiumx.elrankup.chat.command.TellCommand;
import com.ellisiumx.elrankup.clan.ClanManager;
import com.ellisiumx.elrankup.clan.ClanPlayer;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.economy.EconomyManager;
import com.ellisiumx.elrankup.rankup.RankLevel;
import com.ellisiumx.elrankup.rankup.RankupManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.LinkedList;

public class ChatManager implements Listener {

    public static ChatManager context;
    public HashMap<String, PlayerChat> playerChats;

    public ChatManager(JavaPlugin plugin) {
        context = this;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        playerChats = new HashMap<>();
        for (LanguageDB languageDB : LanguageManager.getLanguages()) {
            languageDB.insertTranslation("ChatNoPlayerClose", "&cNo players are nearby to hear your message.");
            languageDB.insertTranslation("ChatNoMoneyAmount", "&cYou don't have the minimum amount of money to talk on this channel!.");
            languageDB.insertTranslation("ChatChannelCommand", "&a/channel <channel> &8- &7Change channel");
            languageDB.insertTranslation("ChatChannelNotExists", "&cThis channel does not exist!");
            languageDB.insertTranslation("ChatChannelChanged", "&aYour channel has been changed to &e%Channel%&a!");
        }
        if (LanguageManager.saveLanguages()) LanguageManager.reloadLanguages();
        new ChangeChannelCommand(plugin);
        new TellCommand(plugin);
    }

    public static void regenerateTags(Player player) {
        if (!context.playerChats.containsKey(player.getName())) return;
        PlayerChat playerChat = context.playerChats.get(player.getName());
        playerChat.formatedTags = playerChat.currentChannel.format.replaceAll("%PlayerName%", player.getDisplayName());

        Rank group = CoreClientManager.get(player).getRank();
        if(group != Rank.ALL) playerChat.formatedTags = playerChat.formatedTags.replaceAll("%Group%", group.getColor() + "[" + group.getTag(false, false) + group.getColor() + "] ");
        else playerChat.formatedTags = playerChat.formatedTags.replaceAll("%Group%","");

        RankLevel rank = RankupManager.get(player);
        if(rank != null) playerChat.formatedTags = playerChat.formatedTags.replaceAll("%Rank%", rank.color + "[" + rank.tag + rank.color + "] ");
        else playerChat.formatedTags = playerChat.formatedTags.replaceAll("%Rank%", "");

        ClanPlayer clanPlayer = ClanManager.getClanPlayer(player);
        if(clanPlayer != null && clanPlayer.clan != null) playerChat.formatedTags = playerChat.formatedTags.replaceAll("%ClanTag%", clanPlayer.clan.colorTag + " ");
        else playerChat.formatedTags = playerChat.formatedTags.replaceAll("%ClanTag%", "");

        playerChat.formatedTags = playerChat.formatedTags.replace('&', ChatColor.COLOR_CHAR);
    }

    @EventHandler
    public void onChatMessage(AsyncPlayerChatEvent event) {
        //new JsonMessage("Hello fella fur").color(JsonColor.AQUA).hover(HoverEvent.SHOW_TEXT, "Teste").send(JsonMessage.MessageType.CHAT_BOX, event.getPlayer());
        PlayerChat playerChat = playerChats.get(event.getPlayer().getName());
        if(playerChat.currentChannel.minPrice > 0) {
            if(!EconomyManager.economy.has(playerChat.player, playerChat.currentChannel.minPrice)) {
                event.setCancelled(true);
                playerChat.player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(playerChat.player).getLanguage(), "ChatNoMoneyAmount").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
        }
        if (playerChat.formatedTags == null) regenerateTags(playerChat.player);
        if (playerChat.currentChannel.distance > 0) {
            event.setCancelled(true);
            LinkedList<Player> players = UtilPlayer.getNearby(playerChat.player.getLocation(), playerChat.currentChannel.distance);
            if (players.size() <= 1) {
                playerChat.player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(playerChat.player).getLanguage(), "ChatNoPlayerClose").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            for (Player player : UtilPlayer.getNearby(playerChat.player.getLocation(), playerChat.currentChannel.distance)) {
                player.sendMessage(playerChat.formatedTags + event.getMessage());
            }
            return;
        }
        event.setFormat(playerChat.formatedTags + event.getMessage());
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
