package com.ellisiumx.elrankup.essentials;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilMessage;
import com.ellisiumx.elcore.utils.UtilPlayer;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SetSpawnCommand extends CommandBase {

    public SetSpawnCommand(JavaPlugin plugin) {
        super(plugin, Rank.DEVELOPER, "setspawn");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        Location loc = caller.getLocation();
        caller.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        RankupConfiguration.SpawnLocation = loc;
        RankupConfiguration.save();
        caller.sendMessage(UtilMessage.main("Spawn", UtilChat.cGreen + "Spawn set!"));
    }
}
