package com.ellisiumx.elrankup.chat;

import com.ellisiumx.elrankup.machine.MachineManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;

public class ChatManager implements Listener {

    private static ChatManager context;
    private HashMap<String, String> playerTags;

    public ChatManager(Plugin plugin) {
        context = this;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void onChatMessage(AsyncPlayerChatEvent event) {

    }

}
