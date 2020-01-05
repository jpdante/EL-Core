package com.ellisiumx.elrankup.chat;

import org.bukkit.entity.Player;

public class PlayerChat {

    public Player player;
    public ChatChannel currentChannel;
    public String formatedTags;

    public PlayerChat(Player player, ChatChannel currentChannel) {
        this.player = player;
        this.currentChannel = currentChannel;
    }

}
