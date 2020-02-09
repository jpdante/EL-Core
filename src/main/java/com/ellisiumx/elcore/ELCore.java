package com.ellisiumx.elcore;

import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.blockrestore.BlockRestore;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.configuration.CoreConfiguration;
import com.ellisiumx.elcore.database.DBPool;
import com.ellisiumx.elcore.explosion.Explosion;
import com.ellisiumx.elcore.hologram.HologramManager;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.memory.MemoryFix;
import com.ellisiumx.elcore.monitor.LagMeter;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elcore.punish.PunishSystem;
import com.ellisiumx.elcore.recharge.Recharge;
import com.ellisiumx.elcore.redis.RedisManager;
import com.ellisiumx.elcore.scoreboard.ScoreboardManager;
import com.ellisiumx.elcore.timing.TimingManager;
import com.ellisiumx.elcore.updater.Updater;
import net.minecraft.server.v1_8_R3.ScoreboardBaseCriteria;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Score;

import java.util.logging.Level;

public class ELCore extends JavaPlugin {

    private static ELCore context;

    @Override
    public void onLoad() {
        context = this;
        this.getLogger().log(Level.INFO, "Loaded!");
    }

    @Override
    public void onEnable() {
        this.getServer().setWhitelist(true);
        saveDefaultConfig();
        reloadConfig();
        // REQUIRED
        new Updater(context);
        new TimingManager(context);
        new CoreConfiguration();
        new DBPool("jdbc:mysql://" + CoreConfiguration.Database_Host, CoreConfiguration.Database_Username, CoreConfiguration.Database_Password);
        new RedisManager();
        new CoreClientManager(context);
        new PreferencesManager(context);
        //new PunishSystem(context);
        new LanguageManager();
        new HologramManager(context);
        new ScoreboardManager(context);
        new BlockRestore(context);
        new Explosion(context);
        new Recharge(context);
        new CommandCenter(context);
        // UTILS
        new MemoryFix(context);
        new LagMeter(context);
        this.getServer().setWhitelist(false);
        this.getLogger().log(Level.INFO, "Enabled!");
    }

    @Override
    public void onDisable() {
        this.getLogger().log(Level.INFO, "Unloading...");
        /*if(config.Database_Enabled) {
            dataManager.shutdown();
        }
        if(config.Redis_Enabled) {
            RedisManager.getContext().Disconnect();
        }*/
        this.getLogger().log(Level.INFO, "Unloaded!");
    }

    public static ELCore getContext() {
        return context;
    }
}
