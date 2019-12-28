package com.ellisiumx.elrankup.clan.command;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.lang.LanguageDB;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.permissions.Rank;
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

    }

    private void showCommands(Player player) {

    }
}
