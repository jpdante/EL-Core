package com.ellisiumx.elcore.memory;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.configuration.CoreConfiguration;
import com.ellisiumx.elcore.redis.ConnectionData;
import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.logging.Level;

public class MemoryFix implements Listener {
    private Field _intHashMap;
    public long min_memory = 1024;
    public boolean last_failed = false;

    public MemoryFix(Plugin plugin) {
        last_failed = false;
        min_memory = CoreConfiguration.MemoryFixer_Min;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void fixInventoryLeaks(UpdateEvent event) {
        if (event.getType() != UpdateType.SLOW) return;
        for (World world : Bukkit.getWorlds()) {
            for (Object tileEntity : ((CraftWorld) world).getHandle().tileEntityList) {
                if (tileEntity instanceof IInventory) {
                    ((IInventory) tileEntity).getViewers().removeIf(entity -> entity instanceof CraftPlayer && !((CraftPlayer) entity).isOnline());
                }
            }
        }
        CraftingManager.getInstance().lastCraftView = null;
        CraftingManager.getInstance().lastRecipe = null;
    }

    @EventHandler
    public void fixGarbageCollector(UpdateEvent event) {
        if (event.getType() != UpdateType.MIN_01) return;
        if (!CoreConfiguration.MemoryFixer_Enabled) return;
        if (((Runtime.getRuntime().freeMemory() / 1024) / 1024) < min_memory) {
            Bukkit.getLogger().log(Level.INFO, "############################################ Memory Fixer ############################################");
            Bukkit.getLogger().log(Level.INFO, "ELCore has detected that there is little memory in the system.");
            Bukkit.getLogger().log(Level.INFO, "ELCore will try to fix the problem by using the garbage collector.");
            if (last_failed) Bukkit.getLogger().log(Level.INFO, "XCore has detected that the last attempt failed.");
            Bukkit.getLogger().log(Level.INFO, "Before -> " + ((Runtime.getRuntime().freeMemory() / 1024) / 1024) + "MB");
            System.gc();
            System.runFinalization();
            Bukkit.getLogger().log(Level.INFO, "After  -> " + ((Runtime.getRuntime().freeMemory() / 1024) / 1024) + "MB");
            if (((Runtime.getRuntime().freeMemory() / 1024) / 1024) < min_memory) {
                Bukkit.getLogger().log(Level.INFO, "ELCore detected that the garbage collector did not take effect.");
                if (last_failed) {
                    Bukkit.getLogger().log(Level.INFO, "ELCore has detected that there have been two memory failures, the server will be restarted!");
                    Bukkit.shutdown();
                }
                last_failed = true;
            } else {
                Bukkit.getLogger().log(Level.INFO, "ELCore will try again in 1 minute and if it fails, it will shut down the server.");
            }
            Bukkit.getLogger().log(Level.INFO, "######################################################################################################");
        }
    }
}