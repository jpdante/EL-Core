package com.ellisiumx.elcore.scoreboard;

import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import com.ellisiumx.elcore.utils.UtilChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Iterator;

public class ScoreboardManager implements Listener {

    private static ScoreboardManager context;

    private HashMap<Player, PlayerScoreboard> playerScoreboards = new HashMap<>();
    private HashMap<String, ScoreboardData> scoreboards = new HashMap<>();

    //Title
    private String _title = "   FIGHTCRAFT   ";
    private int _shineIndex;
    private boolean _shineDirection = true;

    public ScoreboardManager(JavaPlugin plugin) {
        context = this;
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        playerScoreboards.put(event.getPlayer(), new PlayerScoreboard(this));
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        playerScoreboards.remove(event.getPlayer());
    }

    public void draw() {
        Iterator<Player> playerIterator = playerScoreboards.keySet().iterator();
        while (playerIterator.hasNext()) {
            Player player = playerIterator.next();
            //Offline
            if (!player.isOnline()) {
                playerIterator.remove();
                continue;
            }
            playerScoreboards.get(player).draw(this, player);
        }
    }


    public ScoreboardData getData(String scoreboardName, boolean create) {
        if (!create) return scoreboards.get(scoreboardName);
        if (!scoreboards.containsKey(scoreboardName)) scoreboards.put(scoreboardName, new ScoreboardData());
        return scoreboards.get(scoreboardName);
    }

    @EventHandler
    public void updateTitle(UpdateEvent event) {
        if (event.getType() != UpdateType.FASTER) return;
        String out;
        if (_shineDirection) {
            out = UtilChat.cGold + UtilChat.Bold;
        } else {
            out = UtilChat.cWhite + UtilChat.Bold;
        }
        for (int i = 0; i < _title.length(); i++) {
            char c = _title.charAt(i);

            if (_shineDirection) {
                if (i == _shineIndex)
                    out += UtilChat.cYellow + UtilChat.Bold;

                if (i == _shineIndex + 1)
                    out += UtilChat.cWhite + UtilChat.Bold;
            } else {
                if (i == _shineIndex)
                    out += UtilChat.cYellow + UtilChat.Bold;

                if (i == _shineIndex + 1)
                    out += UtilChat.cGold + UtilChat.Bold;
            }

            out += c;
        }
        for (PlayerScoreboard ps : playerScoreboards.values()) {
            ps.setTitle(out);
        }
        _shineIndex++;
        if (_shineIndex == _title.length() * 2) {
            _shineIndex = 0;
            _shineDirection = !_shineDirection;
        }
    }

    public static ScoreboardManager getContext() {
        return context;
    }
}

