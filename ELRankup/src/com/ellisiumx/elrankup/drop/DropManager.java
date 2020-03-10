package com.ellisiumx.elrankup.drop;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.lang.LanguageDB;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import com.ellisiumx.elcore.utils.*;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.drop.command.DropsCommand;
import com.ellisiumx.elrankup.drop.holder.DropsMenuHolder;
import com.ellisiumx.elrankup.drop.repository.DropRepository;
import com.ellisiumx.elrankup.economy.EconomyManager;
import com.ellisiumx.elrankup.machine.Machine;
import com.ellisiumx.elrankup.machine.MachineManager;
import com.ellisiumx.elrankup.machine.MachineOwner;
import com.ellisiumx.elrankup.mine.MineData;
import com.ellisiumx.elrankup.rankup.RankupManager;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Timestamp;
import java.util.*;

public class DropManager implements Listener {

    private static Random random = new Random();
    public static DropManager context;

    public DropRepository repository;
    public final HashMap<String, PlayerDrops> playerDrops;
    public Stack<PlayerDrops> updateBuffer;

    public DropManager(JavaPlugin plugin) {
        context = this;
        repository = new DropRepository(plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        playerDrops = new HashMap<>();
        updateBuffer = new Stack<>();
        for (LanguageDB languageDB : LanguageManager.getLanguages()) {
            // Errors
            languageDB.insertTranslation("DropNotEnoughMoney", "&cYou do not have enough money upgrade!");
            languageDB.insertTranslation("DropTransactionFailure", "&cFailed to transfer, please try again later. %ErrorMessage%");
            languageDB.insertTranslation("ItemLoreEfficiency", "&bEfficiency&f: &a%Level%");
            languageDB.insertTranslation("ItemLoreUnbreakable", "&bUnbreakable&f: &a%Level%");
            languageDB.insertTranslation("ItemLoreFortune", "&bFortune&f: &a%Level%");
            languageDB.insertTranslation("ItemLoreSilkTouch", "&bSilkTouch&f: &a%Level%");
            languageDB.insertTranslation("ItemLoreExplosion", "&bExplosion&f: &a%Level% &8(Chance &7%Chance%%&8)");
            languageDB.insertTranslation("ItemLoreLaser", "&bLaser&f: &a%Level% &8(Chance &7%Chance%%&8)");
            languageDB.insertTranslation("ItemLoreNuke", "&bNuke&f: &a%Level% &8(Chance &7%Chance%%&8)");
            languageDB.insertTranslation("ItemLoreWeasel", "&bWeasel&f: &a%Level% &8(Chance &7%Chance%%&8)");
        }
        if (LanguageManager.saveLanguages()) LanguageManager.reloadLanguages();
        new DropsCommand(plugin);
    }

    public static PlayerDrops get(Player player) {
        return get(player.getName());
    }

    public static PlayerDrops get(String playerName) {
        synchronized (context.playerDrops) {
            return context.playerDrops.get(playerName);
        }
    }

    public Inventory getUpgradeInventory(DropsMenuHolder holder) {
        Inventory inventory = RankupConfiguration.DropUpgradeMenu.createMenu(holder);
        for(int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if(itemStack == null) continue;
            if(itemStack.getType() == Material.STAINED_GLASS_PANE) continue;
            ItemMeta itemMeta = itemStack.getItemMeta();
            if(itemMeta.getLore() != null) {
                int level = 0;
                double price = 0;
                String command = UtilNBT.getString(itemStack, "MenuCommand");
                if(command == null) continue;
                if(command.equalsIgnoreCase("upgrade-efficiency")) {
                    level = holder.item.getEnchantmentLevel(Enchantment.DIG_SPEED);
                    price = (int)(RankupConfiguration.EfficiencyUpgrade * getMultiplier(level));
                } else if(command.equalsIgnoreCase("upgrade-unbreaking")) {
                    level = holder.item.getEnchantmentLevel(Enchantment.DURABILITY);
                    price = (int)(RankupConfiguration.UnbreakingUpgrade * getMultiplier(level));
                } else if(command.equalsIgnoreCase("upgrade-fortune")) {
                    level = holder.item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
                    price = (int)(RankupConfiguration.FortuneUpgrade * getMultiplier(level));
                } else if(command.equalsIgnoreCase("upgrade-silktouch")) {
                    level = holder.item.getEnchantmentLevel(Enchantment.SILK_TOUCH);
                    price = (int)(RankupConfiguration.SilktouchUpgrade * getMultiplier(level));
                } else if(command.equalsIgnoreCase("upgrade-explosion")) {
                    if (UtilNBT.contains(holder.item, "Explode")) {
                        level = UtilNBT.getInt(holder.item, "Explode");
                    }
                    price = (int)(RankupConfiguration.ExplosionUpgrade * getMultiplier(level));
                } else if(command.equalsIgnoreCase("upgrade-laser")) {
                    if (UtilNBT.contains(holder.item, "Laser")) {
                        level = UtilNBT.getInt(holder.item, "Laser");
                    }
                    price = (int)(RankupConfiguration.LaserUpgrade * getMultiplier(level));
                } else if(command.equalsIgnoreCase("upgrade-nuke")) {
                    if (UtilNBT.contains(holder.item, "Nuke")) {
                        level = UtilNBT.getInt(holder.item, "Nuke");
                    }
                    price = (int)(RankupConfiguration.NukeUpgrade * getMultiplier(level));
                } else if(command.equalsIgnoreCase("upgrade-weasel")) {
                    if (UtilNBT.contains(holder.item, "Weasel")) {
                        level = UtilNBT.getInt(holder.item, "Weasel");
                    }
                    price = (int)(RankupConfiguration.WeaselUpgrade * getMultiplier(level));
                }
                ArrayList<String> lore = new ArrayList<>();
                for(String data : itemMeta.getLore()) {
                    lore.add(parseUpgradeString(data, level, price));
                }
                itemMeta.setLore(lore);
            }
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(i, itemStack);

        }
        return inventory;
    }

    public String parseUpgradeString(String data, int currentLevel, double price) {
        return data
                .replace("%CurrentLevel%", String.valueOf(currentLevel))
                .replace("%NextLevel%", String.valueOf(currentLevel + 1))
                .replace("%Price%", String.valueOf(price));
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (event.getItem() == null) return;
        if (event.getItem().getType() != Material.DIAMOND_PICKAXE) return;
        event.getPlayer().closeInventory();
        DropsMenuHolder holder = new DropsMenuHolder();
        holder.item = event.getItem();
        holder.upgradeMode = true;
        event.getPlayer().openInventory(getUpgradeInventory(holder));
    }

    @EventHandler
    public void onBufferElapsed(UpdateEvent event) {
        if (event.getType() == UpdateType.SLOW) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
                if (!updateBuffer.empty()) {
                    repository.updatePlayerDrops(updateBuffer);
                }
            });
        }
    }

    public double getMultiplier(int level) {
        if (level <= 0) return 1;
        if (level > 10 && level <= 20) return level * 1.25;
        else if (level > 20 && level <= 30) return level * 1.5;
        else if (level > 30 && level <= 40) return level * 2;
        else if (level > 40) return level * 3;
        return level;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() == null) return;
        if (!(event.getInventory().getHolder() instanceof DropsMenuHolder)) return;
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        DropsMenuHolder holder = (DropsMenuHolder) event.getInventory().getHolder();
        if (holder.upgradeMode) {
            if (event.getCurrentItem() == null) return;
            if (UtilNBT.contains(event.getCurrentItem(), "MenuItem")) {
                ItemStack itemStack = holder.item;
                String command = UtilNBT.getString(event.getCurrentItem(), "MenuCommand");
                if (command == null) return;
                if (command.equalsIgnoreCase("upgrade-efficiency")) {
                    int level = itemStack.getItemMeta().getEnchantLevel(Enchantment.DIG_SPEED);
                    if(level >= 50) return;
                    double multiplier = getMultiplier(level);
                    if (EconomyManager.economy.has(player, (int)(RankupConfiguration.EfficiencyUpgrade * multiplier))) {
                        EconomyResponse response = EconomyManager.economy.withdrawPlayer(player, (int)(RankupConfiguration.EfficiencyUpgrade * multiplier));
                        if (response.transactionSuccess()) {
                            UtilInv.removeItemStack(player, holder.item, 1);
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            int speed = itemMeta.getEnchantLevel(Enchantment.DIG_SPEED);
                            itemMeta.addEnchant(Enchantment.DIG_SPEED, speed + 1, true);
                            itemStack.setItemMeta(itemMeta);
                        } else {
                            player.closeInventory();
                            player.sendMessage(
                                    LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropTransactionFailure")
                                            .replace("%ErrorMessage%", response.errorMessage)
                                            .replace('&', ChatColor.COLOR_CHAR)
                            );
                            return;
                        }
                    } else {
                        player.closeInventory();
                        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropNotEnoughMoney").replace('&', ChatColor.COLOR_CHAR));
                        return;
                    }
                } else if (command.equalsIgnoreCase("upgrade-unbreaking")) {
                    int level = itemStack.getItemMeta().getEnchantLevel(Enchantment.DURABILITY);
                    if (level >= 50) return;
                    double multiplier = getMultiplier(level);
                    if (EconomyManager.economy.has(player, (int)(RankupConfiguration.UnbreakingUpgrade * multiplier))) {
                        EconomyResponse response = EconomyManager.economy.withdrawPlayer(player, (int)(RankupConfiguration.UnbreakingUpgrade * multiplier));
                        if (response.transactionSuccess()) {
                            UtilInv.removeItemStack(player, holder.item, 1);
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            int durability = itemMeta.getEnchantLevel(Enchantment.DURABILITY);
                            itemMeta.addEnchant(Enchantment.DURABILITY, durability + 1, true);
                            itemStack.setItemMeta(itemMeta);
                        } else {
                            player.closeInventory();
                            player.sendMessage(
                                    LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropTransactionFailure")
                                            .replace("%ErrorMessage%", response.errorMessage)
                                            .replace('&', ChatColor.COLOR_CHAR)
                            );
                            return;
                        }
                    } else {
                        player.closeInventory();
                        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropNotEnoughMoney").replace('&', ChatColor.COLOR_CHAR));
                        return;
                    }
                } else if (command.equalsIgnoreCase("upgrade-fortune")) {
                    int level = itemStack.getItemMeta().getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS);
                    if (level >= 50) return;
                    double multiplier = getMultiplier(level);
                    if (EconomyManager.economy.has(player, (int)(RankupConfiguration.FortuneUpgrade * multiplier))) {
                        EconomyResponse response = EconomyManager.economy.withdrawPlayer(player, (int)(RankupConfiguration.FortuneUpgrade * multiplier));
                        if (response.transactionSuccess()) {
                            UtilInv.removeItemStack(player, holder.item, 1);
                            UtilInv.removeItemStack(player, holder.item, 1);
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            int lootBonus = itemMeta.getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS);
                            itemMeta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, lootBonus + 1, true);
                            itemStack.setItemMeta(itemMeta);
                        } else {
                            player.closeInventory();
                            player.sendMessage(
                                    LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropTransactionFailure")
                                            .replace("%ErrorMessage%", response.errorMessage)
                                            .replace('&', ChatColor.COLOR_CHAR)
                            );
                            return;
                        }
                    } else {
                        player.closeInventory();
                        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropNotEnoughMoney").replace('&', ChatColor.COLOR_CHAR));
                        return;
                    }
                } else if (command.equalsIgnoreCase("upgrade-silktouch")) {
                    int level = itemStack.getItemMeta().getEnchantLevel(Enchantment.SILK_TOUCH);
                    if (level >= 1) return;
                    double multiplier = getMultiplier(level);
                    if (EconomyManager.economy.has(player, (int)(RankupConfiguration.SilktouchUpgrade * multiplier))) {
                        EconomyResponse response = EconomyManager.economy.withdrawPlayer(player, (int)(RankupConfiguration.SilktouchUpgrade * multiplier));
                        if (response.transactionSuccess()) {
                            UtilInv.removeItemStack(player, holder.item, 1);
                            ItemMeta itemMeta = itemStack.getItemMeta();
                            itemMeta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
                            itemStack.setItemMeta(itemMeta);
                        } else {
                            player.closeInventory();
                            player.sendMessage(
                                    LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropTransactionFailure")
                                            .replace("%ErrorMessage%", response.errorMessage)
                                            .replace('&', ChatColor.COLOR_CHAR)
                            );
                            return;
                        }
                    } else {
                        player.closeInventory();
                        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropNotEnoughMoney").replace('&', ChatColor.COLOR_CHAR));
                        return;
                    }
                } else if (command.equalsIgnoreCase("upgrade-explosion")) {
                    int level = 0;
                    if(UtilNBT.contains(itemStack, "Explode")) level = UtilNBT.getInt(itemStack, "Explode");
                    if(level >= 50) return;
                    double multiplier = getMultiplier(level);
                    if (EconomyManager.economy.has(player, (int)(RankupConfiguration.ExplosionUpgrade * multiplier))) {
                        EconomyResponse response = EconomyManager.economy.withdrawPlayer(player, (int)(RankupConfiguration.ExplosionUpgrade * multiplier));
                        if (response.transactionSuccess()) {
                            UtilInv.removeItemStack(player, holder.item, 1);
                            if (UtilNBT.contains(itemStack, "Explode")) {
                                int i = UtilNBT.getInt(itemStack, "Explode");
                                itemStack = UtilNBT.set(itemStack, i + 1, "Explode");
                            } else {
                                itemStack = UtilNBT.set(itemStack, 1, "Explode");
                            }
                        } else {
                            player.closeInventory();
                            player.sendMessage(
                                    LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropTransactionFailure")
                                            .replace("%ErrorMessage%", response.errorMessage)
                                            .replace('&', ChatColor.COLOR_CHAR)
                            );
                            return;
                        }
                    } else {
                        player.closeInventory();
                        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropNotEnoughMoney").replace('&', ChatColor.COLOR_CHAR));
                        return;
                    }
                } else if (command.equalsIgnoreCase("upgrade-laser")) {
                    int level = 0;
                    if(UtilNBT.contains(itemStack, "Laser")) level = UtilNBT.getInt(itemStack, "Laser");
                    if(level >= 50) return;
                    double multiplier = getMultiplier(level);
                    if (EconomyManager.economy.has(player, (int)(RankupConfiguration.LaserUpgrade * multiplier))) {
                        EconomyResponse response = EconomyManager.economy.withdrawPlayer(player, (int)(RankupConfiguration.LaserUpgrade * multiplier));
                        if (response.transactionSuccess()) {
                            UtilInv.removeItemStack(player, holder.item, 1);
                            if (UtilNBT.contains(itemStack, "Laser")) {
                                int i = UtilNBT.getInt(itemStack, "Laser");
                                itemStack = UtilNBT.set(itemStack, i + 1, "Laser");
                            } else {
                                itemStack = UtilNBT.set(itemStack, 1, "Laser");
                            }
                        } else {
                            player.closeInventory();
                            player.sendMessage(
                                    LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropTransactionFailure")
                                            .replace("%ErrorMessage%", response.errorMessage)
                                            .replace('&', ChatColor.COLOR_CHAR)
                            );
                            return;
                        }
                    } else {
                        player.closeInventory();
                        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropNotEnoughMoney").replace('&', ChatColor.COLOR_CHAR));
                        return;
                    }
                } else if (command.equalsIgnoreCase("upgrade-nuke")) {
                    int level = 0;
                    if(UtilNBT.contains(itemStack, "Nuke")) level = UtilNBT.getInt(itemStack, "Nuke");
                    if(level >= 50) return;
                    double multiplier = getMultiplier(level);
                    if (EconomyManager.economy.has(player, (int)(RankupConfiguration.NukeUpgrade * multiplier))) {
                        EconomyResponse response = EconomyManager.economy.withdrawPlayer(player, (int)(RankupConfiguration.NukeUpgrade * multiplier));
                        if (response.transactionSuccess()) {
                            UtilInv.removeItemStack(player, holder.item, 1);
                            if (UtilNBT.contains(itemStack, "Nuke")) {
                                int i = UtilNBT.getInt(itemStack, "Nuke");
                                itemStack = UtilNBT.set(itemStack, i + 1, "Nuke");
                            } else {
                                itemStack = UtilNBT.set(itemStack, 1, "Nuke");
                            }
                        } else {
                            player.closeInventory();
                            player.sendMessage(
                                    LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropTransactionFailure")
                                            .replace("%ErrorMessage%", response.errorMessage)
                                            .replace('&', ChatColor.COLOR_CHAR)
                            );
                            return;
                        }
                    } else {
                        player.closeInventory();
                        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropNotEnoughMoney").replace('&', ChatColor.COLOR_CHAR));
                        return;
                    }
                } else if (command.equalsIgnoreCase("upgrade-weasel")) {
                    int level = 0;
                    if(UtilNBT.contains(itemStack, "Weasel")) level = UtilNBT.getInt(itemStack, "Weasel");
                    if(level >= 50) return;
                    double multiplier = getMultiplier(level);
                    if (EconomyManager.economy.has(player, (int)(RankupConfiguration.WeaselUpgrade * multiplier))) {
                        EconomyResponse response = EconomyManager.economy.withdrawPlayer(player, (int)(RankupConfiguration.WeaselUpgrade * multiplier));
                        if (response.transactionSuccess()) {
                            UtilInv.removeItemStack(player, holder.item, 1);
                            if (UtilNBT.contains(itemStack, "Weasel")) {
                                int i = UtilNBT.getInt(itemStack, "Weasel");
                                itemStack = UtilNBT.set(itemStack, i + 1, "Weasel");
                            } else {
                                itemStack = UtilNBT.set(itemStack, 1, "Weasel");
                            }
                        } else {
                            player.closeInventory();
                            player.sendMessage(
                                    LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropTransactionFailure")
                                            .replace("%ErrorMessage%", response.errorMessage)
                                            .replace('&', ChatColor.COLOR_CHAR)
                            );
                            return;
                        }
                    } else {
                        player.closeInventory();
                        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropNotEnoughMoney").replace('&', ChatColor.COLOR_CHAR));
                        return;
                    }
                }
                itemStack = UtilNBT.set(itemStack, 1, "CustomEnchanted");
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                List<String> lore = new ArrayList<>();
                if(itemMeta.getEnchantLevel(Enchantment.DIG_SPEED) > 0) {
                    lore.add(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ItemLoreEfficiency")
                            .replace("%Level%", String.valueOf(itemMeta.getEnchantLevel(Enchantment.DIG_SPEED)))
                            .replace('&', ChatColor.COLOR_CHAR)
                    );
                }
                if(itemMeta.getEnchantLevel(Enchantment.DURABILITY) > 0) {
                    lore.add(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ItemLoreUnbreakable")
                            .replace("%Level%", String.valueOf(itemMeta.getEnchantLevel(Enchantment.DURABILITY)))
                            .replace('&', ChatColor.COLOR_CHAR)
                    );
                }
                if(itemMeta.getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS) > 0) {
                    lore.add(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ItemLoreFortune")
                            .replace("%Level%", String.valueOf(itemMeta.getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS)))
                            .replace('&', ChatColor.COLOR_CHAR)
                    );
                }
                if(itemMeta.getEnchantLevel(Enchantment.SILK_TOUCH) > 0) {
                    lore.add(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ItemLoreSilkTouch")
                            .replace("%Level%", String.valueOf(itemMeta.getEnchantLevel(Enchantment.SILK_TOUCH)))
                            .replace('&', ChatColor.COLOR_CHAR)
                    );
                }
                if(UtilNBT.contains(itemStack, "Explode")) {
                    int value = UtilNBT.getInt(itemStack, "Explode");
                    lore.add(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ItemLoreExplosion")
                            .replace("%Level%", String.valueOf(value))
                            .replace("%Chance%", String.valueOf(value * 3.0d / 50.0d))
                            .replace('&', ChatColor.COLOR_CHAR)
                    );
                }
                if(UtilNBT.contains(itemStack, "Laser")) {
                    int value = UtilNBT.getInt(itemStack, "Laser");
                    lore.add(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ItemLoreLaser")
                            .replace("%Level%", String.valueOf(value))
                            .replace("%Chance%", String.valueOf(value * 3.0d / 50.0d))
                            .replace('&', ChatColor.COLOR_CHAR)
                    );
                }
                if(UtilNBT.contains(itemStack, "Nuke")) {
                    int value = UtilNBT.getInt(itemStack, "Nuke");
                    lore.add(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ItemLoreNuke")
                            .replace("%Level%", String.valueOf(value))
                            .replace("%Chance%", String.valueOf(value * 3.0d / 50.0d))
                            .replace('&', ChatColor.COLOR_CHAR)
                    );
                }
                if(UtilNBT.contains(itemStack, "Weasel")) {
                    int value = UtilNBT.getInt(itemStack, "Weasel");
                    lore.add(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "ItemLoreWeasel")
                            .replace("%Level%", String.valueOf(value))
                            .replace("%Chance%", String.valueOf(value * 3.0d / 50.0d))
                            .replace('&', ChatColor.COLOR_CHAR)
                    );
                }
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);
                player.getInventory().addItem(itemStack);
                holder.item = itemStack;
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.5f, 1f);
                player.openInventory(getUpgradeInventory(holder));
            } else return;
        } else {
            if (event.getCurrentItem() == null) return;
            if (UtilNBT.contains(event.getCurrentItem(), "MenuItem")) {
                String command = UtilNBT.getString(event.getCurrentItem(), "MenuCommand");
                if (command == null) return;
                int rankBoost = 0;
                int groupBoost = 0;
                int boostPercentage = 0;
                if(CoreClientManager.get(player).getRank().has(Rank.VIP)) groupBoost = 10;
                boostPercentage = rankBoost + groupBoost;
                if (command.equalsIgnoreCase("sell-mobs")) {

                } else if (command.equalsIgnoreCase("sell-ores")) {
                    long dropsQuantity = get(player).getDrops();
                    double sellPrice = dropsQuantity * RankupManager.get(player).oresPrice;
                    double boostPrice = sellPrice * (boostPercentage / 100.0d);
                    EconomyResponse response = EconomyManager.economy.depositPlayer(player, sellPrice + boostPrice);
                    if(response.transactionSuccess()) {
                        PlayerDrops playerDrops = get(player);
                        playerDrops.setDrops(0);
                        if (!updateBuffer.contains(playerDrops)) {
                            updateBuffer.push(playerDrops);
                        }
                        player.closeInventory();
                    } else {
                        player.closeInventory();
                        player.sendMessage(
                                LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropTransactionFailure")
                                        .replace("%ErrorMessage%", response.errorMessage)
                                        .replace('&', ChatColor.COLOR_CHAR)
                        );
                    }
                } else if (command.equalsIgnoreCase("sell-drops")) {
                    int dropsQuantity = 0;
                    double sellPrice = 0;
                    for(Machine machine : MachineManager.context.ownerMachines.get(CoreClientManager.get(player).getAccountId()).getMachines()) {
                        dropsQuantity += machine.getDrops();
                        sellPrice += machine.getDrops() * machine.getType().getDropPrice();
                    }
                    double boostPrice = sellPrice * (boostPercentage / 100.0d);
                    EconomyResponse response = EconomyManager.economy.depositPlayer(player, sellPrice + boostPrice);
                    if(response.transactionSuccess()) {
                        Stack<Machine> buffer = new Stack<>();
                        Date date = new Date();
                        for(Machine machine : MachineManager.context.ownerMachines.get(CoreClientManager.get(player).getAccountId()).getMachines()) {
                            machine.setDrops(0);
                            Timestamp lastMenuOpen = machine.getLastMenuOpen();
                            Timestamp newMenuOpen = new Timestamp(date.getTime());
                            long menuOpenDiference = newMenuOpen.getTime() - lastMenuOpen.getTime();
                            long diffSeconds = menuOpenDiference / 1000 % 60;
                            int dropDelay = machine.getType().getLevels().get(machine.getLevel()).getDropDelay();
                            int dropMultiplier = 0;
                            int fuel = machine.getFuel();
                            for (int i = 0; i < machine.getFuel(); i++) {
                                long result = diffSeconds - dropDelay;
                                if (result <= 0) break;
                                else {
                                    diffSeconds = result;
                                    dropMultiplier++;
                                    fuel--;
                                }
                            }
                            machine.setFuel(fuel);
                            machine.setDrops(machine.getDrops() + dropMultiplier * machine.getType().getLevels().get(machine.getLevel()).getDropQuantity());
                            machine.setLastMenuOpen(newMenuOpen);
                            buffer.add(machine);
                        }
                        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
                            MachineManager.context.repository.updateMachines(buffer);
                        });
                        player.closeInventory();
                    } else {
                        player.closeInventory();
                        player.sendMessage(
                                LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropTransactionFailure")
                                        .replace("%ErrorMessage%", response.errorMessage)
                                        .replace('&', ChatColor.COLOR_CHAR)
                        );
                    }
                } else if (command.equalsIgnoreCase("sell-farms")) {

                } else if (command.equalsIgnoreCase("sell-all")) {

                } else if (command.equalsIgnoreCase("auto-sell")) {

                }
            }
        }
    }

    public void openDrops(Player player) {
        Inventory inventory = RankupConfiguration.DropsMenu.createMenu(new DropsMenuHolder());
        int rankBoost = 0;
        int groupBoost = 0;
        int boostPercentage = 0;
        if(CoreClientManager.get(player).getRank().has(Rank.VIP)) groupBoost = 10;
        boostPercentage = rankBoost + groupBoost;
        for(int i = 0; i < inventory.getSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if(itemStack == null) continue;
            if(itemStack.getType() == Material.STAINED_GLASS_PANE) continue;
            ItemMeta itemMeta = itemStack.getItemMeta();
            if(itemMeta.getLore() != null) {
                String command = UtilNBT.getString(itemStack, "MenuCommand");
                if(command == null) continue;
                long dropsQuantity = 0;
                double sellPrice = 0;
                double boostPrice = 0;
                if(command.equalsIgnoreCase("sell-mobs")) {
                } else if(command.equalsIgnoreCase("sell-ores")) {
                    dropsQuantity = get(player).getDrops();
                    sellPrice = dropsQuantity * RankupManager.get(player).oresPrice;
                    boostPrice = sellPrice * (boostPercentage / 100.0d);
                } else if(command.equalsIgnoreCase("sell-drops")) {
                    MachineOwner machineOwner = MachineManager.context.ownerMachines.get(CoreClientManager.get(player).getAccountId());
                    if(machineOwner != null) {
                        for(Machine machine : machineOwner.getMachines()) {
                            dropsQuantity += machine.getDrops();
                            sellPrice += machine.getDrops() * machine.getType().getDropPrice();
                        }
                        boostPrice = sellPrice * (boostPercentage / 100.0d);
                    }
                } else if(command.equalsIgnoreCase("sell-farms")) {
                } else if(command.equalsIgnoreCase("sell-all")) {
                    long oresDrops = get(player).getDrops();
                    double oresSellPrice = dropsQuantity * RankupManager.get(player).oresPrice;
                    double oresBoostPrice = sellPrice * (boostPercentage / 100.0d);

                    long machineDrops = 0;
                    double machineSellPrice = 0;
                    double machineBoostPrice = 0;
                    MachineOwner machineOwner = MachineManager.context.ownerMachines.get(CoreClientManager.get(player).getAccountId());
                    if(machineOwner != null) {
                        for (Machine machine : machineOwner.getMachines()) {
                            machineDrops += machine.getDrops();
                            machineSellPrice += machine.getDrops() * machine.getType().getDropPrice();
                        }
                        machineBoostPrice = machineSellPrice * (boostPercentage / 100.0d);
                    }

                    dropsQuantity = oresDrops + machineDrops;
                    sellPrice = oresSellPrice + machineSellPrice;
                    boostPrice = oresBoostPrice + machineBoostPrice;
                }
                ArrayList<String> lore = new ArrayList<>();
                for(String data : itemMeta.getLore()) {
                    lore.add(parseDropsString(data, dropsQuantity, sellPrice, boostPercentage, boostPrice, rankBoost, groupBoost));
                }
                itemMeta.setLore(lore);
            }
            itemStack.setItemMeta(itemMeta);
            inventory.setItem(i, itemStack);

        }
        player.openInventory(inventory);
    }

    public String parseDropsString(String data, long dropsQuantity, double sellPrice, int boostPercentage, double boostPrice, int rankBoost, int groupBoost) {
        return data
                .replace("%DropsQuantity%", String.valueOf(dropsQuantity))
                .replace("%BoostPercentage%", String.valueOf(boostPercentage))
                .replace("%SellPrice%", String.valueOf(sellPrice))
                .replace("%RankBoost%", String.valueOf(rankBoost))
                .replace("%GroupBoost%", String.valueOf(groupBoost))
                .replace("%BoostPrice%", String.valueOf(boostPrice));
    }

    public int a(Player p, Block block, Random random) {
        if (p.getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
            if (p.getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) >= 9) {
                return block.getType() == Material.LAPIS_ORE || block.getType() == Material.REDSTONE_ORE ? 1 + random.nextInt(3) : 1;
            } else {
                return block.getType() == Material.LAPIS_ORE || block.getType() == Material.REDSTONE_ORE ? 3 + random.nextInt(4) : 1;
            }
        }
        return block.getType() == Material.LAPIS_ORE || block.getType() == Material.REDSTONE_ORE ? 3 + random.nextInt(4) : 1;
    }

    public int getDropCount(Player p, Block block, int i) {
        if (i > 0) {
            int j = random.nextInt(i + 2) - 1;
            if (j < 0) {
                j = 0;
            }
            return this.a(p, block, random) * (j + 1);
        } else {
            return this.a(p, block, random);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void OnBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE)) return;

        if (event.getPlayer().getItemInHand().getType().toString().endsWith("PICKAXE")) {
            ItemStack pickaxe = event.getPlayer().getItemInHand();
            short durability = pickaxe.getDurability();
            if (pickaxe.containsEnchantment(Enchantment.DURABILITY)) {
                int level = pickaxe.getEnchantmentLevel(Enchantment.DURABILITY);
                double percent = (100 / (level + 1));
                Random r = new Random();
                int result = r.nextInt(100);
                if (result <= percent) {
                    durability += 1;
                }
            } else {
                durability += 1;
            }
            pickaxe.setDurability((short) (durability));
            event.getPlayer().setItemInHand(pickaxe);
            if (pickaxe.getType() == Material.WOOD_PICKAXE) {
                if (durability >= 59) {
                    event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                }
            }
            if (pickaxe.getType() == Material.STONE_PICKAXE) {
                if (durability >= 131) {
                    event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                }
            }
            if (pickaxe.getType() == Material.IRON_PICKAXE) {
                if (durability >= 250) {
                    event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                }
            }
            if (pickaxe.getType() == Material.GOLD_PICKAXE) {
                if (durability >= 32) {
                    event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                }
            }
            if (pickaxe.getType() == Material.DIAMOND_PICKAXE) {
                if (durability >= 1560) {
                    event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                }
            }
        }

        Collection<ItemStack> drops = event.getBlock().getDrops(event.getPlayer().getItemInHand());

        if (event.getPlayer().getItemInHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
            event.getPlayer().getInventory().addItem(new ItemStack(event.getBlock().getType()));
            event.getBlock().setType(Material.AIR);
            event.getBlock().getDrops().clear();
            //event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_PICKUP, 8.0f, 1.0f);
            return;
        }

        if (!event.getBlock().getType().toString().endsWith("ORE")) {
            for (ItemStack drop : drops) {
                drop.setAmount(1);
                event.getPlayer().getInventory().addItem(drop);
                event.getBlock().setType(Material.AIR);
                event.getBlock().getDrops().clear();
                //event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_PICKUP, 8.0f, 1.0f);
            }
        } else {
            int i = 0;
            if (event.getPlayer().getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
                i = event.getPlayer().getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
            }
            int totalDrops;
            if (random.nextDouble() < 0.01) {
                totalDrops = customEnchantBreak(event.getPlayer(), event.getPlayer().getItemInHand(), event.getBlock(), i);
            } else {
                totalDrops = normalBreak(event.getPlayer(), event.getBlock(), i);
            }
            PlayerDrops playerDrops = get(event.getPlayer());
            playerDrops.addDrops(totalDrops);
            if (!updateBuffer.contains(playerDrops)) {
                updateBuffer.push(playerDrops);
            }
            /*if (event.getBlock().getType() == Material.GOLD_ORE) {
                ItemStack drop = new ItemStack(Material.GOLD_INGOT);
                int dropCount = getDropCount(event.getPlayer(), event.getBlock(), i, new Random());
                ItemStack item = drop;
                item.setAmount(dropCount);
                drop.setAmount(dropCount);
                event.getPlayer().getInventory().addItem(drop);
                event.getBlock().setType(Material.AIR);
                event.getBlock().getDrops().clear();
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_PICKUP, 8.0f, 1.0f);
                return;
            }

            if (event.getBlock().getType() == Material.IRON_ORE) {
                ItemStack drop = new ItemStack(Material.IRON_INGOT);
                int dropCount = getDropCount(event.getPlayer(), event.getBlock(), i, new Random());
                ItemStack item = drop;
                item.setAmount(dropCount);
                drop.setAmount(dropCount);
                event.getPlayer().getInventory().addItem(drop);
                event.getBlock().setType(Material.AIR);
                event.getBlock().getDrops().clear();
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_PICKUP, 8.0f, 1.0f);
                return;
            }


            for (ItemStack drop : drops) {
                int dropCount = getDropCount(event.getPlayer(), event.getBlock(), i, new Random());
                ItemStack item = drop;
                item.setAmount(dropCount);
                drop.setAmount(dropCount);
                event.getPlayer().getInventory().addItem(drop);
                event.getBlock().setType(Material.AIR);
                event.getBlock().getDrops().clear();
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_PICKUP, 8.0f, 1.0f);
                return;
            }*/
        }

        /*if(event.getBlock().getType() != Material.EMERALD_BLOCK) return;
        //Block block = event.getBlock();
        event.setCancelled(true);
        event.setExpToDrop(0);
        event.getBlock().setType(Material.AIR);
        event.getPlayer().getInventory().addItem(new ItemStack(Material.EMERALD_BLOCK));
        PlayerDrops pd = playerDrops.get(event.getPlayer().getName());
        pd.setDrops(pd.getDrops() + 1L);
        if(!updateBuffer.contains(pd)) {
            updateBuffer.push(pd);
        }*/
    }

    public int normalBreak(Player player, Block block, int lootBonus) {
        int dropCount = getDropCount(player, block, lootBonus);
        block.setType(Material.AIR);
        block.getDrops().clear();
        return dropCount;
    }

    public int customEnchantBreak(Player player, ItemStack itemStack, Block block, int lootBonus) {
        if (UtilNBT.contains(itemStack, "CustomEnchanted")) {
            List<Pair<CustomEnchantTypes, Integer>> list = new ArrayList<>();
            if (UtilNBT.contains(itemStack, "Explode"))
                list.add(new Pair<>(CustomEnchantTypes.Explosion, UtilNBT.getInt(itemStack, "Explode")));
            if (UtilNBT.contains(itemStack, "Laser"))
                list.add(new Pair<>(CustomEnchantTypes.Laser, UtilNBT.getInt(itemStack, "Laser")));
            if (UtilNBT.contains(itemStack, "Nuke"))
                list.add(new Pair<>(CustomEnchantTypes.Nuke, UtilNBT.getInt(itemStack, "Nuke")));
            if (UtilNBT.contains(itemStack, "Weasel"))
                list.add(new Pair<>(CustomEnchantTypes.Weasel, UtilNBT.getInt(itemStack, "Weasel")));
            if (list.size() <= 0) return normalBreak(player, block, lootBonus);
            Pair<CustomEnchantTypes, Integer> enchant = list.get(random.nextInt(list.size()));
            int dropCount = getDropCount(player, block, lootBonus);
            switch (enchant.getLeft()) {
                case Explosion:
                    return explosionBreak(player, block, dropCount * enchant.getRight());
                case Laser:
                    return laserBreak(player, block, dropCount * enchant.getRight());
                case Nuke:
                    return nukeBreak(player, block, dropCount * enchant.getRight());
                case Weasel:
                    return weaselBreak(player, block, dropCount * enchant.getRight());
            }
        }
        return normalBreak(player, block, lootBonus);
    }

    public int laserBreak(Player player, Block block, int dropCount) {
        int totalDrops = 0;
        Location loc = block.getLocation();
        Material type = block.getType();
        if (random.nextBoolean()) {
            for (int x = -50; x <= 50; x++) {
                Block b = loc.getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY(), loc.getBlockZ());
                if (b.getType() != type) continue;
                totalDrops += dropCount;
                b.setType(Material.AIR);
                b.getDrops().clear();
            }
        } else {
            for (int z = -50; z <= 50; z++) {
                Block b = loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ() + z);
                if (b.getType() != type) continue;
                totalDrops += dropCount;
                b.setType(Material.AIR);
                b.getDrops().clear();
            }
        }
        player.playSound(player.getLocation(), Sound.EXPLODE, 0.3f, 0.6f);
        return totalDrops;
    }

    public int nukeBreak(Player player, Block block, int dropCount) {
        int totalDrops = 0;
        Location loc = block.getLocation();
        boolean inside = false;
        for (MineData mineData : RankupConfiguration.Mines) {
            if (mineData.isInside(loc)) {
                inside = true;
                mineData.currentDelay = 1;
                for (int x = mineData.getMinX(); x <= mineData.getMaxX(); ++x) {
                    for (int y = mineData.getMinY(); y <= mineData.getMaxY(); ++y) {
                        for (int z = mineData.getMinZ(); z <= mineData.getMaxZ(); ++z) {
                            Block b = loc.getWorld().getBlockAt(x, y, z);
                            totalDrops += dropCount;
                            b.setType(Material.AIR);
                            b.getDrops().clear();
                        }
                    }
                }
                break;
            }
        }
        if (!inside) {
            Block b = loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            totalDrops += dropCount;
            b.setType(Material.AIR);
            b.getDrops().clear();
        }
        player.playSound(player.getLocation(), Sound.EXPLODE, 0.2f, 2f);
        return totalDrops;
    }

    public int weaselBreak(Player player, Block block, int dropCount) {
        int totalDrops = 0;
        Location loc = block.getLocation();
        Material type = block.getType();
        totalDrops += dropCount;
        block.setType(Material.AIR);
        block.getDrops().clear();
        for (int y = -50; y <= 50; y++) {
            Block b = loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() + y, loc.getBlockZ());
            if (b.getType() != type) continue;
            totalDrops += dropCount;
            b.setType(Material.AIR);
            b.getDrops().clear();
        }
        player.playSound(player.getLocation(), Sound.EXPLODE, 0.3f, 0.6f);
        return totalDrops;
    }

    public int explosionBreak(Player player, Block block, int dropCount) {
        int totalDrops = 0;
        Location loc = block.getLocation();
        Material type = block.getType();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    Block b = loc.getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z);
                    if (b.getType() != type) continue;
                    totalDrops += dropCount;
                    b.setType(Material.AIR);
                    b.getDrops().clear();
                }
            }
        }
        player.playSound(player.getLocation(), Sound.EXPLODE, 0.3f, 0.6f);
        return totalDrops;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            playerDrops.put(event.getPlayer().getName(), repository.getPlayerDrops(CoreClientManager.get(event.getPlayer()).getAccountId()));
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playerDrops.remove(event.getPlayer().getName());
    }

    public enum CustomEnchantTypes {
        Explosion, Laser, Nuke, Weasel
    }

}
