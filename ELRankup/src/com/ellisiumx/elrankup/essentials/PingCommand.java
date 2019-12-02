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

public class PingCommand extends CommandBase {

    public PingCommand(JavaPlugin plugin) {
        super(plugin, Rank.ALL, "ping");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        if(args == null || args.length == 0) {
            int ping = ((CraftPlayer) caller).getHandle().ping;
            caller.sendMessage(UtilMessage.main("Ping", UtilChat.cAqua + "Pong! Ping: " + getPingColor(ping)));
        } else if(args[0] != null && !args[0].isEmpty()) {
            Player player = UtilPlayer.searchExact(args[0]);
            if(player != null) {
                int ping = ((CraftPlayer) player).getHandle().ping;
                caller.sendMessage(UtilMessage.main("Ping", UtilChat.cAqua + args[0] + "'s Ping: " + getPingColor(ping)));
            } else {
                caller.sendMessage(UtilMessage.main("Ping", UtilChat.cRed + "Player '" + args[0] + "' does not exist!"));
            }
        } else {
            caller.sendMessage(UtilMessage.main("Ping", UtilChat.cRed + "Invalid arguments!"));
            caller.sendMessage(UtilMessage.main("Ping", UtilChat.cRed + "/ping"));
            caller.sendMessage(UtilMessage.main("Ping", UtilChat.cRed + "/ping <player>"));
        }
    }

    private static String getPingColor(int ping) {
        if(ping <= 65) return UtilChat.cGreen + ping;
        else if(ping <= 100) return UtilChat.cYellow + ping;
        else return UtilChat.cRed + ping;
    }
}
