package com.ellisiumx.elrankup.essentials;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ClearChatCommand extends CommandBase {

    public ClearChatCommand(JavaPlugin plugin) {
        super(plugin, Rank.ALL, "clearchat", "cc");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        for(int i = 0; i < 20; i++) {
            caller.sendMessage("");
        }
    }
}