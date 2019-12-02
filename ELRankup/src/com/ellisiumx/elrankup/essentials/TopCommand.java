package com.ellisiumx.elrankup.essentials;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class TopCommand extends CommandBase {

    public TopCommand(JavaPlugin plugin) {
        super(plugin, Rank.DEVELOPER, "top");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            double blockY = caller.getWorld().getHighestBlockYAt(caller.getLocation().getBlockX(), caller.getLocation().getBlockZ());
            if (blockY <= 0.0d) {
                blockY = 256.0d;
            }
            caller.teleport(new Location(caller.getWorld(), caller.getLocation().getX(), blockY, caller.getLocation().getZ()));
        });
    }
}