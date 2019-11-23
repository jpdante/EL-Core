package com.ellisiumx.elcore.monitor;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.account.CoreClient;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.utils.UtilMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;

public class LagMeter implements Listener {
    private long _lastRun = -1;
    private int _count;
    private double _ticksPerSecond;
    private double _ticksPerSecondAverage;
    private long _lastAverage;
    private HashSet<Player> _monitoring = new HashSet<>();

    public LagMeter(JavaPlugin plugin) {
        _lastRun = System.currentTimeMillis();
        _lastAverage = System.currentTimeMillis();
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().trim().equalsIgnoreCase("/lag")) {
            CoreClient client = ELCore.getContext().getClientManager().get(event.getPlayer());
            if(client == null) return;
            if (client.getRank() == Rank.ADMIN || client.getRank() == Rank.DEVELOPER) {
                sendUpdate(event.getPlayer());
                event.setCancelled(true);
            }
        } else if (event.getMessage().trim().equalsIgnoreCase("/monitor")) {
            CoreClient client = ELCore.getContext().getClientManager().get(event.getPlayer());
            if(client == null) return;
            if (client.getRank() == Rank.ADMIN || client.getRank() == Rank.DEVELOPER) {
                if (_monitoring.contains(event.getPlayer()))
                    _monitoring.remove(event.getPlayer());
                else
                    _monitoring.add(event.getPlayer());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        _monitoring.remove(event.getPlayer());
    }

    @EventHandler
    public void update(UpdateEvent event) {
        if (event.getType() != UpdateType.SEC) return;
        long now = System.currentTimeMillis();
        _ticksPerSecond = 1000D / (now - _lastRun) * 20D;
        sendUpdates();
        if (_count % 30 == 0) {
            _ticksPerSecondAverage = 30000D / (now - _lastAverage) * 20D;
            _lastAverage = now;
        }
        _lastRun = now;
        _count++;
    }

    public double getTicksPerSecond() {
        return _ticksPerSecond;
    }

    private void sendUpdates() {
        for (Player player : _monitoring) {
            sendUpdate(player);
        }
    }

    private void sendUpdate(Player player) {
        player.sendMessage(" ");
        player.sendMessage(" ");
        player.sendMessage(" ");
        player.sendMessage(" ");
        player.sendMessage(" ");
        player.sendMessage(UtilMessage.main("LagMeter", ChatColor.GRAY + "Live-------" + ChatColor.YELLOW + String.format("%.00f", _ticksPerSecond)));
        player.sendMessage(UtilMessage.main("LagMeter", ChatColor.GRAY + "Avg--------" + ChatColor.YELLOW + String.format("%.00f", _ticksPerSecondAverage * 20)));
        player.sendMessage(UtilMessage.main("LagMeter", ChatColor.YELLOW + "MEM"));
        player.sendMessage(UtilMessage.main("LagMeter", ChatColor.GRAY + "Free-------" + ChatColor.YELLOW + (Runtime.getRuntime().freeMemory() / 1048576) + "MB"));
        player.sendMessage(UtilMessage.main("LagMeter", ChatColor.GRAY + "Max--------" + ChatColor.YELLOW + (Runtime.getRuntime().maxMemory() / 1048576)) + "MB");
    }
}
