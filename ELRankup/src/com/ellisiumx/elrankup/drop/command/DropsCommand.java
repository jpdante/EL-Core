package com.ellisiumx.elrankup.drop.command;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.utils.UtilServer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class DropsCommand extends CommandBase {

    public DropsCommand(JavaPlugin plugin) {
        super(plugin, Rank.ALL, "drops", "d");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String commandLabel, String[] args) {
        ArrayList<String> list = new ArrayList<>();
        for(Player player : UtilServer.getPlayers()) {
            list.add(player.getName());
        }
        return list;
    }
}