package com.ellisiumx.elrankup.kit.command;

import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilMessage;
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
import org.bukkit.inventory.ItemStack;
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
        if(args == null || args.length == 0) {
            KitManager.context.openKits(caller);
        } else if(args.length == 1) {
            KitManager.context.openKit(caller, args[0]);
        } else if(args.length == 2 && CoreClientManager.get(caller).getRank().has(Rank.DEVELOPER)) {
            if(args[0].equalsIgnoreCase("delete") && CoreClientManager.get(caller).getRank().has(Rank.DEVELOPER)) {
                KitManager.context.deleteKit(caller, args[1]);
            } else if(args[0].equalsIgnoreCase("edit") && CoreClientManager.get(caller).getRank().has(Rank.DEVELOPER)) {
                if(!RankupConfiguration.Kits.containsKey(args[1])) {
                    caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "KitDontExists")
                            .replaceAll("%KitName%", args[1])
                            .replace('&', ChatColor.COLOR_CHAR));
                    return;
                }
                Kit kit = RankupConfiguration.Kits.get(args[1]);
                Inventory inventory = Bukkit.createInventory(new KitMenuHolder(args[1], true), 54, ChatColor.GREEN + "" + ChatColor.BOLD + "Editing kit " + args[1]);
                inventory.setItem(53, CrateCommand.confirmItemStack());
                for(ItemStack item : kit.getItems()) {
                    inventory.addItem(item);
                }
                caller.openInventory(inventory);
            }
        } else if(args.length == 4 && CoreClientManager.get(caller).getRank().has(Rank.DEVELOPER)) {
            if(args[0].equalsIgnoreCase("create") && CoreClientManager.get(caller).getRank().has(Rank.DEVELOPER)) {
                if(RankupConfiguration.Kits.containsKey(args[1])) {
                    caller.sendMessage(ChatColor.RED + "A kit with that name already exists!");
                    return;
                }
                try {
                    Rank rank = Rank.valueOf(args[2].toUpperCase());
                    int delay = Integer.parseInt(args[3]);
                    Inventory inventory = Bukkit.createInventory(new KitMenuHolder(args[1], rank, delay), 54, ChatColor.GREEN + "" + ChatColor.BOLD + "Creating kit " + args[1]);
                    inventory.setItem(53, CrateCommand.confirmItemStack());
                    caller.openInventory(inventory);
                } catch (Exception ex) {
                    caller.sendMessage(UtilMessage.main("Kits", UtilChat.cGold + "/kit create " + args[1] + " <rank> <delay>"));
                }
            }
        }
    }
}
