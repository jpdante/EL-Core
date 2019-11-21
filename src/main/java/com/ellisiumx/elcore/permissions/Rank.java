package com.ellisiumx.elcore.permissions;

import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilPlayer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public enum Rank {
    LT("LT", ChatColor.DARK_RED),
    OWNER("Owner", ChatColor.DARK_RED),
    DEVELOPER("Dev", ChatColor.RED),
    ADMIN("Admin", ChatColor.RED),
    JNR_DEV("Jr.Dev", ChatColor.RED),
    SNR_MODERATOR("Sr.Mod", ChatColor.GOLD),
    MODERATOR("Mod", ChatColor.GOLD),
    HELPER("Trainee", ChatColor.DARK_AQUA),
    MAPLEAD("MapLead", ChatColor.DARK_PURPLE),
    MAPDEV("Builder", ChatColor.BLUE),

    EVENT("Event", ChatColor.WHITE),

    //Staff ^^

    YOUTUBE("YouTube", ChatColor.RED),
    TWITCH("Twitch", ChatColor.DARK_PURPLE),
    VIP("Vip", ChatColor.GREEN),
    VIP_PLUS("Vip+", ChatColor.LIGHT_PURPLE),
    MVP("MVP", ChatColor.AQUA),
    MVP_PLUS("MVP+", ChatColor.AQUA),
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
            UtilPlayer.message(player, UtilChat.mHead + "Permissions> " +
                    UtilChat.mBody + "This requires Permission Rank [" +
                    UtilChat.mHead + rank.Name.toUpperCase() +
                    UtilChat.mBody + "].");
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
