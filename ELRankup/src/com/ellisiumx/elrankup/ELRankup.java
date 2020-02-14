package com.ellisiumx.elrankup;

import com.ellisiumx.elcore.database.DBPool;
import com.ellisiumx.elcore.explosion.Explosion;
import com.ellisiumx.elcore.scoreboard.ScoreboardData;
import com.ellisiumx.elcore.scoreboard.ScoreboardManager;
import com.ellisiumx.elcore.utils.UtilTabTitle;
import com.ellisiumx.elrankup.antilogout.AntiLogoutManager;
import com.ellisiumx.elrankup.autolapis.AutoLapisManager;
import com.ellisiumx.elrankup.cash.CashManager;
import com.ellisiumx.elrankup.chat.ChatManager;
import com.ellisiumx.elrankup.clan.ClanManager;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.crate.CrateManager;
import com.ellisiumx.elrankup.drop.DropManager;
import com.ellisiumx.elrankup.economy.EconomyManager;
import com.ellisiumx.elrankup.essentials.*;
import com.ellisiumx.elrankup.gamemode.*;
import com.ellisiumx.elrankup.god.GodManager;
import com.ellisiumx.elrankup.kit.KitManager;
import com.ellisiumx.elrankup.machine.MachineManager;
import com.ellisiumx.elrankup.mapedit.MapEditManager;
import com.ellisiumx.elrankup.mine.MineReset;
import com.ellisiumx.elrankup.rankup.RankupManager;
import com.ellisiumx.elrankup.spawner.SpawnerManager;
import com.ellisiumx.elrankup.vanish.VanishManager;
import com.ellisiumx.elrankup.warp.WarpManager;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public class ELRankup extends JavaPlugin implements Listener {
    private static ELRankup context;
    private ScoreboardData data;

    @Override
    public void onLoad() {
        context = this;
        this.getLogger().log(Level.INFO, "Loaded!");
    }

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        saveDefaultConfig();
        reloadConfig();
        new RankupConfiguration();
        DBPool.registerDataSource("rankup", "elrankup");
        data = ScoreboardManager.getContext().getData("default", true);
        data.write("aaa yes");
        Explosion.SetDebris(false);
        Explosion.SetLiquidDamage(false);
        Explosion.SetRegenerate(false);
        Explosion.SetTemporaryDebris(false);
        Explosion.SetTNTSpread(false);
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
        new CrateManager(context);
        new CashManager(context);
        new ScoreboardManager(context);
        new RankupManager(context);
        new SpawnerManager(context);
        new KitManager(context);
        new WarpManager(context);
        new ChatManager(context);
        new DropManager(context);
        new AntiLogoutManager(context);
        new AutoLapisManager(context);
        // Commands
        new SetSpawnCommand(context);
        new SetWorldSpawnCommand(context);
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
        new GiveCommand(context);
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

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        if(event.getTo().getY() < -10) {
            event.getPlayer().teleport(event.getTo().getWorld().getSpawnLocation());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
        UtilTabTitle.doHeaderAndFooter(event.getPlayer(), RankupConfiguration.TabHeader, RankupConfiguration.TabFooter);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onHungerChange(FoodLevelChangeEvent event) {
        event.setFoodLevel(20);
    }

    public static ELRankup getContext() {
        return context;
    }
}
