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
    private String scoreboardData = "default";

    private ScoreboardManager manager;
    private Scoreboard scoreboard;
    private Objective sideObjective;

    private ArrayList<String> currentLines = new ArrayList<String>();

    private String[] teamNames;

    public PlayerScoreboard(ScoreboardManager manager) {
        this.manager = manager;
    }

    private void addTeams(Player player) {
        for (Rank rank : Rank.values()) {
            if (rank != Rank.ALL)
                scoreboard.registerNewTeam(rank.Name).setPrefix(rank.getTag(true, true) + ChatColor.RESET + " ");
            else
                scoreboard.registerNewTeam(rank.Name).setPrefix("");
        }
        scoreboard.registerNewTeam("Party").setPrefix(ChatColor.LIGHT_PURPLE + UtilChat.Bold + "Party" + ChatColor.RESET + " ");
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
            //Add Other to Self
            scoreboard.getTeam(otherRankName).addPlayer(otherPlayer);
            //Add Self to Other
            otherPlayer.getScoreboard().getTeam(rankName).addPlayer(player);
        }
    }

    private ScoreboardData getData() {
        ScoreboardData data = manager.getData(scoreboardData, false);
        if (data != null) return data;
        scoreboardData = "default";
        return manager.getData(scoreboardData, false);
    }

    public void draw(ScoreboardManager manager, Player player) {
        ScoreboardData data = getData();
        if (data == null) return;
        for (int i = 0; i < data.getLines(manager, player).size(); i++) {
            //Get New Line
            String newLine = data.getLines(manager, player).get(i);
            //Check if Unchanged
            if (currentLines.size() > i) {
                String oldLine = currentLines.get(i);
                if (oldLine.equals(newLine)) continue;
            }
            //Update
            Team team = scoreboard.getTeam(teamNames[i]);
            if (team == null) {
                System.out.println("Scoreboard Error: Line Team Not Found!");
                return;
            }
            //Set Line Prefix/Suffix
            team.setPrefix(newLine.substring(0, Math.min(newLine.length(), 16)));
            team.setSuffix(ChatColor.getLastColors(newLine) + newLine.substring(team.getPrefix().length(), Math.min(newLine.length(), 32)));

            //Line
            sideObjective.getScore(teamNames[i]).setScore(15 - i);
        }
        //Hide Old Unused
        if (currentLines.size() > data.getLines(manager, player).size()) {
            for (int i = data.getLines(manager, player).size(); i < currentLines.size(); i++) {
                scoreboard.resetScores(teamNames[i]);
            }
        }
        //Save New State
        currentLines = data.getLines(manager, player);
    }

    public void setTitle(String out) {
        sideObjective.setDisplayName(out);
    }

    public void assignScoreboard(Player player, ScoreboardData data) {
        //Scoreboard
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        //Side Obj
        sideObjective = scoreboard.registerNewObjective(player.getName() + UtilMath.r(999999999), "dummy");
        sideObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
        sideObjective.setDisplayName(UtilChat.Bold + "   FIGHTCRAFT   ");
        //Teams
        addTeams(player);
        //Create Line Teams - There will always be 16 teams, with static line allocations.
        teamNames = new String[16];
        for (int i = 0; i < 16; i++) {
            String teamName = ChatColor.COLOR_CHAR + "" + ("1234567890abcdefghijklmnopqrstuvwxyz".toCharArray())[i] + ChatColor.RESET;
            teamNames[i] = teamName;
            Team team = scoreboard.registerNewTeam(teamName);
            team.addEntry(teamName);
        }
        //
//		if (data.getDisplayRanks())
//		for (Player otherPlayer : Bukkit.getOnlinePlayers())
//		{
//			if (_clientManager.Get(otherPlayer) == null)
//				continue;
//
//			String rankName = _clientManager.Get(player).GetRank().Name;
//			String otherRankName = _clientManager.Get(otherPlayer).GetRank().Name;
//
//			if (!_clientManager.Get(player).GetRank().Has(Rank.ULTRA) && _donationManager.Get(player.getName()).OwnsUltraPackage())
//			{
//				rankName = Rank.ULTRA.Name;
//			}
//
//			if (!_clientManager.Get(otherPlayer).GetRank().Has(Rank.ULTRA) && _donationManager.Get(otherPlayer.getName()).OwnsUltraPackage())
//			{
//				otherRankName = Rank.ULTRA.Name;
//			}
//
//			//Add Other to Self
//			board.getTeam(otherRankName).addPlayer(otherPlayer);
//		}
        //Set Scoreboard
        player.setScoreboard(scoreboard);
    }
}
