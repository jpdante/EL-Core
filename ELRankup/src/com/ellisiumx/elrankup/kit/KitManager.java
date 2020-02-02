package com.ellisiumx.elrankup.kit;

import com.ellisiumx.elrankup.kit.command.KitCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class KitManager implements Listener {

    public static KitManager context;
    public HashMap<String, PlayerKit> playersKits;

    public KitManager(JavaPlugin plugin) {
        context = this;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        playersKits = new HashMap<>();
        new KitCommand(plugin);
    }

    public static PlayerKit get(Player player) { return get(player.getName()); }

    public static PlayerKit get(String playerName) {
        return context.playersKits.get(playerName);
    }

    public void createKit(Player player, String kitName, String rankName) {

    }

    public void deleteKit(Player player, String kitName) {

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