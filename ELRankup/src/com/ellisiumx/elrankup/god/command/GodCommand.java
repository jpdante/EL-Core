package com.ellisiumx.elrankup.god.command;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilMessage;
import com.ellisiumx.elcore.utils.UtilPlayer;
import com.ellisiumx.elrankup.god.GodManager;
import com.ellisiumx.elrankup.vanish.VanishManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GodCommand extends CommandBase {

    public GodCommand(JavaPlugin plugin) {
        super(plugin, Rank.HELPER, "god");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        if(args == null || args.length == 0) {
            if(GodManager.contains(caller)) {
                GodManager.remove(caller);
                caller.sendMessage(UtilMessage.main("God", UtilChat.cRed + "God mode disabled!"));
            } else {
                GodManager.add(caller);
                caller.sendMessage(UtilMessage.main("God", UtilChat.cGreen + "God mode enabled!"));
            }
        } else if(args[0] != null && !args[0].isEmpty()) {
            Player player = UtilPlayer.searchExact(args[0]);
            if(player != null) {
                if(GodManager.contains(player)) {
                    GodManager.remove(player);
                    caller.sendMessage(UtilMessage.main("God", UtilChat.cRed + "God mode disabled!"));
                } else {
                    GodManager.add(player);
                    caller.sendMessage(UtilMessage.main("God", UtilChat.cGreen + "God mode enabled!"));
                }
            } else {
                caller.sendMessage(UtilMessage.main("God", UtilChat.cRed + "Player '" + args[0] + "' does not exist!"));
            }
        } else {
            caller.sendMessage(UtilMessage.main("God", UtilChat.cRed + "Invalid arguments!"));
            caller.sendMessage(UtilMessage.main("God", UtilChat.cRed + "/god"));
            caller.sendMessage(UtilMessage.main("God", UtilChat.cRed + "/god <player>"));
        }
    }
}
