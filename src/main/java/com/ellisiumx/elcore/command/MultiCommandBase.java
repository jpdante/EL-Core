package com.ellisiumx.elcore.command;

import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.permissions.Rank;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class MultiCommandBase extends CommandBase {
    protected HashMap<String, ICommand> Commands;

    public MultiCommandBase(JavaPlugin plugin, Rank rank, String... aliases) {
        super(plugin, rank, aliases);

        Commands = new HashMap<String, ICommand>();
    }

    public MultiCommandBase(JavaPlugin plugin, Rank rank, Rank[] specificRanks, String... aliases) {
        super(plugin, rank, specificRanks, aliases);

        Commands = new HashMap<String, ICommand>();
    }

    public void addCommand(ICommand command) {
        for (String commandRoot : command.aliases()) {
            Commands.put(commandRoot, command);
            command.setCommandCenter(commandCenter);
        }
    }

    @Override
    public void execute(Player caller, String[] args) {
        String commandName = null;
        String[] newArgs = null;
        if (args != null && args.length > 0) {
            commandName = args[0];
            if (args.length > 1) {
                newArgs = new String[args.length - 1];
                for (int i = 0; i < newArgs.length; i++) {
                    newArgs[i] = args[i + 1];
                }
            }
        }
        ICommand command = Commands.get(commandName);
        if (command != null && CoreClientManager.get(caller).getRank().has(caller, command.getRequiredRank(), command.getSpecificRanks(), true)) {
            command.setAliasUsed(commandName);
            command.execute(caller, newArgs);
        } else {
            help(caller, args);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String commandLabel, String[] args) {
        if (args.length == 1) {
            List<String> possibleMatches = new ArrayList<String>();
            for (ICommand command : Commands.values()) {
                possibleMatches.addAll(command.aliases());
            }
            return getMatches(args[0], possibleMatches);
        } else if (args.length > 1) {
            String commandName = args[0];
            ICommand command = Commands.get(commandName);
            if (command != null) {
                return command.onTabComplete(sender, commandLabel, args);
            }
        }
        return null;
    }

    protected abstract void help(Player caller, String[] args);
}

