package com.ellisiumx.elrankup.essentials;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SuicideCommand extends CommandBase {

    public SuicideCommand(JavaPlugin plugin) {
        super(plugin, Rank.ALL, "suicide");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        caller.setHealth(0.0D);
    }
}