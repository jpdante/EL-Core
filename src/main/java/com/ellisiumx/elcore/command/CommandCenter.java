package com.ellisiumx.elcore.command;

import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.recharge.Recharge;
import com.ellisiumx.elcore.utils.UtilMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class CommandCenter implements Listener {
    public static CommandCenter context;

    protected JavaPlugin plugin;
    protected HashMap<String, ICommand> commands;

    public CommandCenter(JavaPlugin instance) {
        context = this;
        plugin = instance;
        commands = new HashMap<>();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void OnPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String commandName = event.getMessage().substring(1);
        String[] args = null;
        if (commandName.contains(" ")) {
            commandName = commandName.split(" ")[0];
            args = event.getMessage().substring(event.getMessage().indexOf(' ') + 1).split(" ");
        }
        ICommand command = commands.get(commandName.toLowerCase());
        if (command != null) {
            event.setCancelled(true);
            if (CoreClientManager.get(event.getPlayer()).getRank().has(event.getPlayer(), command.getRequiredRank(), command.getSpecificRanks(), true)) {
                if (!Recharge.use(event.getPlayer(), "Command", 500, false, false)) {
                    event.getPlayer().sendMessage(UtilMessage.main("Command Center", "You can't spam commands that fast."));
                    return;
                }
                command.setAliasUsed(commandName.toLowerCase());
                command.execute(event.getPlayer(), args);
            }
        }
    }

    /*@EventHandler
    public void onTabComplete(TabCompleteEvent event) {
        ICommand command = Commands.get(event.getCommand().toLowerCase());

        if (command != null) {
            List<String> suggestions = command.onTabComplete(event.getSender(), event.getCommand(), event.getArgs());

            if (suggestions != null)
                event.setSuggestions(suggestions);
        }
    }*/

    public static void addCommand(ICommand command) {
        for (String commandRoot : command.aliases()) {
            context.commands.put(commandRoot.toLowerCase(), command);
            command.setCommandCenter(context);
        }
    }

    public static void removeCommand(ICommand command) {
        for (String commandRoot : command.aliases()) {
            context.commands.remove(commandRoot.toLowerCase());
            command.setCommandCenter(null);
        }
    }
}
