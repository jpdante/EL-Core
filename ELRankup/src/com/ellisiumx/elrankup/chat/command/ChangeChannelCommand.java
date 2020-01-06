package com.ellisiumx.elrankup.chat.command;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.lang.LanguageDB;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elrankup.chat.ChatChannel;
import com.ellisiumx.elrankup.chat.ChatManager;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ChangeChannelCommand extends CommandBase {

    public ChangeChannelCommand(JavaPlugin plugin) {
        super(plugin, Rank.DEVELOPER, "channel", "ch");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
        for (LanguageDB languageDB : LanguageManager.getLanguages()) {
            languageDB.insertTranslation("ChatChannelCommand", "&a/channel <channel> &8- &7Change channel");
            languageDB.insertTranslation("ChatChannelNotExists", "&cThis channel does not exist!");
            languageDB.insertTranslation("ChatChannelChanged", "&aYour channel has been changed to &e%Channel%&a!");
        }
        if (LanguageManager.saveLanguages()) LanguageManager.reloadLanguages();
    }

    @Override
    public void execute(Player caller, String[] args) {
        if (args == null || args.length != 1) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ChatChannelCommand").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        ChatChannel channel = RankupConfiguration.getChatChannel(args[0]);
        if(channel == null) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ChatChannelNotExists").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        ChatManager.context.playerChats.get(caller.getName()).currentChannel = channel;
        ChatManager.regenerateTags(caller);
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ChatChannelChanged")
                .replaceAll("%Channel%", channel.key)
                .replace('&', ChatColor.COLOR_CHAR));
    }
}