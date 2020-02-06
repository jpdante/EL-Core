package com.ellisiumx.elrankup.kit;

import com.ellisiumx.elcore.lang.LanguageDB;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elcore.utils.UtilNBT;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.crate.holder.CrateMenuHolder;
import com.ellisiumx.elrankup.kit.command.KitCommand;
import com.ellisiumx.elrankup.kit.holder.KitMenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class KitManager implements Listener {

    public static KitManager context;
    public HashMap<String, PlayerKit> playersKits;

    public KitManager(JavaPlugin plugin) {
        context = this;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        playersKits = new HashMap<>();
        for (LanguageDB languageDB : LanguageManager.getLanguages()) {
            languageDB.insertTranslation("KitNameAlreadyExists", "&f[&aKits&f] &cA kit with that name already exists!");
        }
        if (LanguageManager.saveLanguages()) LanguageManager.reloadLanguages();
        new KitCommand(plugin);
    }

    public static PlayerKit get(Player player) { return get(player.getName()); }

    public static PlayerKit get(String playerName) {
        return context.playersKits.get(playerName);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() == null) return;
        if (!(event.getInventory().getHolder() instanceof KitMenuHolder)) return;
        KitMenuHolder holder = (KitMenuHolder) event.getInventory().getHolder();
        ItemStack itemStack = event.getCurrentItem();
        if(itemStack == null || itemStack.getType() == Material.AIR) return;
        if (!UtilNBT.contains(itemStack, "MenuItem")) return;
        event.setCancelled(true);
        String command = UtilNBT.getString(itemStack, "MenuCommand");
        if (command == null) return;
        String[] args = command.split(" ", 2);
        if(args[0].equals("confirm")) {
            createKit((Player) event.getWhoClicked(), event.getInventory(), holder);
        }
    }

    public void createKit(Player player, Inventory inventory, KitMenuHolder kitMenuHolder) {
        ArrayList<ItemStack> items = new ArrayList<>();
        for(ItemStack itemStack : inventory.getContents()) {
            if(itemStack == null || itemStack.getType() == Material.AIR) continue;
            if (UtilNBT.contains(itemStack, "MenuItem")) continue;
            items.add(itemStack);
        }
        RankupConfiguration.Kits.add(new Kit(UUID.randomUUID().toString(), kitMenuHolder.kitName, kitMenuHolder.kitName, 0, kitMenuHolder.ranks, items));
        RankupConfiguration.save();
        player.sendMessage(ChatColor.GREEN + "Kit created successfully!");
        player.closeInventory();
    }

    public void deleteKit(Player player, String kitName) {
        for(int i = 0; i < RankupConfiguration.Kits.size(); i++) {
            if(RankupConfiguration.Kits.get(i).getName().equals(kitName)) {
                RankupConfiguration.Kits.remove(RankupConfiguration.Kits.get(i));
                RankupConfiguration.save();
                return;
            }
        }
    }

    public void editKit(Player player, String kitName) {

    }

    public void openKits(Player player) {

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        playersKits.put(event.getPlayer().getName(), new PlayerKit(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playersKits.remove(event.getPlayer().getName());
    }
}