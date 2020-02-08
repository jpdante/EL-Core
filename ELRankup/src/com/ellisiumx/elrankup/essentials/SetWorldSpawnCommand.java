package com.ellisiumx.elrankup.essentials;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilMessage;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SetWorldSpawnCommand extends CommandBase {

    public SetWorldSpawnCommand(JavaPlugin plugin) {
        super(plugin, Rank.DEVELOPER, "setworldspawn");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        Location loc = caller.getLocation();
        caller.getWorld().setSpawnLocation(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
        caller.sendMessage(UtilMessage.main("World Spawn", UtilChat.cGreen + "Spawn set!"));
    }
}
