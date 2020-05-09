package com.ellisiumx.elrankup.warp.command;

import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.warp.Warp;
import com.ellisiumx.elrankup.warp.WarpManager;
import com.ellisiumx.elrankup.warp.holder.WarpMenuHolder;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public class WarpsCommand extends CommandBase {

    public WarpsCommand(JavaPlugin plugin) {
        super(plugin, Rank.ALL, "warps");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        if (args != null) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "WarpsCommand").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        WarpManager.context.openMenu(caller);
        /*StringBuilder message = new StringBuilder();
        Rank playerRank = CoreClientManager.get(caller).getRank();
        for(Map.Entry<String, Warp> warp : RankupConfiguration.Warps.entrySet()) {
            if(playerRank.has(warp.getValue().getRank())) {
                message.append(warp.getValue().getName()).append(", ");
            }
        }
        caller.sendMessage(
                LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "WarpsMessage")
                        .replaceAll("%warps%", message.toString())
                        .replace('&', ChatColor.COLOR_CHAR)
        );*/
    }
}