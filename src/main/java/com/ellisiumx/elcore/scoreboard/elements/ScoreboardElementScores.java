package com.ellisiumx.elcore.scoreboard.elements;

import java.util.ArrayList;
import java.util.HashMap;

import com.ellisiumx.elcore.scoreboard.ScoreboardManager;
import org.bukkit.entity.Player;

public class ScoreboardElementScores extends ScoreboardElement {
    private String key;
    private HashMap<String, Integer> scores;
    private boolean higherIsBetter;

    public ScoreboardElementScores(String key, String line, int value, boolean higherIsBetter) {
        scores = new HashMap<String, Integer>();
        this.key = key;
        AddScore(line, value);
        this.higherIsBetter = higherIsBetter;
    }

    @Override
    public ArrayList<String> GetLines(ScoreboardManager manager, Player player) {
        ArrayList<String> orderedScores = new ArrayList<String>();
        //Order Scores
        while (orderedScores.size() < scores.size()) {
            String bestKey = null;
            int bestScore = 0;
            for (String key : scores.keySet()) {
                if (orderedScores.contains(key)) continue;
                if (bestKey == null || (higherIsBetter && scores.get(key) >= bestScore) || (!higherIsBetter && scores.get(key) <= bestScore)) {
                    bestKey = key;
                    bestScore = scores.get(key);
                }
            }
            orderedScores.add(bestKey);
        }
        return orderedScores;
    }

    public boolean IsKey(String key) {
        return this.key.equals(key);
    }

    public void AddScore(String line, int value) {
        scores.put(line, value);
    }
}
