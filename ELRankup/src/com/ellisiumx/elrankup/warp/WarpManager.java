package com.ellisiumx.elrankup.warp;

import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.lang.LanguageDB;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elcore.recharge.Recharge;
import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilConvert;
import com.ellisiumx.elcore.utils.UtilMessage;
import com.ellisiumx.elcore.utils.UtilNBT;
import com.ellisiumx.elrankup.configuration.MenuConfig;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.machine.holder.MachineDropsMenuHolder;
import com.ellisiumx.elrankup.machine.holder.MachineFuelMenuHolder;
import com.ellisiumx.elrankup.machine.holder.MachineInfoMenuHolder;
import com.ellisiumx.elrankup.machine.holder.MachineMenuHolder;
import com.ellisiumx.elrankup.warp.command.SpawnCommand;
import com.ellisiumx.elrankup.warp.command.WarpCommand;
import com.ellisiumx.elrankup.warp.command.WarpsCommand;
import com.ellisiumx.elrankup.warp.holder.WarpMenuHolder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WarpManager implements Listener {

    public static WarpManager context;
    public final HashMap<String, PlayerWarp> playerWarps;
    public ArrayList<Inventory> menus;

    public WarpManager(JavaPlugin plugin) {
        context = this;
        playerWarps = new HashMap<>();
        menus = new ArrayList<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        for (LanguageDB languageDB : LanguageManager.getLanguages()) {
            // Errors
            languageDB.insertTranslation("AlreadyWarping", "&f[&3Warp&f] &cYou are already teleporting, please wait!");
            languageDB.insertTranslation("WarpingCancelled", "&f[&3Warp&f] &cTeleport canceled, you moved!");
            languageDB.insertTranslation("WarpDontExist", "&f[&3Warp&f] &cThis warp does not exist!");
            languageDB.insertTranslation("WarpDontHasRank", "&f[&3Warp&f] &cYou don't have enough rank (%rank%) to teleport!");
            languageDB.insertTranslation("WarpsNoMenu", "&f[&3Warp&f] &cIt was not possible to find this menu!");
            // Messages
            languageDB.insertTranslation("Warping", "&f[&3Warp&f] &aTeleporting in %delay% seconds");
            languageDB.insertTranslation("WarpCommand", " &6/warp <warp name> - Teleport to the specified warp");
            languageDB.insertTranslation("WarpSetCommand", " &6/warp set <warp name> <rank> - Create new warp or set");
            languageDB.insertTranslation("WarpDelCommand", " &6/warp del <warp name> - Delete warp");
            languageDB.insertTranslation("WarpsCommand", " &6/warps - Show all available warps");
            languageDB.insertTranslation("WarpsMessage", "&6Warps: &f%warps%");
        }
        if (LanguageManager.saveLanguages()) LanguageManager.reloadLanguages();
        new WarpCommand(plugin);
        new WarpsCommand(plugin);
        new SpawnCommand(plugin);
        WarpMenuHolder warpMenuHolder = new WarpMenuHolder();
        for(MenuConfig menuConfig : RankupConfiguration.WarpMenus) {
            menus.add(menuConfig.createMenu(warpMenuHolder));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder == null) return;
        if (!(holder instanceof WarpMenuHolder)) return;
        event.setCancelled(true);
        if (!Recharge.use((Player) event.getWhoClicked(), "Warp", 400, false, false)) {
            event.getWhoClicked().sendMessage(UtilMessage.main("Warp", "You can't spam commands that fast."));
            return;
        }
        ItemStack itemStack = event.getCurrentItem();
        if(itemStack == null || itemStack.getType() == Material.AIR) return;
        if (!UtilNBT.contains(itemStack, "MenuItem")) return;
        String command = UtilNBT.getString(itemStack, "MenuCommand");
        if (command == null) return;
        String[] args = command.split(" ", 2);
        if(args[0].equals("open")) {
            if (args.length < 2) return;
            int index = Integer.parseInt(args[1]);
            openMenu((Player) event.getWhoClicked(), index);
        } else if(args[0].equals("warp")) {
            if (args.length < 2) return;
            warpPlayer((Player) event.getWhoClicked(), args[1]);
            event.getWhoClicked().closeInventory();
        } else if(args[0].equals("close")) {
            event.getWhoClicked().closeInventory();
        }
    }

    public void openMenu(Player player, int index) {
        if(index < 0 || index > menus.size() - 1) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "WarpsNoMenu").replace('&', ChatColor.COLOR_CHAR));
            player.closeInventory();
            return;
        }
        player.openInventory(menus.get(index));
    }

    public void warpPlayer(Player player, String warp) {
        if (playerWarps.containsKey(player.getName())) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "AlreadyWarping").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if (!RankupConfiguration.Warps.containsKey(warp.toLowerCase())) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "WarpDontExist").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        Warp warpLocation = RankupConfiguration.Warps.get(warp.toLowerCase());
        Rank playerRank = CoreClientManager.get(player).getRank();
        if (!playerRank.has(warpLocation.getRank())) {
            player.sendMessage(
                    LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "WarpDontHasRank")
                            .replaceAll("%rank%", warpLocation.getRank().getTag(true, true))
                            .replace('&', ChatColor.COLOR_CHAR)
            );
            return;
        }
        int delay = 0;
        if (RankupConfiguration.WarpDelay.containsKey(playerRank))
            delay = RankupConfiguration.WarpDelay.get(playerRank);
        if(delay == 0) {
            player.teleport(warpLocation.getLocation());
        } else {
            playerWarps.put(player.getName(), new PlayerWarp(player, player.getLocation(), warpLocation.getLocation(), delay));
            player.sendMessage(
                    LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "Warping")
                            .replaceAll("%delay%", String.valueOf(delay))
                            .replace('&', ChatColor.COLOR_CHAR)
            );
        }
    }

    public void warpPlayer(Player player, Location location) {
        if (playerWarps.containsKey(player.getName())) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "AlreadyWarping").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        Rank playerRank = CoreClientManager.get(player).getRank();
        int delay = 0;
        if (RankupConfiguration.WarpDelay.containsKey(playerRank))
            delay = RankupConfiguration.WarpDelay.get(playerRank);
        if(delay == 0) {
            player.teleport(location);
        } else {
            playerWarps.put(player.getName(), new PlayerWarp(player, player.getLocation(), location, delay));
            player.sendMessage(
                    LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "Warping")
                            .replaceAll("%delay%", String.valueOf(delay))
                            .replace('&', ChatColor.COLOR_CHAR)
            );
        }
    }

    public void setWarp(Player player, String warp, Rank rank) {
        RankupConfiguration.Warps.remove(warp);
        RankupConfiguration.Warps.put(warp, new Warp(warp, player.getLocation(), rank));
        RankupConfiguration.save();
        player.sendMessage(UtilMessage.main("Warp", UtilChat.cGreen + "Warp '" + warp + "' set!"));
    }

    public void deleteWarp(Player player, String warp) {
        RankupConfiguration.Warps.remove(warp);
        RankupConfiguration.save();
        player.sendMessage(UtilMessage.main("Warp", UtilChat.cGreen + "Warp '" + warp + "' deleted!"));
    }

    @EventHandler
    public void onTimerElapsed(UpdateEvent event) {
        if (event.getType() != UpdateType.SEC) return;
        synchronized (playerWarps) {
            for (String player : playerWarps.keySet()) {
                PlayerWarp playerWarp = playerWarps.get(player);
                playerWarp.setDelay(playerWarp.getDelay() - 1);
                if (playerWarp.getDelay() <= 0) {
                    playerWarps.remove(player);
                    Location l1 = playerWarp.getPlayer().getLocation();
                    Location l2 = playerWarp.getFrom();
                    if (l1.getBlockX() != l2.getBlockX() || l1.getBlockY() != l2.getBlockY() || l1.getBlockZ() != l2.getBlockZ()) {
                        playerWarp.getPlayer().sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "WarpingCancelled").replace('&', ChatColor.COLOR_CHAR));
                        return;
                    }
                    playerWarp.getPlayer().teleport(playerWarp.getTo());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerWarps.remove(event.getPlayer().getName());
    }
}
