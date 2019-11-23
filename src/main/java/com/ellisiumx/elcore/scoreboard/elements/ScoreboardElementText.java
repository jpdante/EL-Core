package com.ellisiumx.elcore.scoreboard.elements;

import java.util.ArrayList;

import com.ellisiumx.elcore.scoreboard.ScoreboardManager;
import org.bukkit.entity.Player;

public class ScoreboardElementText extends ScoreboardElement {
    private String _line;

    public ScoreboardElementText(String line) {
        _line = line;
    }

    @Override
    public ArrayList<String> GetLines(ScoreboardManager manager, Player player) {
        ArrayList<String> orderedScores = new ArrayList<String>();
        orderedScores.add(_line);
        return orderedScores;
    }

}
