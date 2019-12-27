package com.ellisiumx.elrankup.crate.command;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CrateCommand extends CommandBase {

    public CrateCommand(JavaPlugin plugin) {
        super(plugin, Rank.DEVELOPER, "crate");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {

    }

}
