package com.ellisiumx.elcore.recharge;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RechargedEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private Player _player;
    private String _ability;

    public RechargedEvent(Player player, String ability) {
        _player = player;
        _ability = ability;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return _player;
    }

    public String getAbility() {
        return _ability;
    }
}

