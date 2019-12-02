package com.ellisiumx.elrankup.essentials;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilInv;
import com.ellisiumx.elcore.utils.UtilMessage;
import com.ellisiumx.elcore.utils.UtilPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ClearInventoryCommand extends CommandBase {

    public ClearInventoryCommand(JavaPlugin plugin) {
        super(plugin, Rank.HELPER, "clearinventory", "clearinv", "ci");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        if(args == null || args.length == 0) {
            UtilInv.Clear(caller);
            caller.sendMessage(UtilMessage.main("ClearInventory", UtilChat.cGreen + "Inventory cleared!"));
        } else if(args[0] != null && !args[0].isEmpty()) {
            Player player = UtilPlayer.searchExact(args[0]);
            if(player != null) {
                UtilInv.Clear(caller);
                caller.sendMessage(UtilMessage.main("ClearInventory", UtilChat.cGreen + args[0] + "'s Inventory cleared!"));
            } else {
                caller.sendMessage(UtilMessage.main("ClearInventory", UtilChat.cRed + "Player '" + args[0] + "' does not exist!"));
            }
        } else {
            caller.sendMessage(UtilMessage.main("ClearInventory", UtilChat.cRed + "Invalid arguments!"));
            caller.sendMessage(UtilMessage.main("ClearInventory", UtilChat.cRed + "/ci"));
            caller.sendMessage(UtilMessage.main("ClearInventory", UtilChat.cRed + "/ci <player>"));
        }
    }
}