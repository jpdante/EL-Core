package com.ellisiumx.elrankup.economy.command;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilMessage;
import com.ellisiumx.elrankup.economy.EconomyManager;
import org.bukkit.Bukkit;
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
                if(balance != -1) caller.sendMessage(UtilMessage.main("Economy", UtilChat.cGreen + "$ " + balance));
                else caller.sendMessage(UtilMessage.main("Economy", UtilChat.cRed + "Failed to get your balance right now, please try later."));
            } else if(args.length == 1) {
                double balance = EconomyManager.economy.getBalance(args[0]);
                if(balance != -1) caller.sendMessage(UtilMessage.main("Economy", UtilChat.cGreen + args[0] + ": $ " + balance));
                else caller.sendMessage(UtilMessage.main("Economy", UtilChat.cRed + "Player '" + args[0] + "' does not exist!"));
            } else {
                caller.sendMessage(UtilMessage.main("Economy", UtilChat.cRed + "Invalid arguments!"));
                caller.sendMessage(UtilMessage.main("Economy", UtilChat.cRed + "/money <player>"));
            }
        });
    }
}
