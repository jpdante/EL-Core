package com.ellisiumx.elrankup;

import com.ellisiumx.elcore.database.DBPool;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.economy.EconomyManager;
import com.ellisiumx.elrankup.mine.MineReset;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class ELRankup extends JavaPlugin {
    private static ELRankup context;

    @Override
    public void onLoad() {
        this.getLogger().log(Level.INFO, "[ELRankup] Loading...");
        context = this;
        this.getLogger().log(Level.INFO, "[ELRankup] Loaded!");
    }

    @Override
    public void onEnable() {
        this.getLogger().log(Level.INFO, "[ELRankup] Enabling...");
        saveDefaultConfig();
        reloadConfig();
        new RankupConfiguration();
        DBPool.registerDataSource("rankup", "elrankup");
        if(RankupConfiguration.MinesEnabled) {
            this.getLogger().log(Level.INFO, "[ELRankup] Starting Mine Reseter...");
            new MineReset(context);
            MineReset.start();
        }
        new EconomyManager(context);
        this.getLogger().log(Level.INFO, "[ELRankup] Enabled!");
    }

    @Override
    public void onDisable() {
        this.getLogger().log(Level.INFO, "[ELRankup] Unloading...");
        MineReset.stop();
        /*if(config.Database_Enabled) {
            dataManager.shutdown();
        }
        if(config.Redis_Enabled) {
            RedisManager.getContext().Disconnect();
        }*/
        this.getLogger().log(Level.INFO, "[ELRankup] Unloaded!");
    }

    public static ELRankup getContext() {
        return context;
    }
}
