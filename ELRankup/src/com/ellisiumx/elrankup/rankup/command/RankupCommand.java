package com.ellisiumx.elrankup.rankup.command;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elrankup.rankup.RankupManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class RankupCommand extends CommandBase {

    public RankupCommand(JavaPlugin plugin) {
        super(plugin, Rank.ALL, "rankup", "r");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        RankupManager.context.rankupPlayer(caller);
    }
}