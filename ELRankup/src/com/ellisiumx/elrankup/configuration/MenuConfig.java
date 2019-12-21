package com.ellisiumx.elrankup.configuration;

import com.ellisiumx.elcore.utils.Pair;
import com.ellisiumx.elcore.utils.UtilConvert;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class MenuConfig {

    public String Name;
    public int Size;
    public ArrayList<Pair<Integer, ItemStack>> Items;

    public MenuConfig(ConfigurationSection section) {
        Name = section.getString("name");
        Size = section.getInt("size");
        Items = new ArrayList<>();
        for(String data : section.getStringList("items")) {
            Items.add(UtilConvert.getMenuItemFromString(data));
        }
    }

    public Inventory createMenu(InventoryHolder holder) {
        Inventory inventory =  Bukkit.createInventory(holder, Size, Name.replace('&', ChatColor.COLOR_CHAR));
        for(Pair<Integer, ItemStack> item : Items) {
            inventory.setItem(item.getLeft(), item.getRight());
        }
        return inventory;
    }
}
