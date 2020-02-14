package com.ellisiumx.elrankup.autolapis;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.EnchantingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class AutoLapisManager implements Listener {

    private ArrayList<EnchantingInventory> inventories;
    private ItemStack lapis;

    public AutoLapisManager(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
        inventories = new ArrayList<>();
        Dye d = new Dye();
        d.setColor(DyeColor.BLUE);
        this.lapis = d.toItemStack();
        this.lapis.setAmount(64);
    }

    @EventHandler
    public void openInventoryEvent(InventoryOpenEvent e) {
        if (e.getInventory() instanceof EnchantingInventory) {
            e.getInventory().setItem(1, this.lapis);
            inventories.add((EnchantingInventory) e.getInventory());
        }
    }

    @EventHandler
    public void closeInventoryEvent(InventoryCloseEvent e) {
        if (e.getInventory() instanceof EnchantingInventory) {
            if (inventories.contains((EnchantingInventory) e.getInventory())) {
                e.getInventory().setItem(1, null);
                inventories.remove((EnchantingInventory) e.getInventory());
            }
        }
    }

    @EventHandler
    public void inventoryClickEvent(InventoryClickEvent e) {
        if (e.getClickedInventory() instanceof EnchantingInventory) {
            if (inventories.contains((EnchantingInventory) e.getInventory())) {
                if (e.getSlot() == 1) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void enchantItemEvent(EnchantItemEvent e) {
        if (inventories.contains((EnchantingInventory) e.getInventory())) {
            e.getInventory().setItem(1, this.lapis);
        }
    }

}
