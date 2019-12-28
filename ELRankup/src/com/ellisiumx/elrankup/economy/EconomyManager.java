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
import net.minecraft.server.v1_8_R3.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Stack;
import java.util.logging.Level;

public class EconomyManager implements Listener {

    public static EconomyRepository repository;
    public static VaultEconomy economy;

    public Stack<Tuple<String, String>> buffer;

    public EconomyManager(JavaPlugin plugin) {
        repository = new EconomyRepository(plugin);
        buffer = new Stack<>();
        if (UtilServer.getServer().getPluginManager().getPlugin("Vault") == null) {
            UtilLog.log(Level.WARNING, "Failed to start economy, Vault is not present on server!");
            UtilServer.shutdown();
            return;
        }
        economy = new VaultEconomy();
        Bukkit.getServicesManager().register(Economy.class, economy, plugin, ServicePriority.Normal);
        //RegisteredServiceProvider<VaultEconomy> rsp = UtilServer.getServer().getServicesManager().getRegistration(VaultEconomy.class);
        /*if (rsp == null) {
            UtilLog.log(Level.WARNING, "Failed to register economy in vault!");
            UtilServer.shutdown();
            return;
        }
        economy = rsp.getProvider();*/
        Bukkit.getPluginManager().registerEvents(this, plugin);
        UtilLog.log(Level.WARNING, "Registering command!");
        new MoneyCommand(plugin);
        new PayCommand(plugin);
        new EcoCommand(plugin);
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (event.getType() != UpdateType.SLOW) return;
        if (buffer.empty()) return;
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            try (
                    Connection connection = repository.getInternalConnection();
                    PreparedStatement statement = connection.prepareStatement("INSERT IGNORE INTO economy (uuid, name, balance) VALUES (?, ?, ?);");
            ){
                while (!buffer.empty()) {
                    Tuple<String, String> data = buffer.pop();
                    statement.setString(1, data.a());
                    statement.setString(2, data.b());
                    statement.setDouble(3, 0.0D);
                    statement.addBatch();
                    UtilLog.log(Level.INFO, "Ensuring that player " + data.b() + " has an economy account...");
                }
                statement.executeBatch();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        buffer.push(new Tuple<>(event.getPlayer().getUniqueId().toString(), event.getPlayer().getName()));
    }
}
