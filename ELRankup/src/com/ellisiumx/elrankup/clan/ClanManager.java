package com.ellisiumx.elrankup.clan;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.jsonchat.*;
import com.ellisiumx.elcore.lang.LanguageDB;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elcore.timing.TimingManager;
import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import com.ellisiumx.elcore.utils.UtilLog;
import com.ellisiumx.elcore.utils.UtilPlayer;
import com.ellisiumx.elrankup.chat.ChatManager;
import com.ellisiumx.elrankup.clan.command.ClanCommand;
import com.ellisiumx.elrankup.clan.repository.ClanRepository;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.economy.EconomyManager;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Level;

public class ClanManager implements Listener {

    public static ClanManager context;
    public ClanRepository repository;
    public ArrayList<Clan> clans;
    public HashMap<String, ClanPlayer> playerClans;
    public HashMap<String, ClanPlayerInvite> clanPlayerInvites;
    public HashMap<Integer, ClanAllieInvite> clanAllieInvites;
    public Stack<Clan> clanUpdateBuffer = new Stack<>();
    public Stack<ClanPlayer> playerUpdateBuffer = new Stack<>();
    public boolean initialized;

    public ClanManager(JavaPlugin plugin) {
        context = this;
        repository = new ClanRepository(plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            TimingManager.start("load clans");
            clans = repository.getClans();
            for (Clan clan : clans) {
                clan.members = repository.getClanMembers(clan.id);
                clan.calculateKdr();
            }
            TimingManager.stop("load clans");
            UtilLog.log(Level.INFO, "[Clans] " + clans.size() + " clans loaded from mysql.");
            initialized = true;
        });
        playerClans = new HashMap<>();
        clanPlayerInvites = new HashMap<>();
        clanAllieInvites = new HashMap<>();
        for (LanguageDB languageDB : LanguageManager.getLanguages()) {
            // Errors
            languageDB.insertTranslation("ClanNotEnoughMoney", "&cYou do not have enough money to create a clan!");
            languageDB.insertTranslation("ClanTagMaxOut", "&cThe clan tag cannot be longer than 3 characters!");
            languageDB.insertTranslation("ClanTagMaxOutWithColors", "&cClan tags cannot be longer than 25 characters in total along with the colors!");
            languageDB.insertTranslation("ClanNameMaxOut", "&cThe clan name cannot be longer than 50 characters!");
            languageDB.insertTranslation("ClanTagAlreadyExists", "&cA clan with that tag already exists!");
            languageDB.insertTranslation("ClanNameAlreadyExists", "&cA clan with that name already exists!");
            languageDB.insertTranslation("ClanTransactionFailure", "&cFailed to transfer, please try again later. %ErrorMessage%");
            languageDB.insertTranslation("ClansParticipationError", "&cYou need to join a clan to perform this action!");
            languageDB.insertTranslation("ClansNoPermission", "&cYou are not allowed to perform this action!");
            languageDB.insertTranslation("ClansInviteYourself", "&cYou cannot invite yourself!");
            languageDB.insertTranslation("ClanAbandonError", "&cYou can't leave a clan if you don't join one!");
            languageDB.insertTranslation("ClansParseIntError", "&cMake sure you enter a valid number!");
            languageDB.insertTranslation("ClansIntUnderflow", "&cNumber must be greater than or equal to 1");
            languageDB.insertTranslation("ClansIntOverflow", "&cYou are already at the bottom of the list!");
            languageDB.insertTranslation("ClansPlayerNotExist", "&cThis player does not exist or is not online!");
            languageDB.insertTranslation("ClansPlayerAlreadyHasClan", "&cThis player already has a clan!");
            languageDB.insertTranslation("ClansPlayerHasInvite", "&cThis player has already been invited to a clan, please wait until the invitation expires or is accepted/rejected.");
            languageDB.insertTranslation("ClansNoInvite", "&cYou have not received any clan invitations or the invitation has expired!");
            languageDB.insertTranslation("ClanRankMaxOutWithColors", "&cClan rank cannot be longer than 32 characters!");
            languageDB.insertTranslation("ClanNotFound", "&cNo clans were found with this tag or name!");

            // Messages
            languageDB.insertTranslation("ClansInviteSent", "&aClan invitation sent to %PlayerName%");
            languageDB.insertTranslation("ClanCreated", "&aClan &b%ClanName% &ahas been successfully created!");
            languageDB.insertTranslation("ClansListTitle", "&a-=-= &bClan List %PageNumber% &a=-=-");
            languageDB.insertTranslation("ClansListRowTitle", "&e   Name                           Tag   KDR   Rivals   Allies");
            languageDB.insertTranslation("ClansListRow", "#%Index% &f%ClanName% &8- &a%ClanTag% &8- &e%ClanKDR% &8- &e%ClanRivals% &8- &e%ClanAllies%");
            languageDB.insertTranslation("ClansListFoot", "&aTo see more clans use &b/clan list %PageNumber%");
            languageDB.insertTranslation("ClansAccept", "ACCEPT");
            languageDB.insertTranslation("ClansHolderAccept", "&6Click to accept.");
            languageDB.insertTranslation("ClansReject", "REJECT");
            languageDB.insertTranslation("ClansHolderReject", "&6Click to reject.");
            languageDB.insertTranslation("ClansAbandon", "ABANDON");
            languageDB.insertTranslation("ClansHolderAbandon", "&6Click to abandon.");
            languageDB.insertTranslation("ClansAbandonMessagePrefix", "&6Click on ");
            languageDB.insertTranslation("ClansAbandonMessageSuffix", " &6to abandon the clan.");
            languageDB.insertTranslation("ClansPlayerInvite", "&aYou have been invited to join &b%ClanName% &f[%ClanTag%&f]                              %Accept%          %Reject%");
            languageDB.insertTranslation("ClansAllieInvite", "&aYour clan has been invited to ally with &b%ClanName% &f[%ClanTag%&f]                     %Accept%          %Reject%");
            languageDB.insertTranslation("ClansLeaderDeleted", "&6The clan you participate in has been deleted by the leader!");
            languageDB.insertTranslation("ClansParticipationCancel", "&cYou already participate in a clan, exit first!");
            languageDB.insertTranslation("ClansInviteRejected", "&aYou have rejected the clan invitation.");
            languageDB.insertTranslation("ClansInviteAccepted", "&aYou have accepted the clan invitation.");
            languageDB.insertTranslation("ClansNewMember", "&aThe clan has a new member: %PlayerName%");
            languageDB.insertTranslation("ClansPlayerTitle", "&a-=-= Player %PlayerName%&a Statistics =-=-");
            languageDB.insertTranslation("ClansPlayerClan",     " &7Clan:      %ClanTag% %ClanName%");
            languageDB.insertTranslation("ClansPlayerRank",     " &7Rank:      &b%PlayerRank%");
            languageDB.insertTranslation("ClansPlayerKDR",      " &7KDR:       &e%KDR%");
            languageDB.insertTranslation("ClansPlayerKills",    " &7Kills:     &9[Neutral: &e%NeutralKills%&9, Rival: &e%RivalKills%&9, Civilian: &e%CivilianKills%&9]");
            languageDB.insertTranslation("ClansPlayerDeaths",   " &7Deaths:    &e%PlayerDeaths%");
            languageDB.insertTranslation("ClansPlayerJoinDate", " &7Join Date: &f%JoinDate%");
            languageDB.insertTranslation("ClansPlayerLastSeen", " &7Last Seen: &f%LastSeen%");
            languageDB.insertTranslation("ClansRankUpdated", "&aThe rank of %PlayerName% &ahas been updated!");
            languageDB.insertTranslation("ClansMembersTitle", "&a-=-= Clan %ClanName% %ClanTag%&a Members =-=-");
            languageDB.insertTranslation("ClansStatsTitle", "&a-=-= Clan %ClanName% %ClanTag%&a Stats =-=-");
            languageDB.insertTranslation("ClansStatsKDR",         " &7KDR:       &e%KDR%");
            languageDB.insertTranslation("ClansStatsKills",       " &7Kills:     &9[Neutral: &e%NeutralKills%&9, Rival: &e%RivalKills%&9, Civilian: &e%CivilianKills%&9]");
            languageDB.insertTranslation("ClansStatsDeaths",      " &7Deaths:    &e%Deaths%");
            languageDB.insertTranslation("ClansStatsRivals",      " &7Rivals:    &f%Rivals%");
            languageDB.insertTranslation("ClansStatsAllies",      " &7Allies:    &f%Allies%");
            languageDB.insertTranslation("ClansStatsMemberCount", " &7Member Count:   &f%MemberCount%");
            languageDB.insertTranslation("ClansPlayerInviteSent", "&aClan invitation sent to %PlayerName%");
            languageDB.insertTranslation("ClansAllieInviteSent", "&aAllie invitation sent to %ClanName% %ClanTag%");
            languageDB.insertTranslation("ClansRivalAdd", "&aRival successfully added!");
            languageDB.insertTranslation("ClansRivalRemove", "&aRival successfully removed!");
            languageDB.insertTranslation("ClansAllieAccepted", "&aThe clan leader or moderator has agreed to ally with %ClanName% %ClanTag%");
            languageDB.insertTranslation("ClansAllieRejected", "&aThe clan leader or moderator has declined to ally with %ClanName% %ClanTag%");
            languageDB.insertTranslation("ClansChatMessage", "&7[%ClanName%&7] &7%PlayerName%&7: &b%Message%");

            // Commands
            languageDB.insertTranslation("ClansCommands", "&6Clans commands");
            languageDB.insertTranslation("ClansCreateCommand", " &a/clan create [tag] [name] &8- &7Create a clan($1000)");
            languageDB.insertTranslation("ClansListCommand", " &a/clan list &8- &7List clans");
            languageDB.insertTranslation("ClansStatsCommand", " &a/clan stats [name] &8- &7See clan stats info");
            languageDB.insertTranslation("ClansPlayerCommand", " &a/clan player [player] &8- &7See player stats info");
            languageDB.insertTranslation("ClansSetRankCommand", " &a/clan setrank <player> <rank> &8- &7Clans rank");
            languageDB.insertTranslation("ClansInviteAllieCommand", " &a/clan inviteallie &8- &7See clan allies");
            languageDB.insertTranslation("ClansAddRivalCommand", " &a/clan addrival <clan> &8- &Add rival clan");
            languageDB.insertTranslation("ClansRemoveRivalCommand", " &a/clan removerival <clan> &8- &Remove rival clan");
            languageDB.insertTranslation("ClansMembersCommand", " &a/clan members [name] &8- &7See clan members");
            languageDB.insertTranslation("ClansFriendFireCommand", " &a/clan friendfire [enable/disable] &8- &7Enable/Disable friend fire");
            languageDB.insertTranslation("ClansAbandonCommand", " &a/clan abandon &8- &7Abandon clan");
            languageDB.insertTranslation("ClansInviteCommand", " &a/clan invite <player> &8- &7Invite someone to clan");
            languageDB.insertTranslation("ClansInviteAllieCommand", " &a/clan inviteallie <clan> &8- &7Invite clan to allie");
            languageDB.insertTranslation("ClansAcceptCommand", " &a/clan accept &8- &7Accept clan invite");
            languageDB.insertTranslation("ClansRejectCommand", " &a/clan reject &8- &7Reject clan invite");
            languageDB.insertTranslation("ClansAllieCommand", " &a/clan allie accept/reject &8- &7Accept or Reject clan allie invitation");
        }
        if (LanguageManager.saveLanguages()) LanguageManager.reloadLanguages();
        new ClanCommand(plugin);
    }

    public static ClanPlayer getClanPlayer(String playerName) {
        return context.playerClans.get(playerName);
    }

    public static ClanPlayer getClanPlayer(Player player) {
        return getClanPlayer(player.getName());
    }

    public static Clan getClan(String tagOrName) {
        for (Clan clan : context.clans) {
            if(clan.tag.equalsIgnoreCase(tagOrName) || clan.name.equalsIgnoreCase(tagOrName)) return clan;
        }
        return null;
    }

    public void createClan(Player player, String colorTag, String name) {
        if (!EconomyManager.economy.has(player, RankupConfiguration.ClanCreationPrice)) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClanNotEnoughMoney").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if(getClanPlayer(player).clan != null) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansParticipationCancel").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        String tag = ChatColor.stripColor(colorTag.replace('&', ChatColor.COLOR_CHAR));
        if (tag.length() > 3) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClanTagMaxOut").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if (colorTag.length() > 25) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClanTagMaxOutWithColors").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        name = ChatColor.stripColor(name.replace('&', ChatColor.COLOR_CHAR));
        if (name.length() > 50) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClanNameMaxOut").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        for (Clan clan : clans) {
            if (clan.tag.equalsIgnoreCase(tag)) {
                player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClanTagAlreadyExists").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
            if (clan.name.equalsIgnoreCase(name)) {
                player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClanNameAlreadyExists").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
        }
        EconomyResponse response = EconomyManager.economy.withdrawPlayer(player, RankupConfiguration.ClanCreationPrice);
        if (!response.transactionSuccess()) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClanTransactionFailure").replaceAll("%ErrorMessage%", response.errorMessage).replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        String finalName = name;
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            Clan clan = new Clan(CoreClientManager.get(player).getAccountId(), tag, colorTag, finalName, false, 0, 0, 0, 0);
            clan.members.add(player.getName());
            repository.createClan(clan);
            clans.add(clan);
            ClanPlayer clanPlayer = getClanPlayer(player);
            clanPlayer.clan = clan;
            if(!playerUpdateBuffer.contains(clanPlayer)) {
                playerUpdateBuffer.push(clanPlayer);
            }
            ChatManager.regenerateTags(player);
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClanCreated").replaceAll("%ClanName%", finalName).replace('&', ChatColor.COLOR_CHAR));
        });
    }

    public void clanChat(Player player, String message) {
        ClanPlayer clanPlayer = ClanManager.getClanPlayer(player);
        if(clanPlayer.clan == null) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansParticipationError").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        for(String member : clanPlayer.clan.members) {
            Player clanMember = UtilPlayer.searchExact(member);
            if(clanMember == null) continue;
            clanMember.sendMessage(
                    LanguageManager.getTranslation("en-us", "ClansChatMessage")
                            .replace("%ClanName%", clanPlayer.clan.colorTag)
                            .replace("%PlayerName%", player.getDisplayName())
                            .replace("%Message%", ChatColor.stripColor(message))
                            .replace('&', ChatColor.COLOR_CHAR)
            );
        }
    }

    public void listClans(Player player, String args[]) {
        int index = 0;
        if (args.length == 2) {
            try {
                index = Integer.parseInt(args[1]) - 1;
            } catch (Exception ex) {
                player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansParseIntError").replace('&', ChatColor.COLOR_CHAR));
                return;
            }
        }
        if (index < 0) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansIntUnderflow").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if (ClanManager.context.clans.size() - (index * 10) <= -8) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansIntOverflow").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansListTitle")
                .replaceAll("%PageNumber%", String.valueOf(index + 1))
                .replace('&', ChatColor.COLOR_CHAR)
        );
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansListRowTitle").replace('&', ChatColor.COLOR_CHAR));
        for (int i = index * 10; i < ClanManager.context.clans.size(); i++) {
            Clan clan = ClanManager.context.clans.get(i);
            StringBuilder clanName;
            if (clan.name.length() > 25) {
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
            player.sendMessage(
                    LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansListRow")
                            .replaceAll("%Index%", String.valueOf(i))
                            .replaceAll("%ClanName%", clanName.toString())
                            .replaceAll("%ClanTag%", tag.toString())
                            .replaceAll("%ClanKDR%", String.format("%.1f", clan.kdr))
                            .replaceAll("%ClanRivals%", "")
                            .replaceAll("%ClanAllies%", "")
                            .replace('&', ChatColor.COLOR_CHAR)
            );
        }
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansListFoot")
                .replaceAll("%PageNumber%", String.valueOf(index + 2))
                .replace('&', ChatColor.COLOR_CHAR)
        );

    }

    public void abandonClan(Player player, boolean confirmDelete) {
        ClanPlayer clanPlayer = getClanPlayer(player);
        if (clanPlayer.clan == null) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClanAbandonError").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        Clan clan = clanPlayer.clan;
        if (clan.leader == CoreClientManager.get(player).getAccountId()) {
            if (confirmDelete) {
                repository.setClanNullForMembers(clan.members);
                repository.deleteClan(clan);
                for (ClanPlayer cp : playerClans.values()) {
                    if (cp.clan == clan) {
                        cp.clan = null;
                        cp.isClanMod = false;
                        if(!playerUpdateBuffer.contains(cp)) {
                            playerUpdateBuffer.push(cp);
                        }
                        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansLeaderDeleted").replace('&', ChatColor.COLOR_CHAR));
                    }
                }
                clans.remove(clan);
            } else {
                new JsonMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansAbandonMessagePrefix").replace('&', ChatColor.COLOR_CHAR))
                        .extra(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansAbandon").replace('&', ChatColor.COLOR_CHAR))
                        .color(JsonColor.RED)
                        .bold()
                        .click(ClickEvent.RUN_COMMAND, "/clan abandon true")
                        .hover(HoverEvent.SHOW_TEXT, LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansHolderAbandon").replace('&', ChatColor.COLOR_CHAR))
                        .extra(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansAbandonMessageSuffix").replace('&', ChatColor.COLOR_CHAR))
                        .sendToPlayer(player);
                return;
            }
        }
        clanPlayer.clan = null;
        clanPlayer.isClanMod = false;
        clan.members.remove(player.getName());
        if(!playerUpdateBuffer.contains(clanPlayer)) {
            playerUpdateBuffer.push(clanPlayer);
        }
        ChatManager.regenerateTags(player);
    }

    public void invitePlayer(Player caller, String playerName) {
        Player player = UtilPlayer.searchExact(playerName);
        if (player == null) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansPlayerNotExist").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if (caller == player) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansInviteYourself").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if (getClanPlayer(player).clan != null) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansPlayerAlreadyHasClan").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if (clanPlayerInvites.containsKey(player.getName())) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansPlayerHasInvite").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        ClanPlayer clanPlayer = getClanPlayer(caller);
        if (clanPlayer.clan == null) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansParticipationError").replace('&', ChatColor.COLOR_CHAR));
        }
        if (clanPlayer.clan.leader != CoreClientManager.get(caller).getAccountId() && !clanPlayer.isClanMod) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansNoPermission").replace('&', ChatColor.COLOR_CHAR));
        }
        clanPlayerInvites.put(player.getName(), new ClanPlayerInvite(player, clanPlayer.clan, RankupConfiguration.ClanInviteExpiration));
        String message = LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansPlayerInvite").replace('&', ChatColor.COLOR_CHAR)
                .replaceAll("%ClanName%", clanPlayer.clan.name)
                .replaceAll("%ClanTag%", clanPlayer.clan.colorTag)
                .replace('&', ChatColor.COLOR_CHAR);
        String[] acceptSplit = message.split("%Accept%", 2);
        String[] rejectSplit = acceptSplit[1].split("%Reject%", 2);
        new JsonMessage(acceptSplit[0])
                .extra(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansAccept").replace('&', ChatColor.COLOR_CHAR))
                .color(JsonColor.GREEN)
                .bold()
                .click(ClickEvent.RUN_COMMAND, "/clan accept")
                .hover(HoverEvent.SHOW_TEXT, LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansHolderAccept").replace('&', ChatColor.COLOR_CHAR))
                .extra(rejectSplit[0])
                .extra(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansReject").replace('&', ChatColor.COLOR_CHAR))
                .color(JsonColor.RED)
                .bold()
                .click(ClickEvent.RUN_COMMAND, "/clan reject")
                .hover(HoverEvent.SHOW_TEXT, LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansHolderReject").replace('&', ChatColor.COLOR_CHAR))
                .extra(rejectSplit[1])
                .sendToPlayer(player);
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansPlayerInviteSent")
                .replaceAll("%PlayerName%", player.getDisplayName())
                .replace('&', ChatColor.COLOR_CHAR));
    }

    public void clanMembers(Player caller) {
        ClanPlayer clanPlayer = getClanPlayer(caller);
        if(clanPlayer.clan == null) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansParticipationError").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansMembersTitle")
                .replaceAll("%ClanName%", clanPlayer.clan.name)
                .replaceAll("%ClanTag%", clanPlayer.clan.colorTag)
                .replace('&', ChatColor.COLOR_CHAR));
        for (String member : clanPlayer.clan.members) {
            caller.sendMessage(member);
        }
    }

    public void clanMembers(Player caller, String clanTagOrName) {
        Clan clan = getClan(clanTagOrName);
        if(clan == null) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClanNotFound").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansMembersTitle")
                .replaceAll("%ClanName%", clan.name)
                .replaceAll("%ClanTag%", clan.colorTag)
                .replace('&', ChatColor.COLOR_CHAR));
        for (String member : clan.members) {
            caller.sendMessage(member);
        }
    }

    public void clanStats(Player caller, String clanTagOrName) {
        Clan clan = getClan(clanTagOrName);
        if(clan == null) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClanNotFound").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansStatsTitle")
                .replaceAll("%ClanName%", clan.name)
                .replaceAll("%ClanTag%", clan.colorTag)
                .replace('&', ChatColor.COLOR_CHAR));
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansStatsKDR")
                .replaceAll("%KDR%", String.valueOf(clan.kdr))
                .replace('&', ChatColor.COLOR_CHAR));
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansStatsKills")
                .replaceAll("%NeutralKills%", String.valueOf(clan.neutralKills))
                .replaceAll("%RivalKills%", String.valueOf(clan.rivalKills))
                .replaceAll("%CivilianKills%", String.valueOf(clan.civilianKills))
                .replace('&', ChatColor.COLOR_CHAR));
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansStatsDeaths")
                .replaceAll("%Deaths%", String.valueOf(clan.deaths))
                .replace('&', ChatColor.COLOR_CHAR));
        StringBuilder rivals = new StringBuilder();
        for (Clan rival : clan.rivals) {
            rivals.append(rival.colorTag).append("&8, ");
        }
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansStatsRivals")
                .replaceAll("%Rivals%", rivals.toString())
                .replace('&', ChatColor.COLOR_CHAR));
        StringBuilder allies = new StringBuilder();
        for (Clan allie : clan.allies) {
            allies.append(allie.colorTag).append("&8, ");
        }
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansStatsAllies")
                .replaceAll("%Allies%", String.valueOf(clan.neutralKills))
                .replace('&', ChatColor.COLOR_CHAR));
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansStatsMemberCount")
                .replaceAll("%MemberCount%", String.valueOf(clan.members.size()))
                .replace('&', ChatColor.COLOR_CHAR));
    }

    public void clanPlayer(Player caller, String playerName) {
        Player player = null;
        if(playerName == null) player = caller;
        else player = UtilPlayer.searchExact(playerName);
        if(player == null) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansPlayerNotExist").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        ClanPlayer clanPlayer = getClanPlayer(player);
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansPlayerTitle")
                .replaceAll("%PlayerName%", player.getDisplayName())
                .replace('&', ChatColor.COLOR_CHAR));
        if(clanPlayer.clan != null) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansPlayerClan")
                    .replaceAll("%ClanTag%", clanPlayer.clan.colorTag)
                    .replaceAll("%ClanName%", clanPlayer.clan.name)
                    .replace('&', ChatColor.COLOR_CHAR));
        } else {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansPlayerClan")
                    .replaceAll("%ClanTag%", "")
                    .replaceAll("%ClanName%", "")
                    .replace('&', ChatColor.COLOR_CHAR));
        }
        if(clanPlayer.rank == null) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansPlayerRank")
                    .replaceAll("%PlayerRank%", "")
                    .replace('&', ChatColor.COLOR_CHAR));
        } else {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansPlayerRank")
                    .replaceAll("%PlayerRank%", clanPlayer.rank)
                    .replace('&', ChatColor.COLOR_CHAR));
        }
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansPlayerKDR")
                .replaceAll("%KDR%", String.valueOf(clanPlayer.kdr))
                .replace('&', ChatColor.COLOR_CHAR));
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansPlayerKills")
                .replaceAll("%NeutralKills%", String.valueOf(clanPlayer.neutralKills))
                .replaceAll("%RivalKills%", String.valueOf(clanPlayer.rivalKills))
                .replaceAll("%CivilianKills%", String.valueOf(clanPlayer.civilianKills))
                .replace('&', ChatColor.COLOR_CHAR));
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansPlayerDeaths")
                .replaceAll("%PlayerDeaths%", String.valueOf(clanPlayer.deaths))
                .replace('&', ChatColor.COLOR_CHAR));
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansPlayerJoinDate")
                .replaceAll("%JoinDate%", String.valueOf(clanPlayer.joinDate.toString()))
                .replace('&', ChatColor.COLOR_CHAR));
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansPlayerLastSeen")
                .replaceAll("%LastSeen%", String.valueOf(clanPlayer.lastSeen.toString()))
                .replace('&', ChatColor.COLOR_CHAR));
    }

    public void setRank(Player caller, String playerName, String rankName) {
        if(rankName.length() > 32) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClanRankMaxOutWithColors").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        Player player = null;
        if(playerName == null) player = caller;
        else player = UtilPlayer.searchExact(playerName);
        if(player == null) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansPlayerNotExist").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        ClanPlayer clanPlayer = getClanPlayer(player);
        ClanPlayer clanCaller = getClanPlayer(caller);
        if(clanPlayer.clan != clanCaller.clan) return;
        if(clanCaller.clan.leader != CoreClientManager.get(caller).getAccountId() && !clanCaller.isClanMod) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansNoPermission").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if(rankName.equalsIgnoreCase("")) clanPlayer.rank = null;
        else clanPlayer.rank = rankName;
        if(!playerUpdateBuffer.contains(clanPlayer)) {
            playerUpdateBuffer.push(clanPlayer);
        }
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansRankUpdated")
                .replaceAll("%PlayerName%", player.getDisplayName())
                .replace('&', ChatColor.COLOR_CHAR));
    }

    public void inviteAllie(Player caller, String clanTagOrName) {
        ClanPlayer clanPlayer = getClanPlayer(caller);
        Clan clan = getClan(clanTagOrName);
        if(clan == null) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClanNotFound").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if(clanPlayer.clan == null) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansParticipationError").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if (clanAllieInvites.containsKey(clan.id)) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansClanHasInvite").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if (clanPlayer.clan.leader != CoreClientManager.get(caller).getAccountId() && !clanPlayer.isClanMod) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansNoPermission").replace('&', ChatColor.COLOR_CHAR));
        }
        clanAllieInvites.put(clan.id, new ClanAllieInvite(clanPlayer.clan, clan, RankupConfiguration.ClanInviteExpiration));
        for (String member : clan.members) {
            Player player = UtilPlayer.searchExact(member);
            if(player == null) continue;
            if(clan.leader != CoreClientManager.get(player).getAccountId() && !getClanPlayer(player).isClanMod) continue;
            String message = LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansAllieInvite").replace('&', ChatColor.COLOR_CHAR)
                    .replaceAll("%ClanName%", clanPlayer.clan.name)
                    .replaceAll("%ClanTag%", clanPlayer.clan.colorTag)
                    .replace('&', ChatColor.COLOR_CHAR);
            String[] acceptSplit = message.split("%Accept%", 2);
            String[] rejectSplit = acceptSplit[1].split("%Reject%", 2);
            new JsonMessage(acceptSplit[0])
                    .extra(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansAccept").replace('&', ChatColor.COLOR_CHAR))
                    .color(JsonColor.GREEN)
                    .bold()
                    .click(ClickEvent.RUN_COMMAND, "/clan allie accept")
                    .hover(HoverEvent.SHOW_TEXT, LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansHolderAccept").replace('&', ChatColor.COLOR_CHAR))
                    .extra(rejectSplit[0])
                    .extra(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansReject").replace('&', ChatColor.COLOR_CHAR))
                    .color(JsonColor.RED)
                    .bold()
                    .click(ClickEvent.RUN_COMMAND, "/clan allie reject")
                    .hover(HoverEvent.SHOW_TEXT, LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansHolderReject").replace('&', ChatColor.COLOR_CHAR))
                    .extra(rejectSplit[1]).sendToPlayer(player);
        }
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansAllieInviteSent")
                .replaceAll("%ClanName%", clan.name)
                .replaceAll("%ClanTag%", clan.colorTag)
                .replace('&', ChatColor.COLOR_CHAR));
    }

    public void addRival(Player caller, String clanTagOrName) {
        ClanPlayer clanPlayer = getClanPlayer(caller);
        Clan clan = getClan(clanTagOrName);
        if(clan == null) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClanNotFound").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if(clanPlayer.clan == null) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansParticipationError").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if(clanPlayer.clan.leader != CoreClientManager.get(caller).getAccountId() && !clanPlayer.isClanMod) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansNoPermission").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if(!clanPlayer.clan.rivals.contains(clan)) {
            clanPlayer.clan.rivals.add(clan);
            if(!clanUpdateBuffer.contains(clanPlayer.clan)) {
                clanUpdateBuffer.push(clanPlayer.clan);
            }
        }
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansRivalAdd").replace('&', ChatColor.COLOR_CHAR));
    }

    public void removeRival(Player caller, String clanTagOrName) {
        ClanPlayer clanPlayer = getClanPlayer(caller);
        Clan clan = getClan(clanTagOrName);
        if(clan == null) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClanNotFound").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if(clanPlayer.clan == null) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansParticipationError").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if(clanPlayer.clan.leader != CoreClientManager.get(caller).getAccountId() && !clanPlayer.isClanMod) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansNoPermission").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if(clanPlayer.clan.rivals.contains(clan)) {
            clanPlayer.clan.rivals.remove(clan);
            if(!clanUpdateBuffer.contains(clanPlayer.clan)) {
                clanUpdateBuffer.push(clanPlayer.clan);
            }
        }
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansRivalRemove").replace('&', ChatColor.COLOR_CHAR));
    }

    public void acceptAllieInvite(Player caller) {
        ClanPlayer clanPlayer = getClanPlayer(caller);
        if(clanPlayer.clan == null) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansParticipationError").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if (!clanAllieInvites.containsKey(clanPlayer.clan.id)) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansNoInvite").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if (clanPlayer.clan.leader != CoreClientManager.get(caller).getAccountId() && !clanPlayer.isClanMod) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansNoPermission").replace('&', ChatColor.COLOR_CHAR));
        }
        ClanAllieInvite clanAllieInvite = clanAllieInvites.get(clanPlayer.clan.id);
        clanAllieInvite.getFrom().allies.add(clanAllieInvite.getTo());
        clanAllieInvite.getTo().allies.add(clanAllieInvite.getFrom());
        if(!clanUpdateBuffer.contains(clanAllieInvite.getFrom())) {
            clanUpdateBuffer.push(clanAllieInvite.getFrom());
        }
        if(!clanUpdateBuffer.contains(clanAllieInvite.getTo())) {
            clanUpdateBuffer.push(clanAllieInvite.getTo());
        }
        for(String memberName : clanAllieInvite.getFrom().members) {
            Player player = UtilPlayer.searchExact(memberName);
            if(player == null) continue;
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansAllieAccepted")
                    .replaceAll("%ClanName%", clanAllieInvite.getTo().name)
                    .replaceAll("%ClanTag%", clanAllieInvite.getTo().colorTag)
                    .replace('&', ChatColor.COLOR_CHAR));
        }
        for(String memberName : clanAllieInvite.getTo().members) {
            Player player = UtilPlayer.searchExact(memberName);
            if(player == null) continue;
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansAllieAccepted")
                    .replaceAll("%ClanName%", clanAllieInvite.getFrom().name)
                    .replaceAll("%ClanTag%", clanAllieInvite.getFrom().colorTag)
                    .replace('&', ChatColor.COLOR_CHAR));
        }
    }

    public void rejectAllieInvite(Player caller) {
        ClanPlayer clanPlayer = getClanPlayer(caller);
        if(clanPlayer.clan == null) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansParticipationError").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if (!clanAllieInvites.containsKey(clanPlayer.clan.id)) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansNoInvite").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if (clanPlayer.clan.leader != CoreClientManager.get(caller).getAccountId() && !clanPlayer.isClanMod) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansNoPermission").replace('&', ChatColor.COLOR_CHAR));
        }
        ClanAllieInvite clanAllieInvite = clanAllieInvites.get(clanPlayer.clan.id);
        for(String memberName : clanAllieInvite.getFrom().members) {
            Player player = UtilPlayer.searchExact(memberName);
            if(player == null) continue;
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansAllieRejected")
                    .replaceAll("%ClanName%", clanAllieInvite.getTo().name)
                    .replaceAll("%ClanTag%", clanAllieInvite.getTo().colorTag)
                    .replace('&', ChatColor.COLOR_CHAR));
        }
        for(String memberName : clanAllieInvite.getTo().members) {
            Player player = UtilPlayer.searchExact(memberName);
            if(player == null) continue;
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansAllieRejected")
                    .replaceAll("%ClanName%", clanAllieInvite.getFrom().name)
                    .replaceAll("%ClanTag%", clanAllieInvite.getFrom().colorTag)
                    .replace('&', ChatColor.COLOR_CHAR));
        }
        clanAllieInvites.remove(clanPlayer.clan.id);
    }

    public void acceptPlayerInvite(Player caller) {
        if(!clanPlayerInvites.containsKey(caller.getName())) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansNoInvite").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        ClanPlayerInvite clanInvite = clanPlayerInvites.get(caller.getName());
        clanInvite.getClan().members.add(caller.getName());
        ClanPlayer clanPlayer = getClanPlayer(caller);
        clanPlayer.clan = clanInvite.getClan();
        clanPlayer.isClanMod = false;
        if(!playerUpdateBuffer.contains(clanPlayer)) {
            playerUpdateBuffer.push(clanPlayer);
        }
        clanPlayerInvites.remove(caller.getName());
        for(String memberName : clanInvite.getClan().members) {
            Player player = UtilPlayer.searchExact(memberName);
            if(player == null) continue;
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansNewMember")
                    .replaceAll("%PlayerName%", caller.getDisplayName())
                    .replace('&', ChatColor.COLOR_CHAR));
        }
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansInviteAccepted").replace('&', ChatColor.COLOR_CHAR));
        ChatManager.regenerateTags(caller);
    }

    public void rejectPlayerInvite(Player caller) {
        if(!clanPlayerInvites.containsKey(caller.getName())) {
            caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansNoInvite").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        clanPlayerInvites.remove(caller.getName());
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansInviteRejected").replace('&', ChatColor.COLOR_CHAR));
        ChatManager.regenerateTags(caller);
    }

    @EventHandler
    public void onBufferElapsed(UpdateEvent event) {
        if (event.getType() == UpdateType.SLOW) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
                for (String key : clanPlayerInvites.keySet()) {
                    if (clanPlayerInvites.get(key).getTimeout() <= 0) {
                        clanPlayerInvites.remove(key);
                        continue;
                    }
                    clanPlayerInvites.get(key).reduceTimeout();
                }
                for (int key : clanAllieInvites.keySet()) {
                    if (clanAllieInvites.get(key).getTimeout() <= 0) {
                        clanAllieInvites.remove(key);
                        continue;
                    }
                    clanAllieInvites.get(key).reduceTimeout();
                }
                if (!clanUpdateBuffer.isEmpty()) {
                    repository.updateClans(clanUpdateBuffer);
                    clans.sort(Comparator.comparingDouble(c -> c.kdr));
                }
                if (!playerUpdateBuffer.isEmpty()) {
                    repository.updateClanPlayers(playerUpdateBuffer);
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDeath(PlayerDeathEvent event) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            Player victim = event.getEntity();
            Player killer = event.getEntity().getKiller();
            if (victim == null || killer == null) return;

            ClanPlayer victimClanPlayer = getClanPlayer(victim);
            ClanPlayer killerClanPlayer = getClanPlayer(killer);
            Clan victimClan = victimClanPlayer.clan;
            Clan killerClan = killerClanPlayer.clan;

            victimClanPlayer.deaths += 1;
            victimClanPlayer.calculateKdr();
            if(!playerUpdateBuffer.contains(victimClanPlayer)) {
                playerUpdateBuffer.push(victimClanPlayer);
            }

            if (victimClan != null) {
                victimClan.deaths += 1;
                victimClan.calculateKdr();
                if(!clanUpdateBuffer.contains(victimClan)) {
                    clanUpdateBuffer.push(victimClan);
                }
            }

            if (killerClan != null) {
                if (killerClan.rivals.contains(victimClan)) {
                    killerClan.rivalKills += 1;
                    killerClanPlayer.rivalKills += 1;
                } else {
                    if (victimClan == null) {
                        killerClan.civilianKills += 1;
                        killerClanPlayer.civilianKills += 1;
                    } else {
                        killerClan.neutralKills += 1;
                        killerClanPlayer.neutralKills += 1;
                    }
                }
                killerClan.calculateKdr();
                if(!clanUpdateBuffer.contains(killerClan)) {
                    clanUpdateBuffer.push(killerClan);
                }
            } else {
                killerClanPlayer.neutralKills += 1;
            }
            killerClanPlayer.calculateKdr();
            if(!playerUpdateBuffer.contains(killerClanPlayer)) {
                playerUpdateBuffer.push(killerClanPlayer);
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        if(!(event.getDamager() instanceof Player)) return;
        Player victim = (Player) event.getEntity();
        ClanPlayer victimInfo = getClanPlayer(victim);
        if(victimInfo.clan == null) return;
        if(victimInfo.clan.friendFire) return;
        Player damager = (Player) event.getDamager();
        ClanPlayer damagerInfo = getClanPlayer(damager);
        if(victimInfo.clan != damagerInfo.clan) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            TimingManager.start(event.getPlayer().getName() + " load clan");
            ClanPlayer clanPlayer = repository.getClanPlayer(CoreClientManager.get(event.getPlayer()).getAccountId(), event.getPlayer().getName(), clans);
            clanPlayer.calculateKdr();
            playerClans.put(event.getPlayer().getName(), clanPlayer);
            TimingManager.stop(event.getPlayer().getName() + " load clan");
            ChatManager.regenerateTags(event.getPlayer());
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            playerClans.remove(event.getPlayer().getName());
            clanPlayerInvites.remove(event.getPlayer().getName());
        });
    }
}
