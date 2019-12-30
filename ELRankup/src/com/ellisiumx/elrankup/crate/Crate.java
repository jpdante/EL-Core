package com.ellisiumx.elrankup.crate;

import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.crate.holder.CrateMenuHolder;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Crate {

    private Player player;
    private Chest chest;
    private CrateType crateType;
    private int timerMax;
    private int timer;
    private int scrollIndex;
    private Inventory inventory;

    public Crate(Player player, Chest chest, CrateType crateType) {
        this.player = player;
        this.chest = chest;
        this.crateType = crateType;
        timer = 10;
        timerMax = 10;
        scrollIndex = 0;
    }

    public void open() {
        inventory = RankupConfiguration.CrateMenu.createMenu(new CrateMenuHolder(crateType.key, crateType.name, CrateMenuHolder.CrateMenuType.CrateMenu));
        player.closeInventory();
        player.openInventory(inventory);
    }

    public void animationTick() {
        if(timer > 50) return;
        scroll();
        timer++;
    }

    private void scroll() {
        inventory.setItem(13, crateType.items.get(scrollIndex));
        int currentIndex = scrollIndex;
        for(int i = 14; i < 16; i++) {
            if(currentIndex > crateType.items.size() - 1) currentIndex = 0;
            inventory.setItem(i, crateType.items.get(currentIndex));
            currentIndex++;
        }
        for(int i = 10; i < 12; i++) {
            if(currentIndex > crateType.items.size() - 1) currentIndex = 0;
            inventory.setItem(i, crateType.items.get(currentIndex));
            currentIndex++;
        }
        scrollIndex++;
        if(scrollIndex > crateType.items.size() - 1) scrollIndex = 0;
    }

}
