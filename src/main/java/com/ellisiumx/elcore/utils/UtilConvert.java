package com.ellisiumx.elcore.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public final class UtilConvert {

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

    public static String getStringFromLocation(Location location) {
        String data = location.getWorld().getName() + ",";
        data += String.valueOf(location.getX()) + ",";
        data += String.valueOf(location.getY()) + ",";
        data += String.valueOf(location.getZ()) + ",";
        data += String.valueOf(location.getPitch()) + ",";
        data += String.valueOf(location.getYaw());
        return data;
    }

    public static Location getLocationFromString(String data) {
        String[] datas = data.split(",");
        if(datas.length != 6) throw new ArrayIndexOutOfBoundsException("Location string is diferent from 6 parts.");
        World world = Bukkit.getWorld(datas[0]);
        double x = Double.parseDouble(datas[1]);
        double y = Double.parseDouble(datas[2]);
        double z = Double.parseDouble(datas[3]);
        float pitch = Float.parseFloat(datas[4]);
        float yaw = Float.parseFloat(datas[5]);
        return new Location(world, x, y, z, pitch, yaw);
    }

}
