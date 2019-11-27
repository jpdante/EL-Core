package com.ellisiumx.elcore.command;

import com.ellisiumx.elcore.permissions.Rank;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public interface ICommand {
    void setCommandCenter(CommandCenter commandCenter);

    void execute(Player caller, String[] args);

    Collection<String> aliases();

    void setAliasUsed(String name);

    Rank getRequiredRank();

    Rank[] getSpecificRanks();

    List<String> onTabComplete(CommandSender sender, String commandLabel, String[] args);
}
