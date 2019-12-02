package com.ellisiumx.elrankup.gamemode;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.command.MultiCommandBase;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilMessage;
import com.ellisiumx.elcore.utils.UtilPlayer;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GamemodeCommand extends CommandBase {
    public GamemodeCommand(JavaPlugin plugin) {
        super(plugin, Rank.ADMIN, "gm", "gamemode");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        if(args != null && args.length == 1 && args[0] != null && !args[0].isEmpty()) {
            GameMode gameMode = getGamemMode(args[0]);
            caller.setGameMode(gameMode);
            caller.sendMessage(UtilMessage.main("GameMode", UtilChat.cGreen + "Set to " + gameMode.toString()));
        } else if(args != null && args.length == 2 && args[0] != null && !args[0].isEmpty() && args[1] != null && !args[1].isEmpty()) {
            Player player = UtilPlayer.searchExact(args[0]);
            if(player != null) {
                GameMode gameMode = getGamemMode(args[1]);
                player.setGameMode(gameMode);
                caller.sendMessage(UtilMessage.main("GameMode", UtilChat.cGreen +  args[0] + "'s Set to " + gameMode.toString()));
            } else {
                caller.sendMessage(UtilMessage.main("GameMode", UtilChat.cRed + "Player '" + args[0] + "' does not exist!"));
            }
        } else {
            caller.sendMessage(UtilMessage.main("GameMode", UtilChat.cRed + "Invalid arguments!"));
            caller.sendMessage(UtilMessage.main("GameMode", UtilChat.cRed + "/gm <GameMode>"));
            caller.sendMessage(UtilMessage.main("GameMode", UtilChat.cRed + "/gm <player> <GameMode>"));
        }
    }

    public GameMode getGamemMode(String args) {
        if(args.startsWith("s")) return GameMode.SURVIVAL;
        if(args.startsWith("c")) return GameMode.CREATIVE;
        if(args.startsWith("a")) return GameMode.ADVENTURE;
        if(args.startsWith("sp")) return GameMode.SPECTATOR;
        if(args.equalsIgnoreCase("0")) return GameMode.SURVIVAL;
        if(args.equalsIgnoreCase("1")) return GameMode.CREATIVE;
        if(args.equalsIgnoreCase("2")) return GameMode.ADVENTURE;
        if(args.equalsIgnoreCase("3")) return GameMode.SPECTATOR;
        return GameMode.SURVIVAL;
    }
}
