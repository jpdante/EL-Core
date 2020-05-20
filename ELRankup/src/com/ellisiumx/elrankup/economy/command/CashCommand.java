package com.ellisiumx.elrankup.economy.command;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.account.CoreClient;
import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elrankup.economy.EconomyManager;
import com.ellisiumx.elrankup.utils.UtilFormat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CashCommand extends CommandBase  {

    public CashCommand(JavaPlugin plugin) {
        super(plugin, Rank.ALL, "cash");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            if(args == null || args.length == 0) {
                double cash = EconomyManager.casheco.getCash(caller);
                caller.sendMessage(
                LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "GetCash")
                        .replace("%Cash%", String.valueOf(cash))
                        .replace('&', ChatColor.COLOR_CHAR)
                );
            }/*else if(args != null || args.length > 0 ) {
                CoreClient client = CoreClientManager.get(caller);
                if(client.getRank().has(Rank.DEVELOPER)){
                    if(args[0].equals("give")){
                        
                    }
                }
            } */
        });
    }
}
