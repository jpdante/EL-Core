package com.ellisiumx.elrankup.rankup;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.account.CoreClient;
import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.explosion.Explosion;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.scoreboard.ScoreboardData;
import com.ellisiumx.elcore.scoreboard.ScoreboardManager;
import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import com.ellisiumx.elcore.utils.UtilPlayer;
import com.ellisiumx.elcore.utils.UtilServer;
import com.ellisiumx.elrankup.chat.ChatManager;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.rankup.command.RankupCommand;
import com.ellisiumx.elrankup.rankup.repository.RankupRepository;
import net.minecraft.server.v1_8_R3.Scoreboard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class RankupManager implements Listener {

    public static RankupManager context;
    private RankupRepository repository;
    private HashMap<String, RankLevel> playerRanks;

    public RankupManager(JavaPlugin plugin) {
        context = this;
        repository = new RankupRepository(plugin);
        playerRanks = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        new RankupCommand(plugin);
    }

    public static RankLevel get(Player player) {
        return context.playerRanks.get(player.getName());
    }

    public static RankLevel get(String playerName) {
        return context.playerRanks.get(playerName);
    }

    public void rankupPlayer(Player player) {
        
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            String rank = repository.getRank(CoreClientManager.get(event.getPlayer()).getAccountId(), RankupConfiguration.DefaultRank);
            playerRanks.put(event.getPlayer().getName(), RankupConfiguration.getRankLevelByName(rank));
            ChatManager.regenerateTags(event.getPlayer());
        });
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerRanks.remove(event.getPlayer().getName());
    }

    @EventHandler
    public void onTimerElapsed(UpdateEvent event) {
        if (event.getType() != UpdateType.SLOWER) return;
        for(Player player : UtilServer.getPlayers()) {
            //ScoreboardManager.getContext().
        }
    }
}