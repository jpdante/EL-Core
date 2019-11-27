package com.ellisiumx.elcore.command;

import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.recharge.Recharge;
import com.ellisiumx.elcore.utils.UtilServer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class CommandBase implements ICommand {
    private Rank requiredRank;
    private Rank[] specificRank;

    private List<String> aliases;

    protected JavaPlugin plugin;
    protected String aliasUsed;
    protected CommandCenter commandCenter;

    public CommandBase(JavaPlugin plugin, Rank requiredRank, String... aliases) {
        this.plugin = plugin;
        this.requiredRank = requiredRank;
        this.aliases = Arrays.asList(aliases);
    }

    public CommandBase(JavaPlugin plugin, Rank requiredRank, Rank[] specificRank, String... aliases) {
        this.plugin = plugin;
        this.requiredRank = requiredRank;
        this.specificRank = specificRank;

        this.aliases = Arrays.asList(aliases);
    }

    public Collection<String> aliases() {
        return aliases;
    }

    public void setAliasUsed(String alias) {
        aliasUsed = alias;
    }

    public Rank getRequiredRank() {
        return requiredRank;
    }

    public Rank[] getSpecificRanks() {
        return specificRank;
    }

    public void setCommandCenter(CommandCenter commandCenter) {
        this.commandCenter = commandCenter;
    }

    protected void resetCommandCharge(Player caller) {
        Recharge.recharge(caller, "Command");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String commandLabel, String[] args) {
        return null;
    }

    protected List<String> getMatches(String start, List<String> possibleMatches) {
        List<String> matches = new ArrayList<String>();

        for (String possibleMatch : possibleMatches) {
            if (possibleMatch.toLowerCase().startsWith(start.toLowerCase()))
                matches.add(possibleMatch);
        }

        return matches;
    }

    @SuppressWarnings("rawtypes")
    protected List<String> getMatches(String start, Enum[] numerators) {
        List<String> matches = new ArrayList<String>();

        for (Enum e : numerators) {
            String s = e.toString();
            if (s.toLowerCase().startsWith(start.toLowerCase()))
                matches.add(s);
        }

        return matches;
    }

    protected List<String> getPlayerMatches(Player sender, String start) {
        List<String> matches = new ArrayList<String>();

        for (Player player : UtilServer.getPlayers()) {
            if (sender.canSee(player) && player.getName().toLowerCase().startsWith(start.toLowerCase())) {
                matches.add(player.getName());
            }
        }

        return matches;
    }

}

