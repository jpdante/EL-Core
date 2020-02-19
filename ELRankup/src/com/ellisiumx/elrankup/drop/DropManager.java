package com.ellisiumx.elrankup.drop;

import com.ellisiumx.elrankup.chat.command.ChangeChannelCommand;
import com.ellisiumx.elrankup.chat.command.TellCommand;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.drop.holder.DropsMenuHolder;
import com.ellisiumx.elrankup.drop.repository.DropRepository;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collection;
import java.util.HashMap;
import java.util.Random;
import java.util.Stack;

public class DropManager implements Listener {

    public static DropManager context;

    public DropRepository dropRepository;
    public HashMap<String, DropPickaxe> dropPickaxes;
    public HashMap<String, PlayerDrops> playerDrops;
    public Stack<PlayerDrops> updateBuffer;

    public DropManager(JavaPlugin plugin) {
        context = this;
        dropRepository = new DropRepository(plugin);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        dropPickaxes = new HashMap<>();
        playerDrops = new HashMap<>();
        updateBuffer = new Stack<>();
        new ChangeChannelCommand(plugin);
        new TellCommand(plugin);
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_AIR) return;
        if(event.getItem().getType() != Material.DIAMOND_PICKAXE) return;
        if(event.getPlayer().getOpenInventory() != null) return;
        event.getPlayer().openInventory(RankupConfiguration.DropUpgradeMenu.createMenu(new DropsMenuHolder()));
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

    public int getDropCount(Player p, Block block, int i, Random random) {
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
        event.getPlayer().giveExp(event.getExpToDrop());
        event.setExpToDrop(0);

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
            event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_PICKUP, 8.0f, 1.0f);
            return;
        }

        if (!event.getBlock().getType().toString().endsWith("ORE")) {
            for (ItemStack drop : drops) {
                int dropCount = 1;
                drop.setAmount(dropCount);
                event.getPlayer().getInventory().addItem(drop);
                event.getBlock().setType(Material.AIR);
                event.getBlock().getDrops().clear();
                event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ITEM_PICKUP, 8.0f, 1.0f);
            }

        } else {
            int i = 0;
            if (event.getPlayer().getItemInHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
                i = event.getPlayer().getItemInHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
            }
            if (event.getBlock().getType() == Material.GOLD_ORE) {
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
            }
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

}
