package com.ellisiumx.elcore.scoreboard;

import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import com.ellisiumx.elcore.utils.UtilChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Iterator;

public class ScoreboardManager implements Listener {

    public static ScoreboardManager context;

    private HashMap<Player, PlayerScoreboard> _playerScoreboards = new HashMap<Player, PlayerScoreboard>();
    private HashMap<String, ScoreboardData> _scoreboards = new HashMap<String, ScoreboardData>();

    private String _title = "   FightCraft   ";
    private int _shineIndex;
    private boolean _shineDirection = true;

    public ScoreboardManager(JavaPlugin plugin) {
        context = this;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void playerJoin(PlayerJoinEvent event) {
        _playerScoreboards.put(event.getPlayer(), new PlayerScoreboard());
    }

    @EventHandler
    public void playerQuit(PlayerQuitEvent event) {
        _playerScoreboards.remove(event.getPlayer());
    }

    public void draw() {
        Iterator<Player> playerIterator = _playerScoreboards.keySet().iterator();

        while (playerIterator.hasNext()) {
            Player player = playerIterator.next();

            //Offline
            if (!player.isOnline()) {
                playerIterator.remove();
                continue;
            }

            _playerScoreboards.get(player).draw(this, player);
        }
    }


    public ScoreboardData getData(String scoreboardName, boolean create) {
        if (!create)
            return _scoreboards.get(scoreboardName);

        if (!_scoreboards.containsKey(scoreboardName))
            _scoreboards.put(scoreboardName, new ScoreboardData());

        return _scoreboards.get(scoreboardName);
    }

    @EventHandler
    public void updateTitle(UpdateEvent event) {
        if (event.getType() != UpdateType.FASTER)
            return;

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

        for (PlayerScoreboard ps : _playerScoreboards.values()) {
            ps.setTitle(out);
        }

        _shineIndex++;

        if (_shineIndex == _title.length() * 2) {
            _shineIndex = 0;
            _shineDirection = !_shineDirection;
        }
    }
}