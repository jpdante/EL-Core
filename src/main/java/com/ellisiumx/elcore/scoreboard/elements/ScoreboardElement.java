package com.ellisiumx.elcore.scoreboard.elements;

import java.util.ArrayList;

import com.ellisiumx.elcore.scoreboard.ScoreboardManager;
import org.bukkit.entity.Player;

public abstract class ScoreboardElement
{
	public abstract ArrayList<String> GetLines(ScoreboardManager manager, Player player);
}
