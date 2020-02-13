package com.ellisiumx.elcore.preferences;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.account.ClientCache;
import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.redis.DataRepository;
import com.ellisiumx.elcore.redis.RedisDataRepository;
import com.ellisiumx.elcore.redis.RedisManager;
import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class PreferencesManager implements Listener {
    private static PreferencesManager context;

    private PreferencesRepository repository;
    private HashMap<String, UserPreferences> userPreferences;
    private Stack<UserPreferences> saveBuffer = new Stack<>();
    private DataRepository<UserPreferences> cacheDataRepository;

    public PreferencesManager(JavaPlugin plugin) {
        context = this;
        repository = new PreferencesRepository(plugin);
        userPreferences = new HashMap<>();
        cacheDataRepository = new RedisDataRepository(RedisManager.getMasterConnection(), UserPreferences.class, "preferences");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static void save(Player caller) {
        context.saveBuffer.add(context.userPreferences.get(caller.getName()));
    }

    public static UserPreferences get(Player caller) {
        return context.userPreferences.get(caller.getName());
    }

    public static UserPreferences get(String playerName) {
        return context.userPreferences.get(playerName);
    }

    @EventHandler
    public void processBuffer(UpdateEvent event) {
        if (event.getType() != UpdateType.SLOW) return;
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), new Runnable() {
            public void run() {
                Stack<UserPreferences> bufferCopy = new Stack<>();
                while (!saveBuffer.empty()) {
                    UserPreferences preferences = saveBuffer.pop();
                    bufferCopy.add(preferences);
                    cacheDataRepository.addElement(preferences, 3600);
                }
                if (bufferCopy.isEmpty()) return;
                repository.saveUserPreferences(bufferCopy);
            }
        });
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        userPreferences.put(event.getPlayer().getName(), null);
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), new Runnable() {
            public void run() {
                UserPreferences preferences = cacheDataRepository.getElement(event.getPlayer().getUniqueId().toString());
                if (preferences == null) {
                    preferences = repository.loadClientInformation(event.getPlayer().getUniqueId().toString());
                }
                if (preferences == null) {
                    preferences = new UserPreferences(
                            event.getPlayer().getUniqueId().toString(),
                            CoreClientManager.get(event.getPlayer()).getAccountId(),
                            ((CraftPlayer) event.getPlayer()).getHandle().locale,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false
                    );
                }
                preferences.setUUID(event.getPlayer().getUniqueId().toString());
                cacheDataRepository.addElement(preferences, 3600);
                if (userPreferences.containsKey(event.getPlayer().getName())) {
                    userPreferences.replace(event.getPlayer().getName(), preferences);
                }
            }
        });
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        userPreferences.remove(event.getPlayer().getName());
    }
}

