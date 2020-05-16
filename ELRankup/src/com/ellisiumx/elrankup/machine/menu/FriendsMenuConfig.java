package com.ellisiumx.elrankup.machine.menu;

import com.ellisiumx.elcore.utils.Pair;
import com.ellisiumx.elrankup.configuration.MenuConfig;
import com.ellisiumx.elrankup.machine.MachineFriend;
import com.ellisiumx.elrankup.machine.holder.MachineFriendsMenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class FriendsMenuConfig extends MenuConfig {
    public FriendsMenuConfig(ConfigurationSection section) {
        super(section);
    }

    public Inventory getInventory(ArrayList<MachineFriend> machineFriends) {
        ArrayList<Pair<Integer, ItemStack>> fixedItems = (ArrayList<Pair<Integer, ItemStack>>) Items.clone();
        if(machineFriends != null && machineFriends.size() > 0) {
            int j = 0;
            for (int i = 0; i < fixedItems.size(); i++) {
                Pair<Integer, ItemStack> item = fixedItems.get(i);
                MachineFriend friend = machineFriends.get(j);
                ItemMeta itemMeta = item.getRight().getItemMeta();
                itemMeta.setDisplayName(friend.getName());
                item.getRight().setItemMeta(itemMeta);
            }
        }
        Inventory inventory =  Bukkit.createInventory(new MachineFriendsMenuHolder(), Size, Name.replace('&', ChatColor.COLOR_CHAR));
        for(Pair<Integer, ItemStack> item : fixedItems) {
            inventory.setItem(item.getLeft(), item.getRight());
        }
        return inventory;
    }
}
