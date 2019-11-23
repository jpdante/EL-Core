package com.ellisiumx.elcore.utils;

import jsonchat.JsonMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class UtilTextBottom {
    public static void display(String text, Player... players) {
        JsonMessage msg = new JsonMessage(text);
        msg.send(JsonMessage.MessageType.ABOVE_HOTBAR, players);
    }

    public static void displayProgress(double amount, Player... players) {
        displayProgress(null, amount, null, players);
    }

    public static void displayProgress(String prefix, double amount, Player... players) {
        displayProgress(prefix, amount, null, players);
    }

    public static void displayProgress(String prefix, double amount, String suffix, Player... players) {
        displayProgress(prefix, amount, suffix, false, players);
    }

    public static void displayProgress(String prefix, double amount, String suffix, boolean progressDirectionSwap, Player... players) {
        if (progressDirectionSwap)
            amount = 1 - amount;

        //Generate Bar
        int bars = 24;
        String progressBar = UtilChat.cGreen + "";
        boolean colorChange = false;
        for (int i = 0; i < bars; i++) {
            if (!colorChange && (float) i / (float) bars >= amount) {
                progressBar += UtilChat.cRed;
                colorChange = true;
            }

            progressBar += "▌";
        }

        //Send to Player
        for (Player player : players) {
            //1.7 - Add Color
            if (!UtilPlayer.is1_8(player)) {
                UtilTextTop.displayProgress((prefix == null ? "" : UtilChat.cYellow + UtilChat.Bold + prefix) + (suffix == null ? "" : ChatColor.RESET + UtilChat.Bold + " - " + UtilChat.cGreen + UtilChat.Bold + suffix), amount, player);
            }
            //1.8
            else {
                display((prefix == null ? "" : prefix + ChatColor.RESET + " ") + progressBar + (suffix == null ? "" : ChatColor.RESET + " " + suffix), players);
            }
        }
    }
}
