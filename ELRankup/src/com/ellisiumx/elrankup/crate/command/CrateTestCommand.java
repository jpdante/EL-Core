package com.ellisiumx.elrankup.crate.command;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.utils.UtilNBT;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.crate.CrateManager;
import com.ellisiumx.elrankup.crate.CrateType;
import com.ellisiumx.elrankup.crate.holder.CrateMenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class CrateTestCommand extends CommandBase {

    public CrateTestCommand(JavaPlugin plugin) {
        super(plugin, Rank.DEVELOPER, "cratetest");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        caller.getInventory().addItem(CrateManager.getCrateKey(RankupConfiguration.CrateTypes.get(0)));
    }
}
