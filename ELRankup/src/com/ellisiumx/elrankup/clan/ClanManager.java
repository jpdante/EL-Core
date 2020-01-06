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
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Level;

public class ClanManager implements Listener {

    public static ClanManager context;
    public ClanRepository repository;
    public ArrayList<Clan> clans;
    public HashMap<String, ClanPlayer> playerClans;
    public Stack<Clan> clanUpdateBuffer = new Stack<>();
    public Stack<ClanPlayer> playerUpdateBuffer = new Stack<>();
    public boolean initialized;

    public ClanManager(JavaPlugin plugin) {
        context = this;
        repository = new ClanRepository(plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            TimingManager.start("load clans");
            clans = repository.getClans();
            for(Clan clan : clans) {
                clan.calculateKdr();
            }
            TimingManager.stop("load clans");
            UtilLog.log(Level.INFO, "[Clans] " + clans.size() + " clans loaded from mysql.");
            initialized = true;
        });
        playerClans = new HashMap<>();
        for (LanguageDB languageDB : LanguageManager.getLanguages()) {
            languageDB.insertTranslation("ClanNotEnoughMoney", "&cYou do not have enough money to create a clan!");
            languageDB.insertTranslation("ClanTagMaxOut", "&cThe clan tag cannot be longer than 3 characters!");
            languageDB.insertTranslation("ClanTagMaxOutWithColors", "&cClan tags cannot be longer than 25 characters in total along with the colors!");
            languageDB.insertTranslation("ClanNameMaxOut", "&cThe clan name cannot be longer than 50 characters!");
            languageDB.insertTranslation("ClanTagAlreadyExists", "&cA clan with that tag already exists!");
            languageDB.insertTranslation("ClanNameAlreadyExists", "&cA clan with that name already exists!");
            languageDB.insertTranslation("ClanTransactionFailure", "&cFailed to transfer, please try again later. %ErrorMessage%");
            languageDB.insertTranslation("ClanCreated", "&aClan &b%ClanName% &ahas been successfully created!");
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
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClanNotEnoughMoney").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        String tag = ChatColor.stripColor(colorTag.replace('&', ChatColor.COLOR_CHAR));
        if(tag.length() > 3) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClanTagMaxOut").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if(colorTag.length() > 25) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClanTagMaxOutWithColors").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if(name.length() > 50) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClanNameMaxOut").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        for(Clan clan : clans) {
            if(clan.tag.equalsIgnoreCase(tag)) {
                player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClanTagAlreadyExists").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            if(clan.name.equalsIgnoreCase(name)) {
                player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClanNameAlreadyExists").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
        }
        EconomyResponse response = EconomyManager.economy.withdrawPlayer(player, RankupConfiguration.clanCreationPrice);
        if(!response.transactionSuccess()) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClanTransactionFailure").replaceAll("%ErrorMessage%", response.errorMessage).replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            Clan clan = new Clan(false, CoreClientManager.get(player).getAccountId(), tag, colorTag, name, false, 0, 0);
            repository.createClan(clan);
            clans.add(clan);
            ClanPlayer clanPlayer = getClanPlayer(player);
            clanPlayer.clan = clan;
            playerUpdateBuffer.push(clanPlayer);
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClanCreated").replaceAll("%ClanName%", name).replace('&', ChatColor.COLOR_CHAR));
        });
    }

    @EventHandler
    public void onBufferElapsed(UpdateEvent event) {
        if (event.getType() == UpdateType.SLOW) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
                if (!clanUpdateBuffer.isEmpty()) {
                    repository.updateClans(clanUpdateBuffer);
                    clans.sort(Comparator.comparingDouble(c -> c.kdr));
                }
                if (!playerUpdateBuffer.isEmpty()) {
                    repository.updateClanPlayers(playerUpdateBuffer);
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDeath(EntityDeathEvent event) { }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDamage(EntityDamageEvent event) { }

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
