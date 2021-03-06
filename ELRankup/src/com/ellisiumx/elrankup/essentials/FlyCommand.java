package com.ellisiumx.elrankup.essentials;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilMessage;
import com.ellisiumx.elcore.utils.UtilPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class FlyCommand extends CommandBase {

    public FlyCommand(JavaPlugin plugin) {
        super(plugin, Rank.HELPER, "fly");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        if(args == null || args.length == 0) {
            caller.setFallDistance(0f);
            caller.setAllowFlight(!caller.getAllowFlight());
            caller.setFlying(caller.getAllowFlight());
            if(caller.getAllowFlight()) caller.sendMessage(UtilMessage.main("Fly", UtilChat.cGreen + "Flight enabled!"));
            else caller.sendMessage(UtilMessage.main("Fly", UtilChat.cRed + "Flight disabled!"));
        } else if(args[0] != null && !args[0].isEmpty()) {
            Player player = UtilPlayer.searchExact(args[0]);
            if(player != null) {
                player.setFallDistance(0f);
                player.setAllowFlight(!player.getAllowFlight());
                player.setFlying(player.getAllowFlight());
                if(player.getAllowFlight()) caller.sendMessage(UtilMessage.main("Fly", UtilChat.cGreen + args[0] + "'s Flight enabled!"));
                else caller.sendMessage(UtilMessage.main("Fly", UtilChat.cRed + args[0] + "'s Flight disabled!"));
            } else {
                caller.sendMessage(UtilMessage.main("Fly", UtilChat.cRed + "Player '" + args[0] + "' does not exist!"));
            }
        } else {
            caller.sendMessage(UtilMessage.main("Fly", UtilChat.cRed + "Invalid arguments!"));
            caller.sendMessage(UtilMessage.main("Fly", UtilChat.cRed + "/fly"));
            caller.sendMessage(UtilMessage.main("Fly", UtilChat.cRed + "/fly <player>"));
        }
    }
}