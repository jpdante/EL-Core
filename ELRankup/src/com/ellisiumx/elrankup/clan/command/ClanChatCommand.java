package com.ellisiumx.elrankup.clan.command;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elrankup.clan.ClanManager;
import com.ellisiumx.elrankup.clan.ClanPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ClanChatCommand extends CommandBase {

    public ClanChatCommand(JavaPlugin plugin) {
        super(plugin, Rank.ALL, "cc");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        if(args == null || args.length <= 0) return;
        StringBuilder message = new StringBuilder();
        for(String arg : args) {
            message.append(arg).append(" ");
        }
        ClanManager.context.clanChat(caller, message.toString());
    }
}
