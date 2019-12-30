package com.ellisiumx.elcore.utils;

import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public final class UtilConvert {

    public static Object fromBase64String( String s ) throws IOException , ClassNotFoundException {
        byte [] data = Base64.getDecoder().decode( s );
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(  data ));
        Object o  = ois.readObject();
        ois.close();
        return o;
    }

    public static String toBase64String(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream( baos );
        oos.writeObject( o );
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    public static ItemStack getItemStackFromConfig(ConfigurationSection section) {
        try {
            String[] idData = section.getString("id").split(":");
            String itemName = section.getString("name");
            List<String> loreRaw = section.getStringList("lore");
            int id = Integer.parseInt(idData[0]);
            byte data = 0;
            if(idData.length >= 2) data = Byte.parseByte(idData[1]);
            ItemStack itemStack = new ItemStack(id, data);
            ItemMeta itemMeta = itemStack.getItemMeta();
            setNameAndLore(itemMeta, itemName, loreRaw.toArray(new String[0]));
            itemStack.setItemMeta(itemMeta);
            return itemStack;
        } catch (Exception ex) {
            return null;
        }
    }

    public static Pair<Integer, ItemStack> getMenuItemFromString(String data) {
        String[] datas = data.split(",", 7);
        int slot = Integer.parseInt(datas[0]);
        Material itemMaterial = getMaterialFromString(datas[1]);
        byte itemData = getDataFromString(datas[1]);
        int itemAmount = Integer.parseInt(datas[2]);
        boolean itemEnchanted = Boolean.parseBoolean(datas[3]);
        String command = datas[4];
        String itemName = datas[5];
        String[] loreRaw;
        if(datas[6].length() > 0) loreRaw = datas[6].split("\\n");
        else loreRaw = new String[0];
        ItemStack itemStack = new ItemStack(itemMaterial, itemAmount, itemData);
        ItemMeta itemMeta = itemStack.getItemMeta();
        setNameAndLore(itemMeta, itemName, loreRaw);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        if(itemEnchanted) itemMeta.addEnchant(Enchantment.DURABILITY, 1, false);
        itemStack.setItemMeta(itemMeta);
        itemStack = UtilNBT.set(itemStack, "true", "MenuItem");
        itemStack = UtilNBT.set(itemStack, command, "MenuCommand");
        return new Pair<>(slot, itemStack);
    }

    private static void setNameAndLore(ItemMeta itemMeta, String itemName, String[] loreRaw) {
        itemMeta.setDisplayName(itemName.replace('&', ChatColor.COLOR_CHAR));
        ArrayList<String> lore = new ArrayList<>();
        for(String s : loreRaw) {
            lore.add(s.replace('&', ChatColor.COLOR_CHAR));
        }
        itemMeta.setLore(lore);
    }

    public static Material getMaterialFromString(String s) {
        String materialRaw = s.split(":", 2)[0];
        try {
            int id = Integer.parseInt(materialRaw);
            return Material.getMaterial(id);
        } catch (NumberFormatException ignored) { }
        try {
            return Material.getMaterial(materialRaw);
        } catch (Exception ignored) { }
        return Material.AIR;
    }

    public static int getMaterialIDFromString(String s) {
        String materialRaw = s.split(":", 2)[0];
        try {
            int id = Integer.parseInt(materialRaw);
            return Material.getMaterial(id).getId();
        } catch (NumberFormatException ignored) { }
        try {
            return Material.getMaterial(materialRaw).getId();
        } catch (Exception ignored) { }
        return -1;
    }

    public static byte getDataFromString(String s) {
        if(!s.contains(":")) return 0;
        String dataRaw = s.split(":", 2)[1];
        try {
            return Byte.parseByte(dataRaw);
        } catch (NumberFormatException ignored) { }
        return 0;
    }

    public static String getStringFromLocation(Location location) {
        String data = location.getWorld().getName() + ",";
        data += location.getX() + ",";
        data += location.getY() + ",";
        data += location.getZ() + ",";
        data += location.getPitch() + ",";
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
