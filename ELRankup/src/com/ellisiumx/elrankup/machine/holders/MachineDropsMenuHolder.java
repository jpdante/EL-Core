package com.ellisiumx.elrankup.machine.holders;

import com.ellisiumx.elrankup.machine.Machine;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MachineDropsMenuHolder implements InventoryHolder, MachineMenuHolder {

    public final Machine machine;

    public MachineDropsMenuHolder(Machine machine) {
        this.machine = machine;
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
