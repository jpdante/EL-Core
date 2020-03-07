package com.ellisiumx.elrankup.chat.command;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elrankup.chat.ChatChannel;
import com.ellisiumx.elrankup.chat.ChatManager;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SetLocalCommand extends CommandBase {

    public SetLocalCommand(JavaPlugin plugin) {
        super(plugin, Rank.ALL, "local", "l");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        ChatChannel channel = RankupConfiguration.getChatChannel("l");
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