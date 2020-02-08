package com.ellisiumx.elrankup.drop;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.lang.LanguageDB;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elrankup.chat.PlayerChat;
import com.ellisiumx.elrankup.chat.command.ChangeChannelCommand;
import com.ellisiumx.elrankup.chat.command.TellCommand;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.crate.CrateManager;
import com.ellisiumx.elrankup.drop.repository.DropRepository;
import com.ellisiumx.elrankup.economy.repository.EconomyRepository;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class DropManager implements Listener {

    public static DropManager context;
    public static DropRepository repository;

    public HashMap<String, DropPickaxe> dropPickaxes;
    public HashMap<String, PlayerDrops> playerDrops;

    public DropManager(JavaPlugin plugin) {
        context = this;
        repository = new DropRepository(plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        dropPickaxes = new HashMap<>();
        playerDrops = new HashMap<>();
        new ChangeChannelCommand(plugin);
        new TellCommand(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void OnBlockBreak(BlockBreakEvent event) {
        if(event.getBlock().getType() != Material.EMERALD_BLOCK) return;
        //Block block = event.getBlock();
        event.setCancelled(true);
        event.setExpToDrop(0);
        event.getBlock().setType(Material.AIR);
        event.getPlayer().getInventory().addItem(new ItemStack(Material.EMERALD_BLOCK));
        PlayerDrops pd = playerDrops.get(event.getPlayer().getName());
        pd.setDrops(pd.getDrops() + 1L);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            PlayerDrops data = repository.getPlayerDrops(CoreClientManager.get(event.getPlayer()).getAccountId());
            data.setPlayer(event.getPlayer());
            playerDrops.put(event.getPlayer().getName(), data);
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerDrops.remove(event.getPlayer().getName());
    }

}
