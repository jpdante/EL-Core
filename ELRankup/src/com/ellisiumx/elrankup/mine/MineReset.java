package com.ellisiumx.elrankup.mine;

import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.google.gson.internal.$Gson$Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class MineReset implements Listener {

    private static MineReset context;

    private List<MineData> mines;

    public MineReset(JavaPlugin plugin) {
        context = this;
        mines = RankupConfiguration.Mines;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public static void start() {
        for(MineData mineData : context.mines) {
            mineData.enabled = true;
        }
    }

    public static void stop() {
        for(MineData mineData : context.mines) {
            mineData.enabled = false;
        }
    }

    @EventHandler
    public void onUpdate(UpdateEvent event) {
        if (event.getType() != UpdateType.SEC) return;
        for(MineData mineData : mines) {
            if(!mineData.enabled) continue;
            mineData.currentDelay--;
            Bukkit.broadcastMessage(ChatColor.RED + "Resetting in " + mineData.currentDelay);
            if(mineData.currentDelay <= 0) {
                mineData.currentDelay = mineData.delay;
                mineData.fillMine();
            }
        }
    }

}
