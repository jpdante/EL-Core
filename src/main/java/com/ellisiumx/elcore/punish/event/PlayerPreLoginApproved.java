package com.ellisiumx.elcore.punish.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;

public class PlayerPreLoginApproved extends Event {
    private static final HandlerList handlers = new HandlerList();

    public final AsyncPlayerPreLoginEvent event;

    public PlayerPreLoginApproved(AsyncPlayerPreLoginEvent event) {
        this.event = event;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}