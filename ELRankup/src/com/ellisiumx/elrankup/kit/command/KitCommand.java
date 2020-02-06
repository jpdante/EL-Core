package com.ellisiumx.elrankup.kit.command;

import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.crate.command.CrateCommand;
import com.ellisiumx.elrankup.crate.holder.CrateMenuHolder;
import com.ellisiumx.elrankup.kit.Kit;
import com.ellisiumx.elrankup.kit.KitManager;
import com.ellisiumx.elrankup.kit.holder.KitMenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class KitCommand extends CommandBase {

    public KitCommand(JavaPlugin plugin) {
        super(plugin, Rank.ALL, "kit");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        if(args.length == 2) {
            if(args[0].equalsIgnoreCase("delete") && CoreClientManager.get(caller).getRank().has(Rank.DEVELOPER)) {
                KitManager.context.deleteKit(caller, args[1]);
            } else if(args[0].equalsIgnoreCase("edit") && CoreClientManager.get(caller).getRank().has(Rank.DEVELOPER)) {
                KitManager.context.editKit(caller, args[1]);
            }
        } else if(args.length == 3) {
            if(args[0].equalsIgnoreCase("create") && CoreClientManager.get(caller).getRank().has(Rank.DEVELOPER)) {
                for(Kit kit : RankupConfiguration.Kits) {
                    if(kit.getName().equals(args[1])) {
                        caller.sendMessage(ChatColor.RED + "A kit with that name already exists!");
                        return;
                    }
                }
                String[] ranksRaw = args[2].split(",");
                ArrayList<Rank> ranks = new ArrayList<>();
                for (String rank : ranksRaw) {
                    ranks.add(Rank.valueOf(rank));
                }
                Inventory inventory = Bukkit.createInventory(new KitMenuHolder(args[1], ranks), 54,
                        ChatColor.GREEN + "" + ChatColor.BOLD + "Creating kit " + args[1]);
                inventory.setItem(53, CrateCommand.confirmItemStack());
                caller.openInventory(inventory);
            }
        } else {
            KitManager.context.openKits(caller);
        }
    }
}
