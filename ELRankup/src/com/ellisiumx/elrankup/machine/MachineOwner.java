package com.ellisiumx.elrankup.machine;

import com.ellisiumx.elcore.utils.UtilNBT;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.machine.holder.MachineListMenuHolder;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class MachineOwner {

    private int owner;
    private ArrayList<Machine> machines;

    public MachineOwner(int owner) {
        this.owner = owner;
        machines = new ArrayList<>();
    }

    public int getOwner() {
        return owner;
    }

    public ArrayList<Machine> getMachines() {
        return machines;
    }

    public void addMachine(Machine machine) {
        machines.add(machine);
    }

    public void removeMachine(Machine machine) {
        machines.remove(machine);
    }

    public boolean containsMachine(Machine machine) {
        return machines.contains(machine);
    }

    public int getCountMachines() {
        return machines.size();
    }

    public int getCountMachineType(MachineType machineType) {
        int count = 0;
        for(Machine machine : machines) {
            if(machine.getType() == machineType) count++;
        }
        return count;
    }

    public Inventory getMachinesMenu() {
        Inventory inventory = RankupConfiguration.MachineMenu.createMenu(new MachineListMenuHolder());
        if(machines.size() > 0) {
            int machineIndex = 0;
            for(int i = 0; i < inventory.getSize(); i++) {
                ItemStack itemStack = inventory.getItem(i);
                if(itemStack == null) continue;
                if(!UtilNBT.contains(itemStack, "MenuCommand")) continue;
                String command = UtilNBT.getString(itemStack, "MenuCommand");
                if(command == null || !command.equalsIgnoreCase("emptyslot")) continue;
                if(machineIndex >= machines.size()) continue;
                Machine machine = machines.get(machineIndex);
                itemStack = machine.getType().getItem();
                itemStack.setAmount(1);
                itemStack = UtilNBT.set(itemStack, "true", "MenuItem");
                itemStack = UtilNBT.set(itemStack, "open machine " + machine.getId(), "MenuCommand");
                inventory.setItem(i, itemStack);
                machineIndex++;
            }
        }
        return inventory;
    }
}
