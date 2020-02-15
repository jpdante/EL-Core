package com.ellisiumx.elcore.account;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.account.event.ClientUnloadEvent;
import com.ellisiumx.elcore.account.repository.AccountRepository;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.redis.RedisDataRepository;
import com.ellisiumx.elcore.redis.RedisManager;
import com.ellisiumx.elcore.timing.TimingManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.CompletableFuture.runAsync;

public class CoreClientManager implements Listener {

    private static CoreClientManager context;

    private AccountRepository repository;
    private HashMap<String, CoreClient> clientList;
    private final Object clientLock = new Object();
    private RedisDataRepository<ClientCache> cacheDataRepository;
    private static AtomicInteger clientsConnecting = new AtomicInteger(0);
    private static AtomicInteger clientsProcessing = new AtomicInteger(0);
    private HashMap<String, ILoginProcessor> loginProcessors = new HashMap<>();
    private static HashMap<String, Object> clientLoginLock = new HashMap<>();

    public CoreClientManager(JavaPlugin plugin) {
        context = this;
        repository = new AccountRepository(ELCore.getContext());
        cacheDataRepository = new RedisDataRepository<>(RedisManager.getMasterConnection(), ClientCache.class, "accounts");
        clientList = new HashMap<>();
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static CoreClient add(String name) {
        CoreClient newClient = new CoreClient(name);
        CoreClient oldClient;
        synchronized (context.clientLock) {
            oldClient = context.clientList.put(name, newClient);
        }
        if (oldClient != null) {
            oldClient.delete();
        }
        return newClient;
    }

    public static void del(String name) {
        synchronized (context.clientLock) {
            context.clientList.remove(name);
        }
        ELCore.getContext().getServer().getPluginManager().callEvent(new ClientUnloadEvent(name));
    }

    public static void del(Player player) {
        synchronized (context.clientLock) {
            context.clientList.remove(player.getName());
        }
        ELCore.getContext().getServer().getPluginManager().callEvent(new ClientUnloadEvent(player.getName()));
    }

    public static CoreClient get(String name) {
        synchronized (context.clientLock) {
            return context.clientList.get(name);
        }
    }

    public static CoreClient get(Player player) {
        synchronized (context.clientLock) {
            return context.clientList.get(player.getName());
        }
    }

    public static int getPlayerCountIncludingConnecting() {
        return Bukkit.getOnlinePlayers().size() + Math.max(0, clientsConnecting.get());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void AsyncLogin(AsyncPlayerPreLoginEvent event) {
        try {
            clientsConnecting.incrementAndGet();
            while (clientsProcessing.get() >= 5) {
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                clientsProcessing.incrementAndGet();
                if (!loadClient(add(event.getName()), event.getUniqueId()))
                    event.disallow(Result.KICK_OTHER, "There was a problem logging you in.");
            } catch (Exception exception) {
                event.disallow(Result.KICK_OTHER, "Error retrieving information from web, please retry in a minute.");
                exception.printStackTrace();
            } finally {
                clientsProcessing.decrementAndGet();
            }
            if (Bukkit.hasWhitelist() && !get(event.getName()).getRank().has(Rank.MODERATOR)) {
                for (OfflinePlayer player : Bukkit.getWhitelistedPlayers()) {
                    if (player.getName().equalsIgnoreCase(event.getName())) {
                        return;
                    }
                }
                event.disallow(Result.KICK_WHITELIST, "You are not whitelisted my friend.");
            }
        } finally {
            clientsConnecting.decrementAndGet();
        }
        /*if(cacheDataRepository.elementExists(event.event.getUniqueId().toString())) {
            ClientCache cache = cacheDataRepository.getElement(event.event.getUniqueId().toString());
            if(!event.event.getName().equals(cache.name)) {
                cache.name = event.event.getName();
                repository.updateName(cache.uuid, cache.name);
            }
            if(cache == null) {
                event.event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "There was a problem logging in to your account, please try again later.\nIf the problem persists notify the administration.");
                return;
            }
            CoreClient client = new CoreClient(cache);
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
        } else {
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
            if(!cacheDataRepository.elementExists(event.event.getUniqueId().toString())) {
                cacheDataRepository.addElement(new ClientCache(client, event.event.getUniqueId().toString()), 3600);
            }
        }*/
    }

    private boolean loadClient(final CoreClient client, final UUID uuid) {
        TimingManager.start(client.getPlayerName() + " load client");
        long timeStart = System.currentTimeMillis();
        clientLoginLock.put(client.getPlayerName(), new Object());
        runAsync(() -> {
            if(!repository.login(client, loginProcessors, uuid.toString())) {
                System.out.println("[ClientManager] Catastrophic failure to load player!");
            }
            clientLoginLock.remove(client.getPlayerName());
        });
        while (clientLoginLock.containsKey(client.getPlayerName()) && System.currentTimeMillis() - timeStart < 15000) {
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (clientLoginLock.containsKey(client.getPlayerName())) {
            System.out.println("[ClientManager] MYSQL TOOK TOO LONG TO LOGIN...");
        }
        TimingManager.stop(client.getPlayerName() + " load client");
        System.out.println("[ClientManager] [" + client.getPlayerName() + "'s account id = " + client.getAccountId() + "]");
        if (client.getAccountId() > 0) cacheDataRepository.addElement(new ClientCache(client.getAccountId(), uuid.toString(), client.getPlayerName(), client.getRank()), 3600);
        return !clientLoginLock.containsKey(client.getPlayerName());
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
            if (client.getRank().has(event.getPlayer(), Rank.MVP, false)) {
                event.allow();
                event.setResult(PlayerLoginEvent.Result.ALLOWED);
                return;
            }
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "This server is full and no longer accepts players.");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void Quit(PlayerQuitEvent event) {
        del(event.getPlayer());
    }

    public void addStoredProcedureLoginProcessor(ILoginProcessor processor) {
        loginProcessors.put(processor.getName(), processor);
    }
}
