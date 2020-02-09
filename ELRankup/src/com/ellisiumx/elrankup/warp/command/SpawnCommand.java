package com.ellisiumx.elrankup.warp.command;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.warp.WarpManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SpawnCommand extends CommandBase {

    public SpawnCommand(JavaPlugin plugin) {
        super(plugin, Rank.ALL, "spawn", "s");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        WarpManager.context.warpPlayer(caller, RankupConfiguration.SpawnLocation);
    }
}