package com.ellisiumx.elcore.preferences;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.account.ClientCache;
import com.ellisiumx.elcore.redis.DataRepository;
import com.ellisiumx.elcore.redis.RedisDataRepository;
import com.ellisiumx.elcore.redis.RedisManager;
import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class PreferencesManager implements Listener {
    private static PreferencesManager context;

    private PreferencesRepository repository;
    private HashMap<String, UserPreferences> userPreferences;
    private HashMap<String, UserPreferences> saveBuffer = new HashMap<>();
    private DataRepository<UserPreferences> cacheDataRepository;

    public boolean GiveItem;

    public PreferencesManager(JavaPlugin plugin) {
        context = this;
        repository = new PreferencesRepository(plugin);
        userPreferences = new HashMap<>();
        cacheDataRepository = new RedisDataRepository(RedisManager.getMasterConnection(), UserPreferences.class, "preferences");
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static void save(Player caller) {
        context.saveBuffer.put(caller.getUniqueId().toString(), context.userPreferences.get(caller.getName()));
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
                final HashMap<String, UserPreferences> bufferCopy = new HashMap<>();
                for (Map.Entry<String, UserPreferences> entry : saveBuffer.entrySet()) {
                    bufferCopy.put(entry.getKey(), entry.getValue());
                    cacheDataRepository.addElement(entry.getValue(), 3600);
                }
                saveBuffer.clear();
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
                if(preferences == null) {
                    preferences = repository.loadClientInformation(event.getPlayer().getUniqueId().toString());
                    preferences.setUUID(event.getPlayer().getUniqueId().toString());
                    cacheDataRepository.addElement(preferences, 3600);
                }
                if(userPreferences.containsKey(event.getPlayer().getName())) {
                    userPreferences.replace(event.getPlayer().getName(), preferences);
                };
            }
        });
        //if (!GiveItem) return;
        //event.getPlayer().getInventory().setItem(8, ItemStackFactory.Instance.CreateStack(Material.REDSTONE_COMPARATOR.getId(), (byte) 0, 1, ChatColor.GREEN + "/prefs"));
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        userPreferences.remove(event.getPlayer().getName());
    }

    /*@EventHandler(priority = EventPriority.LOWEST)
    public void playerInteract(PlayerInteractEvent event) {
        if (!GiveItem)
            return;

        if (event.getItem() != null && event.getItem().getType() == Material.REDSTONE_COMPARATOR) {
            _shop.attemptShopOpen(event.getPlayer());

            event.setCancelled(true);
        }
    }

    public void openShop(Player caller) {
        _shop.attemptShopOpen(caller);
    }*/
}

