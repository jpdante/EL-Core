package com.ellisiumx.elrankup.gamemode;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilMessage;
import com.ellisiumx.elcore.utils.UtilPlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GamemodeSpectatorCommand extends CommandBase {
    public GamemodeSpectatorCommand(JavaPlugin plugin) {
        super(plugin, Rank.ADMIN, "gmsp");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        if(args == null || args.length == 0) {
            caller.setGameMode(GameMode.SPECTATOR);
            caller.sendMessage(UtilMessage.main("GameMode", UtilChat.cGreen + "Set to " + GameMode.SPECTATOR.toString()));
        } else if(args[0] != null && !args[0].isEmpty()) {
            Player player = UtilPlayer.searchExact(args[0]);
            if(player != null) {
                player.setGameMode(GameMode.SPECTATOR);
                caller.sendMessage(UtilMessage.main("GameMode", UtilChat.cGreen +  args[0] + "'s Set to " + GameMode.SPECTATOR.toString()));
            } else {
                caller.sendMessage(UtilMessage.main("GameMode", UtilChat.cRed + "Player '" + args[0] + "' does not exist!"));
            }
        } else {
            caller.sendMessage(UtilMessage.main("GameMode", UtilChat.cRed + "Invalid arguments!"));
            caller.sendMessage(UtilMessage.main("GameMode", UtilChat.cRed + "/gmsp"));
            caller.sendMessage(UtilMessage.main("GameMode", UtilChat.cRed + "/gmsp <player>"));
        }
    }
}
