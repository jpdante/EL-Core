package com.ellisiumx.elcore.monitor;

import com.ellisiumx.elcore.updater.event.UpdateEvent;
import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.utils.UtilMessage;
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
    private long _lastTick = 0;
    private HashSet<Player> _monitoring = new HashSet<Player>();

    public LagMeter(JavaPlugin plugin) {
        _lastRun = System.currentTimeMillis();
        _lastAverage = System.currentTimeMillis();
    }

    @EventHandler
    public void onPlayerCommandPreProcess(PlayerCommandPreprocessEvent event) {
        if (event.getMessage().trim().equalsIgnoreCase("/lag")) {
            sendUpdate(event.getPlayer());
            event.setCancelled(true);
        } else if (event.getMessage().trim().equalsIgnoreCase("/monitor")) {
            if (_monitoring.contains(event.getPlayer()))
                _monitoring.remove(event.getPlayer());
            else
                _monitoring.add(event.getPlayer());
            event.setCancelled(true);
        }
        /*if (!InternalPlayerCache.playerProfiles.containsKey(event.getPlayer().getUniqueId())) return;
        PlayerProfile profile = InternalPlayerCache.playerProfiles.get(event.getPlayer().getUniqueId());
        if (profile.profileData.rank == Rank.ADMIN || profile.profileData.rank == Rank.DEVELOPER) {
            if (event.getMessage().trim().equalsIgnoreCase("/lag")) {
                sendUpdate(event.getPlayer());
                event.setCancelled(true);
            } else if (event.getMessage().trim().equalsIgnoreCase("/monitor")) {
                if (_monitoring.contains(event.getPlayer()))
                    _monitoring.remove(event.getPlayer());
                else
                    _monitoring.add(event.getPlayer());
                event.setCancelled(true);
            }
        }*/
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
