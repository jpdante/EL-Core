package com.ellisiumx.elrankup.kit.command;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elrankup.kit.KitManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class KitsCommand extends CommandBase {

    public KitsCommand(JavaPlugin plugin) {
        super(plugin, Rank.ALL, "kits");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        KitManager.context.openKits(caller);
    }
}
