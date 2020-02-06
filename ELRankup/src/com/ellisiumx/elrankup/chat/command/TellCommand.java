package com.ellisiumx.elrankup.chat.command;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.lang.LanguageDB;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elcore.utils.UtilPlayer;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.economy.EconomyManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TellCommand extends CommandBase {

    public TellCommand(JavaPlugin plugin) {
        super(plugin, Rank.ALL, "tell", "t");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
        for (LanguageDB languageDB : LanguageManager.getLanguages()) {
            languageDB.insertTranslation("ChatTellCommand", "&a/tell <player> <message> &8- &7Send private message to player");
            languageDB.insertTranslation("ChatTellPlayerNotExists", "&cPlayer &a%PlayerName% &cdoesn't exist or is not online!");
            languageDB.insertTranslation("ChatTellReceived", "&aTell &b%Sender%&a => &7%Message%");
            languageDB.insertTranslation("ChatTellSent", "&aPrivate message has been sent!");
            languageDB.insertTranslation("ChatTellErrorSamePlayer", "&cYou cannot send a private message to yourself!");
        }
        if (LanguageManager.saveLanguages()) LanguageManager.reloadLanguages();
    }

    @Override
    public void execute(Player caller, String[] args) {
        if (args == null || args.length < 2) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ChatTellCommand").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if(!EconomyManager.economy.has(caller, RankupConfiguration.MinTellPrice)) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ChatNoMoneyAmount").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        Player player = UtilPlayer.searchExact(args[0]);
        if (player == null) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ChatTellPlayerNotExists").replaceAll("%PlayerName%", args[0]).replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if(caller == player) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ChatTellErrorSamePlayer").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        StringBuilder message = new StringBuilder();
        for(int i = 1; i < args.length; i++) {
            message.append(args[i]).append(" ");
        }
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ChatTellReceived").replaceAll("%Sender%", caller.getDisplayName()).replaceAll("%Message%", message.toString()).replace('&', ChatColor.COLOR_CHAR));
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ChatTellSent").replace('&', ChatColor.COLOR_CHAR));
    }
}