package com.ellisiumx.elrankup.clan;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.lang.LanguageDB;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elcore.timing.TimingManager;
import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import com.ellisiumx.elcore.utils.UtilLog;
import com.ellisiumx.elrankup.clan.command.ClanCommand;
import com.ellisiumx.elrankup.clan.repository.ClanRepository;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.economy.EconomyManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.logging.Level;

public class ClanManager implements Listener {

    public static ClanManager context;
    public ClanRepository repository;
    public ArrayList<Clan> clans;
    public HashMap<String, ClanPlayer> playerClans;
    public boolean initialized;

    public ClanManager(JavaPlugin plugin) {
        context = this;
        repository = new ClanRepository(plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            TimingManager.start("load clans");
            clans = repository.getClans();
            TimingManager.stop("load clans");
            UtilLog.log(Level.INFO, "[Clans] " + clans.size() + " clans loaded from mysql.");
            initialized = true;
        });
        clans = new ArrayList<>();
        playerClans = new HashMap<>();
        for (LanguageDB languageDB : LanguageManager.getLanguages()) {
            languageDB.insertTranslation("MachineTransactionFailure", "&f[&aMachines&f] &c");
        }
        if (LanguageManager.saveLanguages()) LanguageManager.reloadLanguages();
        new ClanCommand(plugin);
    }

    public static ClanPlayer getClanPlayer(String playerName) {
        return context.playerClans.get(playerName);
    }

    public static ClanPlayer getClanPlayer(Player player) {
        return getClanPlayer(player.getName());
    }

    public void createClan(Player player, String colorTag, String name) {
        if(!EconomyManager.economy.has(player, RankupConfiguration.clanCreationPrice)) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansFriendFireCommand"));
            return;
        }
    }

    @EventHandler
    public void onBufferElapsed(UpdateEvent event) {
        if (event.getType() != UpdateType.SLOW) return;
        Stack<Clan> clanStack = new Stack<>();
        for (Clan clan : clans) {
            if (clan.updated) {
                clanStack.push(clan);
                clan.updated = false;
            }
        }
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            if (!clanStack.isEmpty()) {
                repository.updateClans(clanStack);
            }
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDeath(EntityDeathEvent event) {

    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamage(EntityDamageEvent event) {

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            TimingManager.start(event.getPlayer().getName() + " load clan");
            ClanPlayer clanPlayer = repository.getClanPlayer(CoreClientManager.get(event.getPlayer()).getAccountId(), clans);
            playerClans.put(event.getPlayer().getName(), clanPlayer);
            TimingManager.stop(event.getPlayer().getName() + " load clan");
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            playerClans.remove(event.getPlayer().getName());
        });
    }

}
