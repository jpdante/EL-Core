package com.ellisiumx.elrankup.vanish;

import com.ellisiumx.elcore.utils.UtilServer;
import com.ellisiumx.elrankup.vanish.command.VanishCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class VanishManager implements Listener {

    private static VanishManager context;

    private List<Player> vanishedPlayers;

    public VanishManager(JavaPlugin plugin) {
        context = this;
        vanishedPlayers = new ArrayList<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        new VanishCommand(plugin);
    }

    public static void add(Player caller) {
        context.vanishedPlayers.add(caller);
        for (Player player : UtilServer.getPlayers()) {
            player.hidePlayer(caller);
        }
        caller.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1, false));
    }

    public static void remove(Player caller) {
        context.vanishedPlayers.remove(caller);
        for (Player player : UtilServer.getPlayers()) {
            player.showPlayer(caller);
        }
        caller.removePotionEffect(PotionEffectType.INVISIBILITY);
    }

    public static boolean contains(Player player) {
        return context.vanishedPlayers.contains(player);
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        vanishedPlayers.remove(event.getPlayer());
    }

}