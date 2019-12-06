package com.ellisiumx.elrankup.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public final class UtilConversion {

    public static ItemStack getItemStackFromConfig(ConfigurationSection section) {
        try {
            String[] idData = section.getString("id").split(":");
            String name = section.getString("name");
            List<String> loreRaw = section.getStringList("lore");
            int id = Integer.parseInt(idData[0]);
            byte data = 0;
            if(idData.length >= 2) data = Byte.parseByte(idData[1]);
            ItemStack itemStack = new ItemStack(id, data);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(name.replace('&', ChatColor.COLOR_CHAR));
            ArrayList<String> lore = new ArrayList<>();
            for(String loreItem : loreRaw) {
                lore.add(loreItem.replace('&', ChatColor.COLOR_CHAR));
            }
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        } catch (Exception ex) {
            return null;
        }
    }

}
