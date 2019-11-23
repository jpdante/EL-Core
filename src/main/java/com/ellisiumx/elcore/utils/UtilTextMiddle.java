package com.ellisiumx.elcore.utils;

import net.minecraft.server.v1_8_R3.ChatMessage;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class UtilTextMiddle {
    public static void display(String text, String subtitle, Player... players) {
        setSubtitle(subtitle, players);

        showTitle(text, players);
    }

    public static void display(String text, String subtitle) {
        setSubtitle(subtitle, UtilServer.getPlayers());

        showTitle(text, UtilServer.getPlayers());
    }

    public static void display(String text, String subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks, Player... players) {
        setTimings(fadeInTicks, stayTicks, fadeOutTicks, players);

        display(text, subtitle, players);
    }

    public static void display(String text, String subtitle, int fadeInTicks, int stayTicks, int fadeOutTicks) {
        setTimings(fadeInTicks, stayTicks, fadeOutTicks, UtilServer.getPlayers());

        display(text, subtitle, UtilServer.getPlayers());
    }

    private static void showTitle(String text, Player... players) {
        if (text == null) text = "";

        ChatMessage message = new ChatMessage(text);
        PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, message);
        sendPacket(packet, players);
    }

    private static void setSubtitle(String text, Player... players) {
        if (text == null) text = "";

        ChatMessage message = new ChatMessage(text);
        PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, message);
        sendPacket(packet, players);
    }

    private static void setTimings(int fadeInTicks, int stayTicks, int fadeOutTicks, Player... players) {
        PacketPlayOutTitle packet = new PacketPlayOutTitle(fadeInTicks, stayTicks, fadeOutTicks);
        sendPacket(packet, players);
    }

    public static void clear(Player... players) {
        PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.CLEAR, IChatBaseComponent.ChatSerializer.a("{}"));
        sendPacket(packet, players);
    }

    public static void reset(Player... players) {
        PacketPlayOutTitle packet = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.RESET, IChatBaseComponent.ChatSerializer.a("{}"));
        sendPacket(packet, players);
    }

    private static void sendPacket(Packet packet, Player... players) {
        for (Player player : players) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
        }
    }

    public static String progress(float exp) {
        String out = "";

        for (int i = 0; i < 40; i++) {
            float cur = i * (1f / 40f);

            if (cur < exp)
                out += UtilChat.cGreen + UtilChat.Bold + "|";
            else
                out += UtilChat.cGray + UtilChat.Bold + "|";
        }

        return out;
    }

}
