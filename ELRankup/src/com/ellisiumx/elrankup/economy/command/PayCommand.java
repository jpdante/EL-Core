package com.ellisiumx.elrankup.economy.command;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilMessage;
import com.ellisiumx.elrankup.economy.EconomyManager;
import net.minecraft.server.v1_8_R3.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PayCommand extends CommandBase {

    public PayCommand(JavaPlugin plugin) {
        super(plugin, Rank.ALL, "pay");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            if(args != null && args.length == 2 && args[0] != null && !args[0].isEmpty() && args[1] != null && !args[1].isEmpty()) {
                if(caller.getName().equalsIgnoreCase(args[0])) {
                    caller.sendMessage(UtilMessage.main("Economy", UtilChat.cRed + "You cannot transfer to yourself!"));
                    return;
                }
                try {
                    double amount = Double.parseDouble(args[1]);
                    Tuple<Boolean, Boolean> res = EconomyManager.repository.payAmount(caller.getUniqueId().toString(), args[0], amount);
                    if(res.a()) {
                        if(res.b()) {
                            caller.sendMessage(UtilMessage.main("Economy", UtilChat.cGreen + "Transfer completed successfully!"));
                        } else {
                            caller.sendMessage(UtilMessage.main("Economy", UtilChat.cRed + "You do not have the balance required to make this transfer!"));
                        }
                    } else {
                        caller.sendMessage(UtilMessage.main("Economy", UtilChat.cRed + "Player '" + args[0] + "' does not exist!"));
                    }
                } catch (Exception ex) {
                    caller.sendMessage(UtilMessage.main("Economy", UtilChat.cRed + "Failed to perform transfer, please try again later."));
                }
            } else {
                caller.sendMessage(UtilMessage.main("Economy", UtilChat.cRed + "Invalid arguments!"));
                caller.sendMessage(UtilMessage.main("Economy", UtilChat.cRed + "/pay <player> <amount>"));
            }
        });
    }
}