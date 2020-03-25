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

public class CrateCommand extends CommandBase {

    public CrateCommand(JavaPlugin plugin) {
        super(plugin, Rank.DEVELOPER, "crate");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        if (args == null || args.length == 0) showCommands(caller);
        else {
            if (args[0].equalsIgnoreCase("create")) {
                if (args.length < 3) return;
                CrateType crateType = RankupConfiguration.getCrateTypeByName(args[1]);
                if(crateType != null) {
                    caller.sendMessage(ChatColor.RED + "A crate with this key already exists!");
                    return;
                }
                StringBuilder name = new StringBuilder();
                for(int i = 2; i < args.length; i++) {
                    name.append(args[i]);
                }
                Inventory inventory = Bukkit.createInventory(new CrateMenuHolder(args[1], name.toString(), CrateMenuHolder.CrateMenuType.CreateMenu), 54,
                        ChatColor.GREEN + "" + ChatColor.BOLD + "Creating " + name.toString().replace('&', ChatColor.COLOR_CHAR));
                inventory.setItem(53, confirmItemStack());
                caller.openInventory(inventory);
            } else if (args[0].equalsIgnoreCase("edit")) {
                if (args.length != 2) return;
                CrateType crateType = RankupConfiguration.getCrateTypeByName(args[1]);
                if(crateType == null) {
                    caller.sendMessage(ChatColor.RED + "Unknown key '" + args[1] + "'");
                    return;
                }
                Inventory inventory = Bukkit.createInventory(new CrateMenuHolder(args[1], CrateMenuHolder.CrateMenuType.EditMenu), 54,
                        ChatColor.GREEN + "" + ChatColor.BOLD + "Editing " + crateType.name.replace('&', ChatColor.COLOR_CHAR));
                for(int i = 0; i < crateType.items.size(); i++) {
                    inventory.setItem(i, crateType.items.get(i));
                }
                inventory.setItem(53, confirmItemStack());
                caller.openInventory(inventory);
            } else if (args[0].equalsIgnoreCase("list")) {
                caller.sendMessage(ChatColor.GREEN + "=-=-=- Crate List -=-=-=");
                for (CrateType crateType : RankupConfiguration.CrateTypes) {
                    caller.sendMessage(ChatColor.GOLD + crateType.key + ": " + crateType.name);
                }
            } else if (args[0].equalsIgnoreCase("delete")) {
                if (args.length != 2) return;
                CrateType crateType = RankupConfiguration.getCrateTypeByName(args[1]);
                if(crateType == null) {
                    caller.sendMessage(ChatColor.RED + "Unknown key '" + args[1] + "'");
                    return;
                }
                RankupConfiguration.CrateTypes.remove(crateType);
                RankupConfiguration.save();
                caller.sendMessage(ChatColor.GREEN + "Crate deleted successfully!");
            } else if (args[0].equalsIgnoreCase("addchest")) {
                Block block = caller.getTargetBlock((Set<Material>) null, 5);
                if(block.getType() == Material.CHEST) {
                    if(CrateManager.context.chests.contains(block.getLocation().hashCode()) || RankupConfiguration.CrateChestLocations.contains(block.getLocation())) {
                        caller.sendMessage(ChatColor.RED + "This chest already exist!");
                        return;
                    }
                    CrateManager.context.chests.add(block.getLocation().hashCode());
                    RankupConfiguration.CrateChestLocations.add(block.getLocation());
                    RankupConfiguration.save();
                    caller.sendMessage(ChatColor.GREEN + "Chest created successfully!");
                } else caller.sendMessage(ChatColor.RED + "The block must be a chest!");
            } else if (args[0].equalsIgnoreCase("delchest")) {
                Block block = caller.getTargetBlock((Set<Material>) null, 5);
                if(block.getType() == Material.CHEST) {
                    CrateManager.context.chests.remove(block.getLocation().hashCode());
                    RankupConfiguration.CrateChestLocations.remove(block.getLocation());
                    RankupConfiguration.save();
                    caller.sendMessage(ChatColor.GREEN + "Chest deleted successfully!");
                } else caller.sendMessage(ChatColor.RED + "The block must be a chest!");
            } else if (args[0].equalsIgnoreCase("give")) {
                if (args.length < 2) return;
                for (CrateType crateType : RankupConfiguration.CrateTypes) {
                    if(crateType.name.equalsIgnoreCase(args[1])) {
                        caller.getInventory().addItem(CrateManager.getCrateKey(crateType));
                        caller.sendMessage(ChatColor.GREEN + "Key for " + crateType.name + "gived successfully!");
                        return;
                    }
                }
            }
        }
    }

    public static ItemStack confirmItemStack() {
        ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 5);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(ChatColor.GREEN + "Confirm");
        itemStack.setItemMeta(itemMeta);
        itemStack = UtilNBT.set(itemStack, "true", "MenuItem");
        itemStack = UtilNBT.set(itemStack, "confirm", "MenuCommand");
        return itemStack;
    }

    private void showCommands(Player player) {
        player.sendMessage(ChatColor.GREEN + "=-=-=- Crate Commands -=-=-=");
        player.sendMessage(ChatColor.GREEN + " /crate list");
        player.sendMessage(ChatColor.GREEN + " /crate create <key> <name>");
        player.sendMessage(ChatColor.GREEN + " /crate edit <key>");
        player.sendMessage(ChatColor.GREEN + " /crate delete <key>");
        player.sendMessage(ChatColor.GREEN + " /crate addchest");
        player.sendMessage(ChatColor.GREEN + " /crate delchest");
    }

}
