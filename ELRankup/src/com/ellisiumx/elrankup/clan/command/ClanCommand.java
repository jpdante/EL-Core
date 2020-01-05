package com.ellisiumx.elrankup.clan.command;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.lang.LanguageDB;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elrankup.clan.ClanManager;
import com.ellisiumx.elrankup.machine.MachineManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class ClanCommand extends CommandBase {

    public ClanCommand(JavaPlugin plugin) {
        super(plugin, Rank.ALL, "clan", "c");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
        for (LanguageDB languageDB : LanguageManager.getLanguages()) {
            languageDB.insertTranslation("ClansCommands", "&6Clans commands");
            languageDB.insertTranslation("ClansCreateCommand", " &a/clan create [tag] [name] &8- &7Create a clan($1000)");
            languageDB.insertTranslation("ClansListCommand", " &a/clan list &8- &7List clans");
            languageDB.insertTranslation("ClansProfileCommand", " &a/clan profile [name] &8- &7See clan profile info");
            languageDB.insertTranslation("ClansPlayerCommand", " &a/clan player [player] &8- &7See player profile info");
            languageDB.insertTranslation("ClansRankCommand", " &a/clan rank &8- &7Clans rank");
            languageDB.insertTranslation("ClansAlliesCommand", " &a/clan allies &8- &7See clan allies");
            languageDB.insertTranslation("ClansRivalsCommand", " &a/clan rivals &8- &7See clan rivals");
            languageDB.insertTranslation("ClansMembersCommand", " &a/clan members [name] &8- &7See clan members");
            languageDB.insertTranslation("ClansFriendFireCommand", " &a/clan friendfire [enable/disable] &8- &7Enable/Disable friend fire");
            languageDB.insertTranslation("ClansAbandonCommand", " &a/clan abandon &8- &7Abandon clan");
        }
        if (LanguageManager.saveLanguages()) LanguageManager.reloadLanguages();
    }

    @Override
    public void execute(Player caller, String[] args) {
        if(args == null || args.length <= 0) {
            showCommands(caller);
            return;
        }
        if(args[0].equalsIgnoreCase("create")) {
            if(args.length != 3) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansCreateCommand"));
                return;
            }
            ClanManager.context.createClan(caller, args[1], args[2]);
        } else if(args[0].equalsIgnoreCase("list")) {
            if(args.length != 1) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansListCommand"));
                return;
            }
        } else if(args[0].equalsIgnoreCase("profile")) {
            if(args.length != 2) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansProfileCommand"));
                return;
            }
        } else if(args[0].equalsIgnoreCase("player")) {
            if(args.length != 2) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansPlayerCommand"));
                return;
            }
        } else if(args[0].equalsIgnoreCase("rank")) {
            if(args.length != 1) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansRankCommand"));
                return;
            }
        } else if(args[0].equalsIgnoreCase("allies")) {
            if(args.length != 1) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansAlliesCommand"));
                return;
            }
        } else if(args[0].equalsIgnoreCase("rivals")) {
            if(args.length != 1) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansRivalsCommand"));
                return;
            }
        } else if(args[0].equalsIgnoreCase("members")) {
            if(args.length != 2) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansMembersCommand"));
                return;
            }
        } else if(args[0].equalsIgnoreCase("friendfire")) {
            if(args.length != 2) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansFriendFireCommand"));
                return;
            }
        } else if(args[0].equalsIgnoreCase("abandon")) {
            if(args.length != 1) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansAbandonCommand"));
                return;
            }
        }
    }

    private void showCommands(Player player) {
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansCommands"));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansCreateCommand"));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansListCommand"));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansProfileCommand"));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansPlayerCommand"));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansRankCommand"));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansAlliesCommand"));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansRivalsCommand"));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansMembersCommand"));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansFriendFireCommand"));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansAbandonCommand"));
    }
}
