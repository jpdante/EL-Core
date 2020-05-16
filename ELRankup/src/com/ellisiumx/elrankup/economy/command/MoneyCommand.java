package com.ellisiumx.elrankup.economy.command;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilMessage;
import com.ellisiumx.elrankup.economy.EconomyManager;
import com.ellisiumx.elrankup.utils.UtilFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MoneyCommand extends CommandBase {

    public MoneyCommand(JavaPlugin plugin) {
        super(plugin, Rank.ALL, "money");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            if(args == null || args.length == 0) {
                double balance = EconomyManager.economy.getBalance(caller);
                if(balance != -1) caller.sendMessage(
                        LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "GetMoney")
                                .replace("%Balance%", String.valueOf(balance))
                                .replace("%FormattedBalance%", String.valueOf(UtilFormat.FormatMoney(balance)))
                                .replace('&', ChatColor.COLOR_CHAR)
                );
                else caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "GetMoneyError").replace('&', ChatColor.COLOR_CHAR));
            } else if(args.length == 1) {
                double balance = EconomyManager.economy.getBalance(args[0]);
                if(balance != -1) caller.sendMessage(
                        LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "GetMoney")
                                .replace("%Player%", args[0])
                                .replace("%Balance%", String.valueOf(balance))
                                .replace("%FormattedBalance%", String.valueOf(UtilFormat.FormatMoney(balance)))
                                .replace('&', ChatColor.COLOR_CHAR)
                );
                else caller.sendMessage(
                        LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "GetMoneyPlayerNotExists")
                                .replace("%Player%", args[0])
                                .replace('&', ChatColor.COLOR_CHAR)
                );
            } else {
                caller.sendMessage(
                        LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "GetMoneyInvalidArgs")
                                .replace("\n", System.lineSeparator())
                                .replace('&', ChatColor.COLOR_CHAR)
                );
            }
        });
    }
}
