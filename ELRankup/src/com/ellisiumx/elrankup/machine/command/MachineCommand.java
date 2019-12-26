package com.ellisiumx.elrankup.machine.command;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elrankup.machine.MachineManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MachineCommand extends CommandBase {

    public MachineCommand(JavaPlugin plugin) {
        super(plugin, Rank.ALL, "machine", "m");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        if(!MachineManager.getContext().initialized) return;
        MachineManager.getContext().openMenu(caller, "main", null);
    }
}
