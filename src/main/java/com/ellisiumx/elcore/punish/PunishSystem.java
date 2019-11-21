package com.ellisiumx.elcore.punish;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.account.repository.AccountRepository;
import com.ellisiumx.elcore.punish.event.PlayerPreLoginApproved;
import com.ellisiumx.elcore.punish.repository.PunishRepository;
import com.ellisiumx.elcore.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;

import java.sql.Timestamp;

public class PunishSystem implements Listener {

    private PunishRepository repository;

    public PunishSystem() {
        repository = new PunishRepository(ELCore.getContext());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void AsyncLogin(AsyncPlayerPreLoginEvent event) {
        Pair<String, Timestamp> ban = repository.isBanned(event.getUniqueId().toString(), event.getName());
        if(ban != null) {
            String message = ban.getLeft();
            if(ban.getRight() != null) {
                message += ChatColor.GREEN + " Expire: " + ChatColor.WHITE + "[" + ChatColor.BLUE + ban.getRight().toString() + ChatColor.WHITE + "]";
            }
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, message);
        }
        Bukkit.getServer().getPluginManager().callEvent(new PlayerPreLoginApproved(event));
    }

}
