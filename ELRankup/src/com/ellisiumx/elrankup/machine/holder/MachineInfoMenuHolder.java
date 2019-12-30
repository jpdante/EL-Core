package com.ellisiumx.elrankup.machine.holder;

import com.ellisiumx.elrankup.machine.Machine;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MachineInfoMenuHolder implements InventoryHolder, MachineMenuHolder {
    public final Machine machine;

    public MachineInfoMenuHolder(Machine machine) {
        this.machine = machine;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
