package com.ellisiumx.elrankup.machine.holder;

import com.ellisiumx.elrankup.machine.Machine;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MachineFuelMenuHolder implements InventoryHolder, MachineMenuHolder {

    public final Machine machine;

    public MachineFuelMenuHolder(Machine machine) {
        this.machine = machine;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
