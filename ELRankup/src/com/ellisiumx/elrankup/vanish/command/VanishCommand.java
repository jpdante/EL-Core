package com.ellisiumx.elrankup.vanish.command;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilMessage;
import com.ellisiumx.elcore.utils.UtilPlayer;
import com.ellisiumx.elrankup.vanish.VanishManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class VanishCommand extends CommandBase {

    public VanishCommand(JavaPlugin plugin) {
        super(plugin, Rank.HELPER, "vanish");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        if(args == null || args.length == 0) {
            if(VanishManager.contains(caller)) VanishManager.remove(caller);
            else VanishManager.add(caller);
            caller.sendMessage(UtilMessage.main("Vanish", UtilChat.cGreen + "Vanished!"));
        } else if(args[0] != null && !args[0].isEmpty()) {
            Player player = UtilPlayer.searchExact(args[0]);
            if(player != null) {
                if(VanishManager.contains(player)) VanishManager.remove(player);
                else VanishManager.add(player);
                caller.sendMessage(UtilMessage.main("Vanish", UtilChat.cGreen + args[0] + " vanished!"));
            } else {
                caller.sendMessage(UtilMessage.main("Vanish", UtilChat.cRed + "Player '" + args[0] + "' does not exist!"));
            }
        } else {
            caller.sendMessage(UtilMessage.main("Vanish", UtilChat.cRed + "Invalid arguments!"));
            caller.sendMessage(UtilMessage.main("Vanish", UtilChat.cRed + "/vanish"));
            caller.sendMessage(UtilMessage.main("Vanish", UtilChat.cRed + "/vanish <player>"));
        }
    }
}
