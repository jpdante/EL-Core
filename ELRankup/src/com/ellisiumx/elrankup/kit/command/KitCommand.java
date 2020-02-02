package com.ellisiumx.elrankup.kit.command;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elrankup.kit.Kit;
import com.ellisiumx.elrankup.kit.KitManager;
import org.bukkit.ChatColor;
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
        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("delete")) {
                KitManager.context.deleteKit(caller, args[1]);
            } else if(args[0].equalsIgnoreCase("edit")) {
                KitManager.context.editKit(caller, args[1]);
            }
        } else if(args.length == 3) {
            if(args[0].equalsIgnoreCase("create")) {
                KitManager.context.createKit(caller, args[1], args[2]);
            }
        } else {
            KitManager.context.openKits(caller);
        }
    }

}
