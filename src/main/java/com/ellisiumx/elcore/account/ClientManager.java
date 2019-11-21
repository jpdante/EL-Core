package com.ellisiumx.elcore.account;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.account.event.ClientUnloadEvent;
import com.ellisiumx.elcore.account.repository.AccountRepository;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.punish.event.PlayerPreLoginApproved;
import com.ellisiumx.elcore.redis.RedisDataRepository;
import com.ellisiumx.elcore.redis.RedisManager;
import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import com.google.gson.Gson;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientManager implements Listener {

    private AccountRepository repository;
    private HashMap<String, CoreClient> clientList;
    private HashSet<String> duplicateLoginGlitchPreventionList;
    private final Object clientLock = new Object();

    private static AtomicInteger clientsConnecting = new AtomicInteger(0);
    private static AtomicInteger clientsProcessing = new AtomicInteger(0);
    private static HashMap<String, Object> clientLoginLock = new HashMap<String, Object>();

    public ClientManager() {
        repository = new AccountRepository(ELCore.getContext());
        clientList = new HashMap<String, CoreClient>();
        duplicateLoginGlitchPreventionList = new HashSet<String>();
    }

    public CoreClient add(String name) {
        CoreClient newClient = new CoreClient(name);
        CoreClient oldClient;
        synchronized (clientLock) {
            oldClient = clientList.put(name, newClient);
        }
        if (oldClient != null) {
            oldClient.delete();
        }
        return newClient;
    }

    public void del(String name) {
        synchronized (clientLock) {
            clientList.remove(name);
        }
        ELCore.getContext().getServer().getPluginManager().callEvent(new ClientUnloadEvent(name));
    }

    public CoreClient get(String name) {
        synchronized (clientLock) {
            return clientList.get(name);
        }
    }

    public CoreClient get(Player player) {
        synchronized (clientLock) {
            return clientList.get(player.getName());
        }
    }

    public int getPlayerCountIncludingConnecting() {
        return Bukkit.getOnlinePlayers().size() + Math.max(0, clientsConnecting.get());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void AsyncLogin(PlayerPreLoginApproved event) {
        CoreClient client = repository.executeLogin(event.event.getUniqueId().toString(), event.event.getName());
        if(client == null) {
            event.event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "There was a problem logging in to your account, please try again later.\nIf the problem persists notify the administration.");
            return;
        }
        if(!clientList.containsKey(event.event.getName())) {
            clientList.put(event.event.getName(), client);
        }
        if (Bukkit.hasWhitelist() && !get(event.event.getName()).getRank().has(Rank.MODERATOR)) {
            for (OfflinePlayer player : Bukkit.getWhitelistedPlayers()) {
                if (player.getName().equalsIgnoreCase(event.event.getName())) {
                    return;
                }
            }
            event.event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_WHITELIST, "You are not whitelisted my friend.");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void Login(PlayerLoginEvent event) {
        CoreClient client = get(event.getPlayer().getName());
        if (client == null || client.getRank() == null) {
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "There was an error logging you in. Please reconnect.");
            return;
        }
        client.setPlayer(event.getPlayer());
        if (Bukkit.getOnlinePlayers().size() >= Bukkit.getServer().getMaxPlayers()) {
            if (client.getRank().has(event.getPlayer(), Rank.ULTRA, false)) {
                event.allow();
                event.setResult(PlayerLoginEvent.Result.ALLOWED);
                return;
            }
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "This server is full and no longer accepts players.");
        }
    }
}
