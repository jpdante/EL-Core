package com.ellisiumx.elrankup.essentials;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilMessage;
import com.ellisiumx.elcore.utils.UtilPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ResetCommand extends CommandBase {

    public ResetCommand(JavaPlugin plugin) {
        super(plugin, Rank.HELPER, "reset");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        if(args == null || args.length == 0) {
            caller.setHealth(caller.getMaxHealth());
            caller.setFoodLevel(20);
            caller.sendMessage(UtilMessage.main("Reset", UtilChat.cGreen + "Stats reset!"));
        } else if(args[0] != null && !args[0].isEmpty()) {
            Player player = UtilPlayer.searchExact(args[0]);
            if(player != null) {
                player.setHealth(caller.getMaxHealth());
                player.setFoodLevel(20);
                caller.sendMessage(UtilMessage.main("Reset", UtilChat.cGreen + args[0] + "'s stats reset!"));
            } else {
                caller.sendMessage(UtilMessage.main("Reset", UtilChat.cRed + "Player '" + args[0] + "' does not exist!"));
            }
        } else {
            caller.sendMessage(UtilMessage.main("Reset", UtilChat.cRed + "Invalid arguments!"));
            caller.sendMessage(UtilMessage.main("Reset", UtilChat.cRed + "/reset"));
            caller.sendMessage(UtilMessage.main("Reset", UtilChat.cRed + "/reset <player>"));
        }
    }
}
