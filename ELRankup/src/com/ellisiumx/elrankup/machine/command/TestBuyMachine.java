package com.ellisiumx.elrankup.machine.command;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.utils.UtilNBT;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.machine.MachineType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class TestBuyMachine extends CommandBase {

    public TestBuyMachine(JavaPlugin plugin) {
        super(plugin, Rank.ALL, "machine");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        Bukkit.broadcastMessage(RankupConfiguration.MachineTypes.size() + "");
        MachineType type = RankupConfiguration.MachineTypes.get(0);
        ItemStack itemStack = new ItemStack(type.getItem());
        itemStack.setAmount(1);
        itemStack = UtilNBT.set(itemStack, "2", "Machine");
        caller.getInventory().addItem(itemStack);
        caller.sendMessage("Maquina comprada");
    }
}
