package com.ellisiumx.elrankup.machine;

import com.ellisiumx.elrankup.machine.repository.MachineRepository;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class MachineManager implements Listener {

    private MachineRepository machineRepository;

    public MachineManager(JavaPlugin plugin) {
        machineRepository = new MachineRepository(plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

}
