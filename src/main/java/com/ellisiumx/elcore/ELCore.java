package com.ellisiumx.elcore;

import com.ellisiumx.elcore.configuration.CoreConfiguration;
import com.ellisiumx.elcore.database.DBPool;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.memory.MemoryFix;
import com.ellisiumx.elcore.monitor.LagMeter;
import com.ellisiumx.elcore.updater.Updater;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class ELCore extends JavaPlugin {

    private static ELCore context;

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
        new CoreConfiguration();
        new DBPool("jdbc:mysql://" + CoreConfiguration.Database_Host + ":" + CoreConfiguration.Database_Port + "/" + CoreConfiguration.Database_Database, CoreConfiguration.Database_Username, CoreConfiguration.Database_Password);
        new Updater(context);
        //new InternalPlayerCache();
        new LanguageManager();
        //cache = new UtilCache();
        //this.getServer().getPluginManager().registerEvents(new onPlayerJoin(), plugin);
        //this.getServer().getPluginManager().registerEvents(new onPlayerQuit(), plugin);
        //this.getServer().getPluginManager().registerEvents(new onPlayerChat(), plugin);
        //this.getServer().getPluginManager().registerEvents(new onPlayerCommandPreProcess(), plugin);
        this.getServer().getPluginManager().registerEvents(new LagMeter(context), context);
        if(CoreConfiguration.MemoryFixer_Enabled) {
            MemoryFix.last_failed = false;
            MemoryFix.min_memory = CoreConfiguration.MemoryFixer_Min;
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
