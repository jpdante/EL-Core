package com.ellisiumx.elcore.utils;

import com.ellisiumx.elcore.permissions.Rank;
import org.bukkit.ChatColor;

public class UtilMessage {
    public static String main(String module, String body) {
        return UtilChat.cWhite + "[" + UtilChat.mHead + module + UtilChat.cWhite + "] " + UtilChat.mBody + body;
    }

    public static String tute(String sender, String body) {
        return UtilChat.cGold + sender + "> " + UtilChat.cWhite + body;
    }

    public static String te(String message) {
        return UtilChat.cYellow + message + UtilChat.cWhite;
    }

    public static String game(String elem) {
        return UtilChat.mGame + elem + UtilChat.mBody;
    }

    public static String ta(String message) {
        return UtilChat.cGreen + message + UtilChat.cWhite;
    }

    public static String ts(String message) {
        return UtilChat.cGold + message + UtilChat.cWhite;
    }

    public static String sys(String head, String body) {
        return UtilChat.sysHead + head + "> " + UtilChat.sysBody + body;
    }

    public static String elem(String elem) {
        return UtilChat.mElem + elem + ChatColor.RESET + UtilChat.mBody;
    }

    public static String name(String elem) {
        return UtilChat.mElem + elem + UtilChat.mBody;
    }

    public static String count(String elem) {
        return UtilChat.mCount + elem + UtilChat.mBody;
    }

    public static String item(String elem) {
        return UtilChat.mItem + elem + UtilChat.mBody;
    }

    public static String link(String elem) {
        return UtilChat.mLink + elem + UtilChat.mBody;
    }

    public static String skill(String elem) {
        return UtilChat.mSkill + elem + UtilChat.mBody;
    }

    public static String skill(String a, String b) {

        return UtilChat.cYellow + " " + UtilChat.cGreen + b + UtilChat.mBody;
    }

    public static String time(String elem) {
        return UtilChat.mTime + elem + UtilChat.mBody;
    }

    public static String desc(String head, String body) {
        return UtilChat.descHead + head + ": " + UtilChat.descBody + body;
    }

    public static String wField(String field, String data) {
        return UtilChat.wFrame + "[" + UtilChat.wField + field + UtilChat.wFrame + "] " + UtilChat.mBody + data + " ";
    }

    public static String help(String cmd, String body, Rank rank) {
        return rank.getColor() + cmd + " " + UtilChat.mBody + body + " " + rank(rank);
    }

    public static String rank(Rank rank) {
        if (rank == Rank.ALL)
            return rank.getColor() + "Player";
        return rank.getTag(false, false);
    }

    public static String value(String variable, String value) {
        return value(0, variable, value);
    }

    public static String value(int a, String variable, String value) {
        String indent = "";
        while (indent.length() < a)
            indent += ChatColor.GRAY + ">";

        return indent + UtilChat.listTitle + variable + ": " + UtilChat.listValue + value;
    }

    public static String value(String variable, String value, boolean on) {
        return value(0, variable, value, on);
    }

    public static String value(int a, String variable, String value, boolean on) {
        String indent = "";
        while (indent.length() < a)
            indent += ChatColor.GRAY + ">";

        if (on) return indent + UtilChat.listTitle + variable + ": " + UtilChat.listValueOn + value;
        else return indent + UtilChat.listTitle + variable + ": " + UtilChat.listValueOff + value;
    }

    public static String ed(boolean var) {
        if (var)
            return UtilChat.listValueOn + "Enabled" + UtilChat.mBody;
        return UtilChat.listValueOff + "Disabled" + UtilChat.mBody;
    }

    public static String oo(boolean var) {
        if (var)
            return UtilChat.listValueOn + "On" + UtilChat.mBody;
        return UtilChat.listValueOff + "Off" + UtilChat.mBody;
    }

    public static String tf(boolean var) {
        if (var)
            return UtilChat.listValueOn + "True" + UtilChat.mBody;
        return UtilChat.listValueOff + "False" + UtilChat.mBody;
    }

    public static String oo(String variable, boolean value) {
        if (value)
            return UtilChat.listValueOn + variable + UtilChat.mBody;
        return UtilChat.listValueOff + variable + UtilChat.mBody;
    }

    public static String combine(String[] args, int start, String color, boolean comma) {
        if (args.length == 0)
            return "";

        String out = "";

        for (int i = start; i < args.length; i++) {
            if (color != null) {
                String preColor = ChatColor.getLastColors(args[i]);
                out += color + args[i] + preColor;
            } else
                out += args[i];

            if (comma)
                out += ", ";
            else
                out += " ";
        }

        if (out.length() > 0)
            if (comma) out = out.substring(0, out.length() - 2);
            else out = out.substring(0, out.length() - 1);

        return out;
    }
}