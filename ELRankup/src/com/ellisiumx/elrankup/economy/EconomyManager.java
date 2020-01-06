package com.ellisiumx.elrankup.economy;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import com.ellisiumx.elcore.utils.UtilLog;
import com.ellisiumx.elcore.utils.UtilServer;
import com.ellisiumx.elrankup.economy.command.EcoCommand;
import com.ellisiumx.elrankup.economy.command.MoneyCommand;
import com.ellisiumx.elrankup.economy.command.PayCommand;
import com.ellisiumx.elrankup.economy.repository.EconomyRepository;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Stack;
import java.util.logging.Level;

public class EconomyManager implements Listener {

    public static EconomyManager context;
    public static EconomyRepository repository;
    public static VaultEconomy economy;

    public HashMap<String, PlayerMoney> playerMonies;

    public Stack<PlayerMoney> updateBuffer;

    public EconomyManager(JavaPlugin plugin) {
        context = this;
        repository = new EconomyRepository(plugin);
        if (UtilServer.getServer().getPluginManager().getPlugin("Vault") == null) {
            UtilLog.log(Level.WARNING, "Failed to start economy, Vault is not present on server!");
            UtilServer.shutdown();
            return;
        }
        Bukkit.getServicesManager().register(Economy.class, economy, plugin, ServicePriority.Normal);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        economy = new VaultEconomy();
        playerMonies = new HashMap<>();
        updateBuffer = new Stack<>();
        /*RegisteredServiceProvider<VaultEconomy> rsp = UtilServer.getServer().getServicesManager().getRegistration(VaultEconomy.class);
        if (rsp == null) {
            UtilLog.log(Level.WARNING, "Failed to register economy in vault!");
            UtilServer.shutdown();
            return;
        }
        economy = rsp.getProvider();*/
        new MoneyCommand(plugin);
        new PayCommand(plugin);
        new EcoCommand(plugin);
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (event.getType() != UpdateType.SLOW) return;
        if (updateBuffer.empty()) return;
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            repository.updateAccounts(updateBuffer);
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            repository.createEconomyAccount(event.getPlayer().getUniqueId().toString(), event.getPlayer().getName());
            double balance = repository.getBalanceByUUID(event.getPlayer().getUniqueId().toString());
            playerMonies.put(event.getPlayer().getName(), new PlayerMoney(event.getPlayer(), balance));
        });
    }
}
