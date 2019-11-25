package com.ellisiumx.elrankup;

import com.ellisiumx.elrankup.configuration.RankupConfiguration;
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
        this.getServer().setWhitelist(true);
        saveDefaultConfig();
        reloadConfig();
        new RankupConfiguration();
        new MineReset(context);
        MineReset.start();
        this.getServer().setWhitelist(false);
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
