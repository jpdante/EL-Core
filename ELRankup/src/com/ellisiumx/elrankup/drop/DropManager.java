package com.ellisiumx.elrankup.drop;

import com.ellisiumx.elcore.ELCore;
import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.lang.LanguageDB;
import com.ellisiumx.elcore.lang.LanguageManager;
import com.ellisiumx.elcore.preferences.PreferencesManager;
import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import com.ellisiumx.elcore.utils.*;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.drop.command.DropsCommand;
import com.ellisiumx.elrankup.drop.holder.DropsMenuHolder;
import com.ellisiumx.elrankup.drop.repository.DropRepository;
import com.ellisiumx.elrankup.economy.EconomyManager;
import com.ellisiumx.elrankup.mine.MineData;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

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

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if (event.getItem() == null) return;
        if (event.getItem().getType() != Material.DIAMOND_PICKAXE) return;
        event.getPlayer().closeInventory();
        DropsMenuHolder holder = new DropsMenuHolder();
        holder.item = event.getItem();
        holder.upgradeMode = true;
        event.getPlayer().openInventory(RankupConfiguration.DropUpgradeMenu.createMenu(holder));
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

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() == null) return;
        if (!(event.getInventory().getHolder() instanceof DropsMenuHolder)) return;
        event.setCancelled(true);
        Player player = (Player) event.getWhoClicked();
        DropsMenuHolder holder = (DropsMenuHolder) event.getInventory().getHolder();
        if (holder.upgradeMode) {
            player.getInventory().remove(holder.item);
            ItemStack itemStack = holder.item;
            ItemMeta itemMeta = itemStack.getItemMeta();
            if (event.getCurrentItem() == null) return;
            if (UtilNBT.contains(event.getCurrentItem(), "MenuItem")) {
                String command = UtilNBT.getString(event.getCurrentItem(), "MenuCommand");
                if (command == null) return;
                if (command.equalsIgnoreCase("upgrade-efficiency")) {
                    if (EconomyManager.economy.has(player, RankupConfiguration.EfficiencyUpgrade)) {
                        EconomyResponse response = EconomyManager.economy.withdrawPlayer(player, RankupConfiguration.EfficiencyUpgrade);
                        if (response.transactionSuccess()) {
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
                        }
                    } else {
                        player.closeInventory();
                        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropNotEnoughMoney").replace('&', ChatColor.COLOR_CHAR));
                    }
                } else if (command.equalsIgnoreCase("upgrade-unbreaking")) {
                    if (EconomyManager.economy.has(player, RankupConfiguration.UnbreakingUpgrade)) {
                        EconomyResponse response = EconomyManager.economy.withdrawPlayer(player, RankupConfiguration.UnbreakingUpgrade);
                        if (response.transactionSuccess()) {
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
                        }
                    } else {
                        player.closeInventory();
                        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropNotEnoughMoney").replace('&', ChatColor.COLOR_CHAR));
                    }
                } else if (command.equalsIgnoreCase("upgrade-fortune")) {
                    if (EconomyManager.economy.has(player, RankupConfiguration.FortuneUpgrade)) {
                        EconomyResponse response = EconomyManager.economy.withdrawPlayer(player, RankupConfiguration.FortuneUpgrade);
                        if (response.transactionSuccess()) {
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
                        }
                    } else {
                        player.closeInventory();
                        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropNotEnoughMoney").replace('&', ChatColor.COLOR_CHAR));
                    }
                } else if (command.equalsIgnoreCase("upgrade-silktouch")) {
                    if (EconomyManager.economy.has(player, RankupConfiguration.SilktouchUpgrade)) {
                        EconomyResponse response = EconomyManager.economy.withdrawPlayer(player, RankupConfiguration.SilktouchUpgrade);
                        if (response.transactionSuccess()) {
                            itemMeta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
                            itemStack.setItemMeta(itemMeta);
                        } else {
                            player.closeInventory();
                            player.sendMessage(
                                    LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropTransactionFailure")
                                            .replace("%ErrorMessage%", response.errorMessage)
                                            .replace('&', ChatColor.COLOR_CHAR)
                            );
                        }
                    } else {
                        player.closeInventory();
                        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropNotEnoughMoney").replace('&', ChatColor.COLOR_CHAR));
                    }
                } else if (command.equalsIgnoreCase("upgrade-explosion")) {
                    if (EconomyManager.economy.has(player, RankupConfiguration.ExplosionUpgrade)) {
                        EconomyResponse response = EconomyManager.economy.withdrawPlayer(player, RankupConfiguration.ExplosionUpgrade);
                        if (response.transactionSuccess()) {
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
                        }
                    } else {
                        player.closeInventory();
                        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropNotEnoughMoney").replace('&', ChatColor.COLOR_CHAR));
                    }
                } else if (command.equalsIgnoreCase("upgrade-laser")) {
                    if (EconomyManager.economy.has(player, RankupConfiguration.LaserUpgrade)) {
                        EconomyResponse response = EconomyManager.economy.withdrawPlayer(player, RankupConfiguration.LaserUpgrade);
                        if (response.transactionSuccess()) {
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
                        }
                    } else {
                        player.closeInventory();
                        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropNotEnoughMoney").replace('&', ChatColor.COLOR_CHAR));
                    }
                } else if (command.equalsIgnoreCase("upgrade-nuke")) {
                    if (EconomyManager.economy.has(player, RankupConfiguration.NukeUpgrade)) {
                        EconomyResponse response = EconomyManager.economy.withdrawPlayer(player, RankupConfiguration.NukeUpgrade);
                        if (response.transactionSuccess()) {
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
                        }
                    } else {
                        player.closeInventory();
                        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropNotEnoughMoney").replace('&', ChatColor.COLOR_CHAR));
                    }
                } else if (command.equalsIgnoreCase("upgrade-weasel")) {
                    if (EconomyManager.economy.has(player, RankupConfiguration.WeaselUpgrade)) {
                        EconomyResponse response = EconomyManager.economy.withdrawPlayer(player, RankupConfiguration.WeaselUpgrade);
                        if (response.transactionSuccess()) {
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
                        }
                    } else {
                        player.closeInventory();
                        player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "DropNotEnoughMoney").replace('&', ChatColor.COLOR_CHAR));
                    }
                }
                itemStack = UtilNBT.set(itemStack, 1, "CustomEnchanted");
                player.getInventory().addItem(itemStack);
                holder.item = itemStack;
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 0.5f, 1f);
            } else return;
        } else {

        }
    }

    public void openDrops(Player player) {
        player.openInventory(RankupConfiguration.DropsMenu.createMenu(new DropsMenuHolder()));
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
            if (random.nextDouble() < 0.1) {
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
            for (Pair<CustomEnchantTypes, Integer> data : list) {
                Bukkit.broadcastMessage(data.getLeft().toString());
            }
            if (list.size() <= 0) return normalBreak(player, block, lootBonus);
            Pair<CustomEnchantTypes, Integer> enchant = list.get(random.nextInt(list.size()));
            Bukkit.broadcastMessage("> " + enchant.getLeft().toString());
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
        if (random.nextBoolean()) {
            for (int x = -50; x <= 50; x++) {
                Block b = loc.getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY() - 1, loc.getBlockZ());
                if (b.getType() != type) continue;
                totalDrops += dropCount;
                b.setType(Material.AIR);
                b.getDrops().clear();
            }
        } else {
            for (int z = -50; z <= 50; z++) {
                Block b = loc.getWorld().getBlockAt(loc.getBlockX(), loc.getBlockY() - 1, loc.getBlockZ() + z);
                if (b.getType() != type) continue;
                totalDrops += dropCount;
                b.setType(Material.AIR);
                b.getDrops().clear();
            }
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
