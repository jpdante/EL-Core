package com.ellisiumx.elrankup.warp.command;

import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilMessage;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.warp.WarpManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class WarpCommand extends CommandBase {

    public WarpCommand(JavaPlugin plugin) {
        super(plugin, Rank.ALL, "warp", "w");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        if(args == null || args.length == 0) {
            WarpManager.context.openMenu(caller);
        } else if (args.length == 1) {
            WarpManager.context.warpPlayer(caller, args[0].toLowerCase());
        } else if (args.length == 2 && CoreClientManager.get(caller).getRank().has(Rank.DEVELOPER)) {
            if(args[0].equalsIgnoreCase("del")) {
                WarpManager.context.deleteWarp(caller, args[1]);
                RankupConfiguration.save();
            } else {
                ShowCommands(caller);
            }
        } else if (args.length == 3 && CoreClientManager.get(caller).getRank().has(Rank.DEVELOPER)) {
            if(args[0].equalsIgnoreCase("set")) {
                try {
                    Rank rank = Rank.valueOf(args[2]);
                    WarpManager.context.setWarp(caller, args[1], rank);
                    RankupConfiguration.save();
                } catch (Exception ex) {
                    caller.sendMessage(UtilMessage.main("Warp", UtilChat.cRed + "Invalid rank '" + args[2] + "'"));
                }
            } else {
                ShowCommands(caller);
            }
        } else {
            ShowCommands(caller);
        }
    }

    public void ShowCommands(Player caller) {
        caller.sendMessage(UtilMessage.main("Warp", UtilChat.cRed + "Invalid command!"));
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "WarpCommand").replace('&', ChatColor.COLOR_CHAR));
        if(CoreClientManager.get(caller).getRank().has(Rank.DEVELOPER)) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "WarpSetCommand").replace('&', ChatColor.COLOR_CHAR));
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "WarpDelCommand").replace('&', ChatColor.COLOR_CHAR));
        }
    }
}