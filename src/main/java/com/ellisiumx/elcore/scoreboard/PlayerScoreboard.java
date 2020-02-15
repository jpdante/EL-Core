package com.ellisiumx.elcore.scoreboard;

import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilMath;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;

public class PlayerScoreboard {

    private String _scoreboardData = "default";
    private Scoreboard _scoreboard;
    private Objective _sideObjective;
    private ArrayList<String> _currentLines = new ArrayList<String>();
    private String[] _teamNames;

    private void addTeams(Player player) {
        for (Rank rank : Rank.values()) {
            if (rank != Rank.ALL) _scoreboard.registerNewTeam(rank.Name).setPrefix(rank.getTag(true, true) + ChatColor.RESET + " ");
            else _scoreboard.registerNewTeam(rank.Name).setPrefix("");
        }
        _scoreboard.registerNewTeam("Party").setPrefix(ChatColor.LIGHT_PURPLE + UtilChat.Bold + "Party" + ChatColor.RESET + " ");
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            if (CoreClientManager.get(otherPlayer) == null) continue;
            String rankName = CoreClientManager.get(player).getRank().Name;
            String otherRankName = CoreClientManager.get(otherPlayer).getRank().Name;
            if (!CoreClientManager.get(player).getRank().has(Rank.MVP)) {
                rankName = Rank.MVP.Name;
            }
            if (!CoreClientManager.get(otherPlayer).getRank().has(Rank.MVP)) {
                otherRankName = Rank.MVP.Name;
            }
            _scoreboard.getTeam(otherRankName).addPlayer(otherPlayer);
            otherPlayer.getScoreboard().getTeam(rankName).addPlayer(player);
        }
    }

    private ScoreboardData getData() {
        ScoreboardData data = ScoreboardManager.context.getData(_scoreboardData, false);
        if (data != null) return data;
        _scoreboardData = "default";
        return ScoreboardManager.context.getData(_scoreboardData, false);
    }

    public void draw(ScoreboardManager manager, Player player) {
        ScoreboardData data = getData();
        if (data == null) return;
        for (int i = 0; i < data.getLines(manager, player).size(); i++) {
            String newLine = data.getLines(manager, player).get(i);
            if (_currentLines.size() > i) {
                String oldLine = _currentLines.get(i);
                if (oldLine.equals(newLine)) continue;
            }
            Team team = _scoreboard.getTeam(_teamNames[i]);
            if (team == null) {
                System.out.println("Scoreboard Error: Line Team Not Found!");
                return;
            }
            team.setPrefix(newLine.substring(0, Math.min(newLine.length(), 16)));
            team.setSuffix(ChatColor.getLastColors(newLine) + newLine.substring(team.getPrefix().length(), Math.min(newLine.length(), 32)));
            _sideObjective.getScore(_teamNames[i]).setScore(15 - i);
        }
        if (_currentLines.size() > data.getLines(manager, player).size()) {
            for (int i = data.getLines(manager, player).size(); i < _currentLines.size(); i++) {
                _scoreboard.resetScores(_teamNames[i]);
            }
        }
        _currentLines = data.getLines(manager, player);
    }

    public void setTitle(String out) {
        _sideObjective.setDisplayName(out);
    }

    public void assignScoreboard(Player player, ScoreboardData data) {
        _scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        _sideObjective = _scoreboard.registerNewObjective(player.getName() + UtilMath.r(999999999), "dummy");
        _sideObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        _sideObjective.setDisplayName(UtilChat.Bold + "   MINEPLEX   ");
        addTeams(player);
        _teamNames = new String[16];
        for (int i = 0; i < 16; i++) {
            String teamName = ChatColor.COLOR_CHAR + "" + ("1234567890abcdefghijklmnopqrstuvwxyz".toCharArray())[i] + ChatColor.RESET;
            _teamNames[i] = teamName;
            Team team = _scoreboard.registerNewTeam(teamName);
            team.addEntry(teamName);
        }
        player.setScoreboard(_scoreboard);
    }
}
