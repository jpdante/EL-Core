package com.ellisiumx.elrankup.machine;

import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.machine.holders.MachineMachinesMenuHolder;
import com.ellisiumx.elrankup.machine.holders.MachineMainMenuHolder;
import org.bukkit.inventory.Inventory;

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
        return RankupConfiguration.MainMenu.createMenu(new MachineMachinesMenuHolder());
    }
}
