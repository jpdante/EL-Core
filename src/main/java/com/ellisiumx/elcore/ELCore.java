package com.ellisiumx.elcore;

import com.ellisiumx.elcore.configuration.CoreConfiguration;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.memory.MemoryFix;
import com.ellisiumx.elcore.monitor.LagMeter;
import com.ellisiumx.elcore.updater.Updater;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class ELCore extends JavaPlugin {

    private static ELCore context;
    public CoreConfiguration config;

    @Override
    public void onLoad() {
        this.getLogger().log(Level.INFO, "[ELCore] Loading...");
        this.context = this;
        this.getLogger().log(Level.INFO, "[ELCore] Loaded!");
    }

    @Override
    public void onEnable() {
        this.getLogger().log(Level.INFO, "[ELCore] Enabling...");
        this.getServer().setWhitelist(true);
        saveDefaultConfig();
        reloadConfig();
        config = new CoreConfiguration(context);
        /*if(config.Database_Enabled) {
            if(config.Database_Type.equalsIgnoreCase("mysql")) {
                database = new MySQL(config.Database_Host, config.Database_Port, config.Database_Database, config.Database_Username, config.Database_Password);
                Bukkit.getLogger().log(Level.INFO, "[XCore] Using MySQL");
            } else if(config.Database_Type.equalsIgnoreCase("sqlite")) {
                database = new SQLite(config.Database_Filename);
                Bukkit.getLogger().log(Level.INFO, "[XCore] Using SQLite");
            }
            try {
                database.openConnection();
            } catch (SQLException e) {
                e.printStackTrace();
                Bukkit.shutdown();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                Bukkit.shutdown();
            }
            defaultDataProvider = new DataProvider(database);
            dataManager = new DataManager(defaultDataProvider);
        }
        if(config.Redis_Enabled) {
            Bukkit.getLogger().log(Level.INFO, "[XCore] Using Redis");
            Bukkit.getServer().getPluginManager().registerEvents(new RedisManager(), plugin);
            RedisManager.getContext().Connect(config.Redis_Host, config.Redis_Port, config.Redis_Password, config.Redis_Database);
        }*/
        new Updater(context);
        //new InternalPlayerCache();
        new LanguageManager();
        //cache = new UtilCache();
        //this.getServer().getPluginManager().registerEvents(new onPlayerJoin(), plugin);
        //this.getServer().getPluginManager().registerEvents(new onPlayerQuit(), plugin);
        //this.getServer().getPluginManager().registerEvents(new onPlayerChat(), plugin);
        //this.getServer().getPluginManager().registerEvents(new onPlayerCommandPreProcess(), plugin);
        this.getServer().getPluginManager().registerEvents(new LagMeter(context), context);
        if(config.MemoryFixer_Enabled) {
            MemoryFix.last_failed = false;
            MemoryFix.min_memory = config.MemoryFixer_Min;
            this.getServer().getPluginManager().registerEvents(new MemoryFix(), context);
        }
        this.getServer().setWhitelist(false);
        this.getLogger().log(Level.INFO, "[ELCore] Enabled!");
    }

    @Override
    public void onDisable() {
        this.getLogger().log(Level.INFO, "[ELCore] Unloading...");
        /*if(config.Database_Enabled) {
            dataManager.shutdown();
        }
        if(config.Redis_Enabled) {
            RedisManager.getContext().Disconnect();
        }*/
        this.getLogger().log(Level.INFO, "[ELCore] Unloaded!");
    }

    public static ELCore getContext() {
        return context;
    }
}
