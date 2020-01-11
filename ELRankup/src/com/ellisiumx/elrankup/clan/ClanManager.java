package com.ellisiumx.elrankup.clan;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.jsonchat.ClickEvent;
import com.ellisiumx.elcore.jsonchat.HoverEvent;
import com.ellisiumx.elcore.jsonchat.JsonColor;
import com.ellisiumx.elcore.jsonchat.JsonMessage;
import com.ellisiumx.elcore.lang.LanguageDB;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elcore.timing.TimingManager;
import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import com.ellisiumx.elcore.utils.UtilLog;
import com.ellisiumx.elcore.utils.UtilPlayer;
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
    public HashMap<String, ClanInvite> clanInvites;
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
            languageDB.insertTranslation("ClansInvite", "&aYou have been invited to join &b%ClanName% &f[%ClanTag%&f]                              %Accept%          %Reject%");

            // Commands
            languageDB.insertTranslation("ClansCommands", "&6Clans commands");
            languageDB.insertTranslation("ClansCreateCommand", " &a/clan create [tag] [name] &8- &7Create a clan($1000)");
            languageDB.insertTranslation("ClansListCommand", " &a/clan list &8- &7List clans");
            languageDB.insertTranslation("ClansStatsCommand", " &a/clan stats [name] &8- &7See clan stats info");
            languageDB.insertTranslation("ClansPlayerCommand", " &a/clan player [player] &8- &7See player stats info");
            languageDB.insertTranslation("ClansSetRankCommand", " &a/clan setrank <player> <rank> &8- &7Clans rank");
            languageDB.insertTranslation("ClansInviteAllieCommand", " &a/clan inviteallie &8- &7See clan allies");
            languageDB.insertTranslation("ClansAddRivalCommand", " &a/clan addrival <clan> &8- &Add rival clan");
            languageDB.insertTranslation("ClansAddRivalCommand", " &a/clan removerival <clan> &8- &Remove rival clan");
            languageDB.insertTranslation("ClansMembersCommand", " &a/clan members [name] &8- &7See clan members");
            languageDB.insertTranslation("ClansFriendFireCommand", " &a/clan friendfire [enable/disable] &8- &7Enable/Disable friend fire");
            languageDB.insertTranslation("ClansAbandonCommand", " &a/clan abandon &8- &7Abandon clan");
            languageDB.insertTranslation("ClansInviteCommand", " &a/clan invite <player> &8- &7Invite someone to clan");
            languageDB.insertTranslation("ClansAcceptCommand", " &a/clan accept &8- &7Accept clan invite");
            languageDB.insertTranslation("ClansRejectCommand", " &a/clan reject &8- &7Reject clan invite");
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
        if (!EconomyManager.economy.has(player, RankupConfiguration.clanCreationPrice)) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClanNotEnoughMoney").replace('&', ChatColor.COLOR_CHAR));
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
        EconomyResponse response = EconomyManager.economy.withdrawPlayer(player, RankupConfiguration.clanCreationPrice);
        if (!response.transactionSuccess()) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClanTransactionFailure").replaceAll("%ErrorMessage%", response.errorMessage).replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        String finalName = name;
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            Clan clan = new Clan(CoreClientManager.get(player).getAccountId(), tag, colorTag, finalName, false, 0, 0, 0, 0);
            repository.createClan(clan);
            clans.add(clan);
            ClanPlayer clanPlayer = getClanPlayer(player);
            clanPlayer.clan = clan;
            playerUpdateBuffer.push(clanPlayer);
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClanCreated").replaceAll("%ClanName%", finalName).replace('&', ChatColor.COLOR_CHAR));
        });
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
        if (ClanManager.context.clans.size() - (index * 10) == -10) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansIntOverflow").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansListTitle")
                .replaceAll("%PageNumber%", String.valueOf(index))
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
                        // TODO: Send message alerting clan deletion
                    }
                }
                clans.remove(clan);
            }
            return;
        }
        clanPlayer.clan = null;
        clanPlayer.isClanMod = false;
        clan.members.remove(player.getName());
        playerUpdateBuffer.push(clanPlayer);
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
        if (clanInvites.containsKey(player.getName())) {
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
        clanInvites.put(player.getName(), new ClanInvite(player, clanPlayer.clan, RankupConfiguration.clanInviteExpiration));
        String message = LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ClansInvite").replace('&', ChatColor.COLOR_CHAR)
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
        caller.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(caller).getLanguage(), "ClansInviteSent")
                .replaceAll("%PlayerName%", player.getDisplayName())
                .replace('&', ChatColor.COLOR_CHAR));
    }

    public void removeRival(Player caller, String arg) {

    }

    public void clanMembers(Player caller) {

    }

    public void clanMembers(Player caller, String clanTagOrName) {

    }

    public void clanStats(Player caller, String clanTagOrName) {

    }

    public void clanPlayer(Player caller, String playerName) {

    }

    public void setRank(Player caller, String playerName, String rankName) {

    }

    public void inviteAllie(Player caller, String arg) {

    }

    public void addRival(Player caller, String arg) {

    }

    @EventHandler
    public void onBufferElapsed(UpdateEvent event) {
        if (event.getType() == UpdateType.SLOW) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
                for (String key : clanInvites.keySet()) {
                    if (clanInvites.get(key).getTimeout() <= 0) {
                        clanInvites.remove(key);
                        continue;
                    }
                    clanInvites.get(key).reduceTimeout();
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
            Clan killerClan = victimClanPlayer.clan;

            victimClanPlayer.deaths += 1;
            playerUpdateBuffer.push(victimClanPlayer);

            if (victimClan != null) {
                victimClan.deaths += 1;
                clanUpdateBuffer.push(victimClan);
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
                clanUpdateBuffer.push(killerClan);
            } else {
                killerClanPlayer.neutralKills += 1;
            }
            playerUpdateBuffer.push(killerClanPlayer);
        });
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            TimingManager.start(event.getPlayer().getName() + " load clan");
            ClanPlayer clanPlayer = repository.getClanPlayer(CoreClientManager.get(event.getPlayer()).getAccountId(), event.getPlayer().getName(), clans);
            playerClans.put(event.getPlayer().getName(), clanPlayer);
            TimingManager.stop(event.getPlayer().getName() + " load clan");
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            playerClans.remove(event.getPlayer().getName());
            clanInvites.remove(event.getPlayer().getName());
        });
    }
}
