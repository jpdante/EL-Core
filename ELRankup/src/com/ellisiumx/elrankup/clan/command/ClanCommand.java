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
            languageDB.insertTranslation("ClansListTitle", "&a-=-= &bClan List %PageNumber% &a=-=-");
            languageDB.insertTranslation("ClansListRowTitle", "&e   Name                           Tag   KDR   Rivals   Allies");
            languageDB.insertTranslation("ClansListRow",      "#%Index% &f%ClanName% &8- &a%ClanTag% &8- &e%ClanKDR% &8- &e%ClanRivals% &8- &e%ClanAllies%");
            languageDB.insertTranslation("ClansParseIntError", " &cMake sure you enter a valid number!");
            languageDB.insertTranslation("ClansIntUnderflow", " &cNumber must be greater than or equal to 1");
            languageDB.insertTranslation("ClansIntOverflow", " &cYou are already at the bottom of the list!");
            languageDB.insertTranslation("ClansListFoot", "&aTo see more clans use &b/clan list %PageNumber%");
        }
        if (LanguageManager.saveLanguages()) LanguageManager.reloadLanguages();
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
            int index = 0;
            if (args.length == 2) {
                try {
                    index = Integer.parseInt(args[1]) - 1;
                } catch (Exception ex) {
                    caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansParseIntError").replace('&', ChatColor.COLOR_CHAR));
                    return;
                }
            }
            if (index < 0) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansIntUnderflow").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            if (ClanManager.context.clans.size() - (index * 10) == -10) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansIntOverflow").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansListTitle")
                    .replaceAll("%PageNumber%", String.valueOf(index))
                    .replace('&', ChatColor.COLOR_CHAR)
            );
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansListRowTitle").replace('&', ChatColor.COLOR_CHAR));
            for (int i = index * 10; i < ClanManager.context.clans.size(); i++) {
                Clan clan = ClanManager.context.clans.get(i);
                StringBuilder clanName;
                if(clan.name.length() > 25) {
                    clanName = new StringBuilder(clan.name.substring(0, 25));
                } else {
                    clanName = new StringBuilder(clan.name);
                }
                if (clan.name.length() < 24) {
                    for (int j = 0; j < 25 - clan.name.length(); j++) {
                        clanName.append(" ");
                    }
                }
                StringBuilder tag = new StringBuilder(clan.colorTag);
                if (clan.tag.length() < 3) {
                    for (int j = 0; j < 3 - clan.tag.length(); j++) {
                        tag.append(" ");
                    }
                }
                caller.sendMessage(
                        LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansListRow")
                                .replaceAll("%Index%", String.valueOf(i))
                                .replaceAll("%ClanName%", clanName.toString())
                                .replaceAll("%ClanTag%", tag.toString())
                                .replaceAll("%ClanKDR%", String.format("%.1f", clan.kdr))
                                .replaceAll("%ClanRivals%", "")
                                .replaceAll("%ClanAllies%", "")
                                .replace('&', ChatColor.COLOR_CHAR)
                );
            }
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansListFoot")
                    .replaceAll("%PageNumber%", String.valueOf(index + 2))
                    .replace('&', ChatColor.COLOR_CHAR)
            );
        } else if (args[0].equalsIgnoreCase("profile")) {
            if (args.length != 2) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansProfileCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
        } else if (args[0].equalsIgnoreCase("player")) {
            if (args.length != 2) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansPlayerCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
        } else if (args[0].equalsIgnoreCase("rank")) {
            if (args.length != 1) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansRankCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
        } else if (args[0].equalsIgnoreCase("allies")) {
            if (args.length != 1) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansAlliesCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
        } else if (args[0].equalsIgnoreCase("rivals")) {
            if (args.length != 1) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansRivalsCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
        } else if (args[0].equalsIgnoreCase("members")) {
            if (args.length != 2) {
                caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansMembersCommand").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
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
    }
}
