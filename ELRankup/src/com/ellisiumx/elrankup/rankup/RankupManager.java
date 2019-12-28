package com.ellisiumx.elrankup.rankup;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import com.ellisiumx.elcore.utils.Pair;
import com.ellisiumx.elcore.utils.UtilLog;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.crate.CrateManager;
import com.ellisiumx.elrankup.crate.command.CrateCommand;
import com.ellisiumx.elrankup.rankup.command.RankupCommand;
import com.ellisiumx.elrankup.rankup.repository.RankupRepository;
import com.google.gson.internal.$Gson$Preconditions;
import net.minecraft.server.v1_8_R3.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import java.util.logging.Level;

public class RankupManager implements Listener {

    private static RankupManager context;
    private RankupRepository repository;
    private HashMap<String, RankLevel> playerRanks;
    public Stack<Pair<String, Integer>> buffer;

    public RankupManager(JavaPlugin plugin) {
        context = this;
        repository = new RankupRepository(plugin);
        playerRanks = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        /*for (LanguageDB languageDB : LanguageManager.getLanguages()) {
            languageDB.insertTranslation("MachineTransactionFailure", "&f[&aMachines&f] &cFailed to transfer, please try again later. %ErrorMessage%");
            languageDB.insertTranslation("MachineNotEnoughMoney", "&f[&aMachines&f] &cYou don't have enough money to buy %MachineType%&c, it costs %Cost%");
            languageDB.insertTranslation("FuelNotEnoughMoney", "&f[&aMachines&f] &cYou don't have enough money to buy Fuel, it costs &a%Cost%");
            languageDB.insertTranslation("MachineLimitReached", "&f[&aMachines&f] &cMachine limit for %MachineType%&c has been reached!");
            languageDB.insertTranslation("MachineBought", "&f[&aMachines&f] &aMachine %MachineType%&a bought successfully!");
            languageDB.insertTranslation("MachineFuelBought", "&f[&aMachines&f] &aFuel bought successfully!");
            languageDB.insertTranslation("MachineUpgraded", "&f[&aMachines&f] &aMachine successfully upgraded!");
            languageDB.insertTranslation("MachineFullDrop", "&f[&aMachines&f] &cThe machine is already full and can no longer work, sell the drops to get it back to work.");
            languageDB.insertTranslation("MachineTankAlreadyFull", "&f[&aMachines&f] &cThe fuel tank of the machine is already full!");
            languageDB.insertTranslation("MachineTankReFull", "&f[&aMachines&f] &aThe machine has been replenished!");
            languageDB.insertTranslation("MachineDropsSold", "&f[&aMachines&f] &a%DropsAmount% drops were sold for %TotalPrice%, your new balance is %Balance%.");
        }
        if (LanguageManager.saveLanguages()) LanguageManager.reloadLanguages();*/
        new RankupCommand(plugin);
    }

    public RankLevel get(Player player) {
        return playerRanks.get(player.getName());
    }

    public RankLevel get(String playerName) {
        return playerRanks.get(playerName);
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (event.getType() != UpdateType.SLOW) return;
        if (buffer.empty()) return;
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            ArrayList<Pair<Pair<String, Integer>, String>> ranks = repository.getRanks(buffer, RankupConfiguration.DefaultRank);
            for(Pair<Pair<String, Integer>, String> rank : ranks) {
                playerRanks.put(rank.getLeft().getLeft(), RankupConfiguration.getRankLevelByName(rank.getRight()));
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        buffer.push(new Pair<>(event.getPlayer().getName(), CoreClientManager.get(event.getPlayer()).getAccountId()));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerRanks.remove(event.getPlayer().getName());
    }
}
