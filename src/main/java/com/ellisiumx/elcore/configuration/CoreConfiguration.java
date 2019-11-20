package com.ellisiumx.elcore.configuration;

import com.ellisiumx.elcore.ELCore;

import java.util.List;

public class CoreConfiguration {
    public static String Database_Host = "127.0.0.1";
    public static String Database_Port = "3306";
    public static String Database_Database = "database";
    public static String Database_Username = "username";
    public static String Database_Password = "password";

    public static String Redis_Host = "127.0.0.1";
    public static String Redis_Port = "6379";
    public static int Redis_Database = 0;
    public static String Redis_Password = "password";

    public static boolean MemoryFixer_Enabled = false;
    public static long MemoryFixer_Min = 1024;

    public static boolean Chat_Enabled = false;
    public static boolean Chat_Announce_Vip_Join = false;

    public static List<String> Languages;

    public CoreConfiguration() {
        Database_Host = ELCore.getContext().getConfig().getString("database.host");
        Database_Port = ELCore.getContext().getConfig().getString("database.port");
        Database_Database = ELCore.getContext().getConfig().getString("database.database");
        Database_Username = ELCore.getContext().getConfig().getString("database.username");
        Database_Password = ELCore.getContext().getConfig().getString("database.password");

        Redis_Host = ELCore.getContext().getConfig().getString("redis.host");
        Redis_Port = ELCore.getContext().getConfig().getString("redis.port");
        Redis_Database = ELCore.getContext().getConfig().getInt("redis.database");
        Redis_Password = ELCore.getContext().getConfig().getString("redis.password");

        MemoryFixer_Enabled = ELCore.getContext().getConfig().getBoolean("memoryfixer.needed");
        MemoryFixer_Min = ELCore.getContext().getConfig().getLong("memoryfixer.min");

        Chat_Enabled = ELCore.getContext().getConfig().getBoolean("chat.needed");
        Chat_Announce_Vip_Join = ELCore.getContext().getConfig().getBoolean("chat.announce_vip_join");

        Languages = ELCore.getContext().getConfig().getStringList("languages");
    }
}
