package com.ellisiumx.elcore.permissions;

import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elcore.preferences.UserPreferences;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.prefs.Preferences;

public enum Rank {
    OWNER("Owner", ChatColor.DARK_RED),
    DEVELOPER("Dev", ChatColor.RED),
    ADMIN("Admin", ChatColor.RED),
    MODERATOR("Mod", ChatColor.GOLD),
    HELPER("Helper", ChatColor.DARK_AQUA),
    MAPLEAD("MapLead", ChatColor.DARK_PURPLE),
    MAPDEV("Builder", ChatColor.BLUE),
    EVENT("Event", ChatColor.WHITE),

    YOUTUBE("YouTube", ChatColor.RED),
    TWITCH("Twitch", ChatColor.DARK_PURPLE),
    MVP_PLUS("MVP+", ChatColor.AQUA),
    MVP("MVP", ChatColor.AQUA),
    VIP_PLUS("VIP+", ChatColor.LIGHT_PURPLE),
    VIP("VIP", ChatColor.GREEN),
    ALL("", ChatColor.WHITE);

    private ChatColor Color;
    public String Name;

    Rank(String name, ChatColor color) {
        Color = color;
        Name = name;
    }

    public boolean has(Rank rank) {
        return has(null, rank, false);
    }

    public boolean has(Player player, Rank rank, boolean inform) {
        return has(player, rank, null, inform);
    }

    public boolean has(Player player, Rank rank, Rank[] specific, boolean inform) {
        if (specific != null) {
            for (Rank curRank : specific) {
                if (compareTo(curRank) == 0) {
                    return true;
                }
            }
        }
        if (compareTo(rank) <= 0) return true;
        if (inform) {
            UserPreferences preferences = PreferencesManager.get(player);
            UtilPlayer.message(player,
                    UtilChat.mHead +
                            LanguageManager.getTranslation(preferences.getLanguage(), "RankPermissions") +
                            UtilChat.mBody +
                            LanguageManager.getTranslation(preferences.getLanguage(), "RankRequiredRank") +
                            UtilChat.mHead +
                            rank.Name.toUpperCase() +
                            UtilChat.mBody +
                            "]."
            );
        }
        return false;
    }

    public String getTag(boolean bold, boolean uppercase) {
        if (Name.equalsIgnoreCase("ALL"))
            return "";

        String name = Name;
        if (uppercase)
            name = Name.toUpperCase();

        if (bold) return Color + UtilChat.Bold + name;
        else return Color + name;
    }

    public ChatColor getColor() {
        return Color;
    }
}
