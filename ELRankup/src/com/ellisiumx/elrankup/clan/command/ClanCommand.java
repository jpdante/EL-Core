package com.ellisiumx.elrankup.clan.command;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.lang.LanguageDB;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elrankup.clan.Clan;
import com.ellisiumx.elrankup.clan.ClanManager;
import com.ellisiumx.elrankup.machine.MachineManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;

public class ClanCommand extends CommandBase {

    public ClanCommand(JavaPlugin plugin) {
        super(plugin, Rank.ALL, "clan", "c");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        if (args == null || args.length <= 0) {
            showCommands(caller);
            return;
        }
        if (args[0].equalsIgnoreCase("create")) {
            if (args.length != 3) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansCreateCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            ClanManager.context.createClan(caller, args[1], args[2]);
        } else if (args[0].equalsIgnoreCase("list")) {
            if (args.length > 2) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansListCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            ClanManager.context.listClans(caller, args);
        } else if (args[0].equalsIgnoreCase("stats")) {
            if (args.length != 2) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansProfileCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            ClanManager.context.clanStats(caller, args[1]);
        } else if (args[0].equalsIgnoreCase("player")) {
            if (args.length != 2) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansPlayerCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            ClanManager.context.clanPlayer(caller, args[1]);
        } else if (args[0].equalsIgnoreCase("setrank")) {
            if (args.length != 3) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansRankCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            ClanManager.context.setRank(caller, args[1], args[2]);
        } else if (args[0].equalsIgnoreCase("inviteallie")) {
            if (args.length != 2) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansInviteAllieCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            ClanManager.context.inviteAllie(caller, args[1]);
        } else if (args[0].equalsIgnoreCase("addrival")) {
            if (args.length != 2) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansAddRivalCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            ClanManager.context.addRival(caller, args[1]);
        } else if (args[0].equalsIgnoreCase("removerival")) {
            if (args.length != 2) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansRemoveRivalCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            ClanManager.context.removeRival(caller, args[1]);
        } else if (args[0].equalsIgnoreCase("members")) {
            if (args.length != 2) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansMembersCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            ClanManager.context.clanMembers(caller, args[1]);
        } else if (args[0].equalsIgnoreCase("friendfire")) {
            if (args.length != 2) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansFriendFireCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
        } else if (args[0].equalsIgnoreCase("abandon")) {
            if (args.length != 1) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansAbandonCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            ClanManager.context.abandonClan(caller, false);
        } else if (args[0].equalsIgnoreCase("invite")) {
            if (args.length != 2) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansInviteCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            ClanManager.context.invitePlayer(caller, args[1]);
        }
    }

    private void showCommands(Player player) {
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansCommands").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansCreateCommand").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansListCommand").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansProfileCommand").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansPlayerCommand").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansRankCommand").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansAlliesCommand").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansRivalsCommand").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansMembersCommand").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansFriendFireCommand").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansAbandonCommand").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansInviteCommand").replace('&', ChatColor.COLOR_CHAR));
    }
}
