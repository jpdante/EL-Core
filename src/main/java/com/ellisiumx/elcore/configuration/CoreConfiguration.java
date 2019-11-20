package com.ellisiumx.elcore.configuration;

import com.ellisiumx.elcore.ELCore;

public class CoreConfiguration {
    public String Database_Host = "127.0.0.1";
    public String Database_Port = "3306";
    public String Database_Database = "database";
    public String Database_Username = "username";
    public String Database_Password = "password";

    public String Redis_Host = "127.0.0.1";
    public String Redis_Port = "6379";
    public int Redis_Database = 0;
    public String Redis_Password = "password";

    public boolean MemoryFixer_Enabled = false;
    public long MemoryFixer_Min = 1024;

    public boolean Chat_Enabled = false;
    public boolean Chat_Announce_Vip_Join = false;

    /*public boolean Inject_Enabled = false;
    public boolean Intercept_PlayerChat = false;
    public boolean Intercept_PlayerCommandPreProcess = false;
    public boolean Intercept_PlayerJoin = false;
    public boolean Intercept_PlayerQuit = false;*/

    public CoreConfiguration(ELCore context) {
        Database_Host = context.getConfig().getString("database.host");
        Database_Port = context.getConfig().getString("database.port");
        Database_Database = context.getConfig().getString("database.database");
        Database_Username = context.getConfig().getString("database.username");
        Database_Password = context.getConfig().getString("database.password");

        Redis_Host = context.getConfig().getString("redis.host");
        Redis_Port = context.getConfig().getString("redis.port");
        Redis_Database = context.getConfig().getInt("redis.database");
        Redis_Password = context.getConfig().getString("redis.password");

        MemoryFixer_Enabled = context.getConfig().getBoolean("memoryfixer.needed");
        MemoryFixer_Min = context.getConfig().getLong("memoryfixer.min");

        Chat_Enabled = context.getConfig().getBoolean("chat.needed");
        Chat_Announce_Vip_Join = context.getConfig().getBoolean("chat.announce_vip_join");

        //Inject_Enabled = context.getConfig().getBoolean("injector.enabled");
    }
}
