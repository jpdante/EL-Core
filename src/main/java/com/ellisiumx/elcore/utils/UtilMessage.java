package com.ellisiumx.elcore.utils;

import org.bukkit.ChatColor;

public class UtilMessage {
    public static String main(String module, String body) {
        return UtilColors.cWhite + "[" + UtilColors.mHead + module + UtilColors.cWhite + "] " + UtilColors.mBody + body;
    }

    public static String tute(String sender, String body) {
        return UtilColors.cGold + sender + "> " + UtilColors.cWhite + body;
    }

    public static String te(String message) {
        return UtilColors.cYellow + message + UtilColors.cWhite;
    }

    public static String game(String elem) {
        return UtilColors.mGame + elem + UtilColors.mBody;
    }

    public static String ta(String message) {
        return UtilColors.cGreen + message + UtilColors.cWhite;
    }

    public static String ts(String message) {
        return UtilColors.cGold + message + UtilColors.cWhite;
    }

    public static String sys(String head, String body) {
        return UtilColors.sysHead + head + "> " + UtilColors.sysBody + body;
    }

    public static String elem(String elem) {
        return UtilColors.mElem + elem + ChatColor.RESET + UtilColors.mBody;
    }

    public static String name(String elem) {
        return UtilColors.mElem + elem + UtilColors.mBody;
    }

    public static String count(String elem) {
        return UtilColors.mCount + elem + UtilColors.mBody;
    }

    public static String item(String elem) {
        return UtilColors.mItem + elem + UtilColors.mBody;
    }

    public static String link(String elem) {
        return UtilColors.mLink + elem + UtilColors.mBody;
    }

    public static String skill(String elem) {
        return UtilColors.mSkill + elem + UtilColors.mBody;
    }

    public static String skill(String a, String b) {

        return UtilColors.cYellow + " " + UtilColors.cGreen + b + UtilColors.mBody;
    }

    public static String time(String elem) {
        return UtilColors.mTime + elem + UtilColors.mBody;
    }

    public static String desc(String head, String body) {
        return UtilColors.descHead + head + ": " + UtilColors.descBody + body;
    }

    public static String wField(String field, String data) {
        return UtilColors.wFrame + "[" + UtilColors.wField + field + UtilColors.wFrame + "] " + UtilColors.mBody + data + " ";
    }

    public static String help(String cmd, String body, Rank rank) {
        return rank.GetColor() + cmd + " " + UtilColors.mBody + body + " " + rank(rank);
    }

    public static String rank(Rank rank) {
        if (rank == Rank.NONE)
            return rank.GetColor() + "Player";

        return rank.GetTag(false, false);
    }

    public static String value(String variable, String value) {
        return value(0, variable, value);
    }

    public static String value(int a, String variable, String value) {
        String indent = "";
        while (indent.length() < a)
            indent += ChatColor.GRAY + ">";

        return indent + UtilColors.listTitle + variable + ": " + UtilColors.listValue + value;
    }

    public static String value(String variable, String value, boolean on) {
        return value(0, variable, value, on);
    }

    public static String value(int a, String variable, String value, boolean on) {
        String indent = "";
        while (indent.length() < a)
            indent += ChatColor.GRAY + ">";

        if (on) return indent + UtilColors.listTitle + variable + ": " + UtilColors.listValueOn + value;
        else return indent + UtilColors.listTitle + variable + ": " + UtilColors.listValueOff + value;
    }

    public static String ed(boolean var) {
        if (var)
            return UtilColors.listValueOn + "Enabled" + UtilColors.mBody;
        return UtilColors.listValueOff + "Disabled" + UtilColors.mBody;
    }

    public static String oo(boolean var) {
        if (var)
            return UtilColors.listValueOn + "On" + UtilColors.mBody;
        return UtilColors.listValueOff + "Off" + UtilColors.mBody;
    }

    public static String tf(boolean var) {
        if (var)
            return UtilColors.listValueOn + "True" + UtilColors.mBody;
        return UtilColors.listValueOff + "False" + UtilColors.mBody;
    }

    public static String oo(String variable, boolean value) {
        if (value)
            return UtilColors.listValueOn + variable + UtilColors.mBody;
        return UtilColors.listValueOff + variable + UtilColors.mBody;
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