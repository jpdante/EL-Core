package com.ellisiumx.elrankup.kit.command;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class KitCommand extends CommandBase {

    public KitCommand(JavaPlugin plugin) {
        super(plugin, Rank.ALL, "kit");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {

    }

}
