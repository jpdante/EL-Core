package com.ellisiumx.elrankup.kit;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class KitManager implements Listener {

    public static KitManager context;

    public KitManager(JavaPlugin plugin) {
        context = this;
    }

}