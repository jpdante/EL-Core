package com.ellisiumx.elcore.preventer;

import com.ellisiumx.elcore.utils.UtilChat;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

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
        else if(event.getMessage().startsWith("/deop")) event.setCancelled(true);
        else if(event.getMessage().startsWith("/help")) event.setCancelled(true);
        else if(event.getMessage().startsWith("/stop")) event.setCancelled(true);
        if(event.isCancelled()) {
            event.getPlayer().sendMessage(UtilChat.cRed + "This command is disabled by " + UtilChat.cGreen + "ELCore");
        }
    }

}
