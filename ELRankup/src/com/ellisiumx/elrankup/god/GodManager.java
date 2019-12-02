package com.ellisiumx.elrankup.god;

import com.ellisiumx.elcore.utils.UtilServer;
import com.ellisiumx.elrankup.god.command.GodCommand;
import com.ellisiumx.elrankup.vanish.VanishManager;
import com.ellisiumx.elrankup.vanish.command.VanishCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class GodManager implements Listener {

    private static GodManager context;

    private List<Player> godPlayers;

    public GodManager(JavaPlugin plugin) {
        context = this;
        godPlayers = new ArrayList<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        new GodCommand(plugin);
    }

    public static void add(Player caller) {
        context.godPlayers.add(caller);
        caller.setHealth(caller.getMaxHealth());
    }

    public static void remove(Player caller) {
        context.godPlayers.remove(caller);
    }

    public static boolean contains(Player player) {
        return context.godPlayers.contains(player);
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        godPlayers.remove(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if(godPlayers.contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if(godPlayers.contains(player)) {
            player.setFireTicks(0);
            player.setRemainingAir(player.getMaximumAir());
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onEntityCombust(EntityCombustEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if(godPlayers.contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onEntityCombustByEntity(EntityCombustByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if(godPlayers.contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if(godPlayers.contains(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onPotionSplashEvent(PotionSplashEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if(godPlayers.contains(player)) {
            event.setCancelled(true);
        }
    }

}
