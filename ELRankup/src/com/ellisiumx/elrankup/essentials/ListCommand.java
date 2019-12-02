package com.ellisiumx.elrankup.essentials;

import com.ellisiumx.elcore.account.CoreClient;
import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilMessage;
import com.ellisiumx.elcore.utils.UtilPlayer;
import com.ellisiumx.elcore.utils.UtilServer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class ListCommand extends CommandBase {

    public ListCommand(JavaPlugin plugin) {
        super(plugin, Rank.DEVELOPER, "list");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        caller.sendMessage(UtilMessage.main("List", UtilChat.cGold + "Players currently online: " + UtilChat.cGray +CoreClientManager.getPlayerCountIncludingConnecting()));
        for(Rank rank : Rank.values()) {
            caller.sendMessage(getOnlineFromRank(rank));
        }
    }

    public String getOnlineFromRank(Rank rank) {
        List<Player> playerList = new ArrayList<>();
        for(Player player : UtilServer.getPlayers()) {
            CoreClient client = CoreClientManager.get(player);
            if(client.getRank() == rank) playerList.add(player);
        }
        StringBuilder response = null;
        if(rank != Rank.ALL) response = new StringBuilder(rank.getColor() + rank.Name + UtilChat.cGray + "(" + UtilChat.cWhite + playerList.size() + UtilChat.cGray + "): " + UtilChat.cGray);
        else response = new StringBuilder(rank.getColor() + "ALL" + UtilChat.cGray + "(" + UtilChat.cWhite + playerList.size() + UtilChat.cGray + "): " + UtilChat.cGray);
        for(Player player : playerList) {
            response.append(player.getName()).append(", ");
        }
        return response.toString();
    }
}