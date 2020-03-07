package com.ellisiumx.elcore.preventer;

import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilLog;
import com.ellisiumx.elcore.utils.UtilServer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class PreventManager implements Listener {

    public PreventManager(JavaPlugin plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if(event.getMessage().startsWith("/op")) event.setCancelled(true);
        else if(event.getMessage().startsWith("/plugins")) event.setCancelled(true);
        else if(event.getMessage().startsWith("/?")) event.setCancelled(true);
        else if(event.getMessage().startsWith("/bukkit")) event.setCancelled(true);
        else if(event.getMessage().startsWith("/spigot")) event.setCancelled(true);
        else if(event.getMessage().startsWith("/minecraft")) event.setCancelled(true);
        else if(event.getMessage().startsWith("/deop")) event.setCancelled(true);
        else if(event.getMessage().startsWith("/help")) event.setCancelled(true);
        else if(event.getMessage().startsWith("/stop")) event.setCancelled(true);
        else if(event.getMessage().startsWith("/reload")) event.setCancelled(true);
        if(event.isCancelled()) {
            event.getPlayer().sendMessage(UtilChat.cRed + "This command is disabled by " + UtilChat.cGreen + "ELCore");
        }
    }

    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
        if(event.getCommand().startsWith("op")) event.setCancelled(true);
        else if(event.getCommand().startsWith("plugins")) event.setCancelled(true);
        else if(event.getCommand().startsWith("?")) event.setCancelled(true);
        else if(event.getCommand().startsWith("bukkit")) event.setCancelled(true);
        else if(event.getCommand().startsWith("spigot")) event.setCancelled(true);
        else if(event.getCommand().startsWith("minecraft")) event.setCancelled(true);
        else if(event.getCommand().startsWith("deop")) event.setCancelled(true);
        else if(event.getCommand().startsWith("help")) event.setCancelled(true);
        else if(event.getCommand().startsWith("reload")) event.setCancelled(true);
        if(event.isCancelled()) {
            UtilLog.log(Level.WARNING, UtilChat.cRed + "This command is disabled by " + UtilChat.cGreen + "ELCore");
            event.setCommand("");
        }
    }

}
