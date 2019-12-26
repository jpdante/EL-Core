package com.ellisiumx.elrankup;

import com.ellisiumx.elcore.database.DBPool;
import com.ellisiumx.elrankup.clan.ClanManager;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.economy.EconomyManager;
import com.ellisiumx.elrankup.essentials.*;
import com.ellisiumx.elrankup.game.RainDisabler;
import com.ellisiumx.elrankup.gamemode.*;
import com.ellisiumx.elrankup.god.GodManager;
import com.ellisiumx.elrankup.machine.MachineManager;
import com.ellisiumx.elrankup.mapedit.MapEditManager;
import com.ellisiumx.elrankup.mine.MineReset;
import com.ellisiumx.elrankup.vanish.VanishManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class ELRankup extends JavaPlugin {
    private static ELRankup context;

    @Override
    public void onLoad() {
        context = this;
        this.getLogger().log(Level.INFO, "Loaded!");
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadConfig();
        new RankupConfiguration();
        DBPool.registerDataSource("rankup", "elrankup");
        if(RankupConfiguration.MinesEnabled) {
            this.getLogger().log(Level.INFO, "Starting Mine Reseter...");
            new MineReset(context);
            MineReset.start();
        }
        new EconomyManager(context);
        new MapEditManager(context);
        new VanishManager(context);
        new GodManager(context);
        new MachineManager(context);
        new ClanManager(context);
        new RainDisabler(context);
        // Commands
        new PingCommand(context);
        new ResetCommand(context);
        new SuicideCommand(context);
        new ListCommand(context);
        new TopCommand(context);
        new SpeedCommand(context);
        new FlyCommand(context);
        new GamemodeCommand(context);
        new GamemodeSurvivalCommand(context);
        new GamemodeAdventureCommand(context);
        new GamemodeSpectatorCommand(context);
        new GamemodeCreativeCommand(context);
        new ClearChatCommand(context);
        new ClearInventoryCommand(context);
        new TimeCommand(context);
        this.getLogger().log(Level.INFO, "Enabled!");
    }

    @Override
    public void onDisable() {
        this.getLogger().log(Level.INFO, "Unloading...");
        MineReset.stop();
        /*if(config.Database_Enabled) {
            dataManager.shutdown();
        }
        if(config.Redis_Enabled) {
            RedisManager.getContext().Disconnect();
        }*/
        this.getLogger().log(Level.INFO, "Unloaded!");
    }

    public static ELRankup getContext() {
        return context;
    }
}
