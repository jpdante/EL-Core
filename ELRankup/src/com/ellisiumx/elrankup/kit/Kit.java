package com.ellisiumx.elrankup.kit;

import com.ellisiumx.elcore.permissions.Rank;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Kit {

    private String key;
    private String name;
    private String displayName;
    private int delay;
    private List<Rank> ranks;
    private List<ItemStack> items;

    public Kit(String key, String name, String displayName, int delay, ArrayList<Rank> ranks, ArrayList<ItemStack> items) {
        this.key = key;
        this.name = name;
        this.displayName = displayName;
        this.delay = delay;
        this.ranks = ranks;
        this.items = items;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDelay() {
        return delay;
    }

    public List<Rank> getRanks() {
        return ranks;
    }

    public List<ItemStack> getItems() {
        return items;
    }
}
