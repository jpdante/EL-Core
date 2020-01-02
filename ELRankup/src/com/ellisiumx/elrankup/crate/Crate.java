package com.ellisiumx.elrankup.crate;

import com.ellisiumx.elcore.utils.UtilFirework;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.crate.holder.CrateMenuHolder;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;

public class Crate {

    private Player player;
    private Chest chest;
    private CrateType crateType;
    private int runCount;
    private int timer;
    private int scrollIndex;
    private Inventory inventory;
    private boolean doTick;
    private boolean doFinish;
    private ArrayList<ItemStack> items;

    public Crate(Player player, Chest chest, CrateType crateType) {
        this.player = player;
        this.chest = chest;
        this.crateType = crateType;
        this.timer = 1;
        this.runCount = 0;
        this.scrollIndex = 0;
        this.doTick = true;
        this.items = new ArrayList<>(crateType.items);
        Collections.shuffle(items);
    }

    public Crate open() {
        inventory = RankupConfiguration.CrateMenu.createMenu(new CrateMenuHolder(crateType.key, crateType.name, CrateMenuHolder.CrateMenuType.CrateMenu), "%CrateName%", crateType.name);
        player.closeInventory();
        player.openInventory(inventory);
        return this;
    }

    public void animationTick() {
        if (!doTick) return;
        timer--;
        if (timer <= 0) {
            if(doFinish) {
                doTick = false;
                CrateManager.context.openCrates.remove(this);
                player.closeInventory();
                int index = scrollIndex - 1;
                if(index < 0) index = 0;
                player.getInventory().addItem(items.get(index));
                UtilFirework.playFirework(chest.getLocation().add(0.5, 1.0, 0.5), FireworkEffect.builder()
                        .trail(true)
                        .with(FireworkEffect.Type.BALL)
                        .flicker(true)
                        .withColor(Color.AQUA)
                        .withFade(Color.RED).build());
                return;
            }
            if (runCount > 10) timer = runCount - 9;
            else timer = 1;
            if (runCount >= 20) {
                doFinish = true;
                return;
            }
            player.playSound(player.getLocation(), Sound.CLICK, 1.0f, 1.0f);
            scroll();
            runCount++;
        }
    }

    private void scroll() {
        inventory.setItem(13, items.get(scrollIndex));
        int currentIndex = scrollIndex + 1;
        for (int i = 14; i < 17; i++) {
            if (currentIndex > items.size() - 1) currentIndex = 0;
            inventory.setItem(i, items.get(currentIndex));
            currentIndex++;
        }
        for (int i = 10; i < 13; i++) {
            if (currentIndex > items.size() - 1) currentIndex = 0;
            inventory.setItem(i, items.get(currentIndex));
            currentIndex++;
        }
        scrollIndex++;
        if (scrollIndex > items.size() - 1) scrollIndex = 0;
    }

}
