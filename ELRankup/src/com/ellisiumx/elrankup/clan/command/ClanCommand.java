package com.ellisiumx.elrankup.clan.command;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elrankup.clan.ClanManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

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
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansStatsCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            ClanManager.context.clanStats(caller, args[1]);
        } else if (args[0].equalsIgnoreCase("player")) {
            if (args.length > 3) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansPlayerCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            if (args.length == 1) {
                ClanManager.context.clanPlayer(caller, null);
            } else if (args.length == 2) {
                ClanManager.context.clanPlayer(caller, args[1]);
            }
        } else if (args[0].equalsIgnoreCase("setrank")) {
            if (args.length  == 1 || args.length > 4) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansSetRankCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            if(args.length == 2) ClanManager.context.setRank(caller, null, args[1]);
            else if(args.length == 3) ClanManager.context.setRank(caller, args[1], args[2]);
        } else if (args[0].equalsIgnoreCase("inviteallie")) {
            if (args.length != 2) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansInviteAllieCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            ClanManager.context.inviteAllie(caller, args[1]);
        } else if (args[0].equalsIgnoreCase("allie")) {
            if (args.length != 2) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansAllieCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            if(args[1].equalsIgnoreCase("accept")) ClanManager.context.acceptAllieInvite(caller);
            else if(args[1].equalsIgnoreCase("reject")) ClanManager.context.rejectAllieInvite(caller);
            else caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansAllieCommand").replace('&', ChatColor.COLOR_CHAR));
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
            if (args.length > 3) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansMembersCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            if(args.length == 1) ClanManager.context.clanMembers(caller);
            else if(args.length == 2) ClanManager.context.clanMembers(caller, args[1]);
        } else if (args[0].equalsIgnoreCase("friendfire")) {
            if (args.length != 2) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansFriendFireCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
        } else if (args[0].equalsIgnoreCase("abandon")) {
            if (args.length > 2) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansAbandonCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            if(args.length == 1) ClanManager.context.abandonClan(caller, false);
            else if (args[1].equalsIgnoreCase("true")) ClanManager.context.abandonClan(caller, true);
        } else if (args[0].equalsIgnoreCase("invite")) {
            if (args.length != 2) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansInviteCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            ClanManager.context.invitePlayer(caller, args[1]);
        } else if (args[0].equalsIgnoreCase("accept")) {
            if (args.length != 1) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansAcceptCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            ClanManager.context.acceptPlayerInvite(caller);
        } else if (args[0].equalsIgnoreCase("reject")) {
            if (args.length != 1) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansRejectCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            ClanManager.context.rejectPlayerInvite(caller);
        } else if (args[0].equalsIgnoreCase("chat")) {
            if(args.length == 1) return;
            StringBuilder message = new StringBuilder();
            for(int i = 1; i < args.length; i++) {
                message.append(args[i]).append(" ");
            }
            ClanManager.context.clanChat(caller, message.toString());
        }
    }

    private void showCommands(Player player) {
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansCommands").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansCreateCommand").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansListCommand").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansStatsCommand").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansPlayerCommand").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansSetRankCommand").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansInviteAllieCommand").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansAddRivalCommand").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansRemoveRivalCommand").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansMembersCommand").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansFriendFireCommand").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansAbandonCommand").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansInviteCommand").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansAcceptCommand").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansRejectCommand").replace('&', ChatColor.COLOR_CHAR));
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansAllieCommand").replace('&', ChatColor.COLOR_CHAR));
    }
}
