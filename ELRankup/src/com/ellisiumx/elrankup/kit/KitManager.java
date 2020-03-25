package com.ellisiumx.elrankup.kit;

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
import com.ellisiumx.elrankup.kit.command.KitCommand;
import com.ellisiumx.elrankup.kit.command.KitsCommand;
import com.ellisiumx.elrankup.kit.holder.KitMenuHolder;
import com.ellisiumx.elrankup.kit.repository.KitRepository;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Timestamp;
import java.time.Duration;
import java.util.*;

public class KitManager implements Listener {

    public static KitManager context;
    public KitRepository repository;
    public HashMap<String, PlayerKit> playersKits;
    public Stack<PlayerKit> updateBuffer;

    public KitManager(JavaPlugin plugin) {
        context = this;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        playersKits = new HashMap<>();
        updateBuffer = new Stack<>();
        repository = new KitRepository(plugin);
        for (LanguageDB languageDB : LanguageManager.getLanguages()) {
            languageDB.insertTranslation("KitDontExists", "&f[&aKits&f] &cThe kit '%KitName%' does not exist!");
            languageDB.insertTranslation("KitNoSpace", "&f[&aKits&f] &cYou don't have enough space to get the kit!");
            languageDB.insertTranslation("KitNoRank", "&f[&aKits&f] &cYou do not have the necessary rank to open this kit!");
            languageDB.insertTranslation("KitWaitDelay", "&f[&aKits&f] &cYou need to wait %TimeLeft% to be able to get this kit again.");
            languageDB.insertTranslation("KitOneUse", "&f[&aKits&f] &cThis kit is for single use and cannot be obtained again.");
            languageDB.insertTranslation("KitsMessage", "&f[&aKits&f] &6Kits: &f%kits%");
        }
        if (LanguageManager.saveLanguages()) LanguageManager.reloadLanguages();
        new KitCommand(plugin);
        new KitsCommand(plugin);
    }

    public static PlayerKit get(Player player) { return get(player.getName()); }

    public static PlayerKit get(String playerName) {
        return context.playersKits.get(playerName);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() == null) return;
        if (!(event.getInventory().getHolder() instanceof KitMenuHolder)) return;
        KitMenuHolder holder = (KitMenuHolder) event.getInventory().getHolder();
        ItemStack itemStack = event.getCurrentItem();
        if(itemStack == null || itemStack.getType() == Material.AIR) return;
        if (!UtilNBT.contains(itemStack, "MenuItem")) return;
        event.setCancelled(true);
        String command = UtilNBT.getString(itemStack, "MenuCommand");
        if (command == null) return;
        String[] args = command.split(" ", 2);
        if(args[0].equals("confirm")) {
            if(holder.edit) editKit((Player) event.getWhoClicked(), event.getInventory(), holder);
            else createKit((Player) event.getWhoClicked(), event.getInventory(), holder);
        }
    }

    public void createKit(Player player, Inventory inventory, KitMenuHolder kitMenuHolder) {
        ArrayList<ItemStack> items = new ArrayList<>();
        for(ItemStack itemStack : inventory.getContents()) {
            if(itemStack == null || itemStack.getType() == Material.AIR) continue;
            if (UtilNBT.contains(itemStack, "MenuItem")) continue;
            items.add(itemStack);
        }
        RankupConfiguration.Kits.put(kitMenuHolder.kitName, new Kit(kitMenuHolder.kitName, kitMenuHolder.kitName, kitMenuHolder.delay, kitMenuHolder.rank, items));
        RankupConfiguration.save();
        player.sendMessage(ChatColor.GREEN + "Kit created successfully!");
        player.closeInventory();
    }

    public void deleteKit(Player player, String kitName) {
        RankupConfiguration.Kits.remove(kitName);
        player.sendMessage(UtilMessage.main("Kits", UtilChat.cRed + "Kit '" + kitName + "' deleted!"));
    }

    public void editKit(Player player, Inventory inventory, KitMenuHolder kitMenuHolder) {
        ArrayList<ItemStack> items = new ArrayList<>();
        for(ItemStack itemStack : inventory.getContents()) {
            if(itemStack == null || itemStack.getType() == Material.AIR) continue;
            if (UtilNBT.contains(itemStack, "MenuItem")) continue;
            items.add(itemStack);
        }
        RankupConfiguration.Kits.get(kitMenuHolder.kitName).setItems(items);
        RankupConfiguration.save();
        player.sendMessage(ChatColor.GREEN + "Kit edited successfully!");
        player.closeInventory();
    }

    public void openKits(Player player) {
        StringBuilder message = new StringBuilder();
        Rank playerRank = CoreClientManager.get(player).getRank();
        for(Map.Entry<String, Kit> kits : RankupConfiguration.Kits.entrySet()) {
            if(playerRank.has(kits.getValue().getRank())) {
                message.append(kits.getValue().getKey()).append(", ");
            }
        }
        player.sendMessage(
                LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "KitsMessage")
                        .replaceAll("%kits%", message.toString())
                        .replace('&', ChatColor.COLOR_CHAR)
        );
    }

    public void openKit(Player player, String kitName) {
        if(!RankupConfiguration.Kits.containsKey(kitName)) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "KitDontExists")
                    .replaceAll("%KitName%", kitName)
                    .replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        PlayerKit playerKit = get(player);
        Kit kit = RankupConfiguration.Kits.get(kitName);
        if(!CoreClientManager.get(player).getRank().has(kit.getRank())) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "KitNoRank").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        if(!UtilInv.HasSpace(player, kit.getItems().size())) {
            player.sendMessage(LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "KitNoSpace").replace('&', ChatColor.COLOR_CHAR));
            return;
        }
        final Calendar time = new GregorianCalendar();
        if(!playerKit.getKitDelay().containsKey(kit)) {
            playerKit.getKitDelay().put(kit, time.getTimeInMillis() / 1000);
            for(ItemStack item : kit.getItems()) {
                player.getInventory().addItem(item);
            }
            if(!updateBuffer.contains(playerKit)) {
                updateBuffer.push(playerKit);
            }
        } else {
            long nextUse = getNextUse(playerKit, kit);
            if (nextUse == 0L) {
                playerKit.getKitDelay().put(kit, time.getTimeInMillis() / 1000);
                for(ItemStack item : kit.getItems()) {
                    player.getInventory().addItem(item);
                }
                if(!updateBuffer.contains(playerKit)) {
                    updateBuffer.push(playerKit);
                }
            } else if (nextUse < 0L) {
                player.sendMessage(
                        LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "KitOneUse")
                                .replace('&', ChatColor.COLOR_CHAR)
                );
            } else {
                player.sendMessage(
                        LanguageManager.getTranslation(PreferencesManager.get(player).getLanguage(), "KitWaitDelay")
                                .replace("%TimeLeft%", UtilDate.formatDateDiff(nextUse, player))
                                .replace('&', ChatColor.COLOR_CHAR)
                );
            }
        }
    }

    public long getNextUse(PlayerKit playerKit, Kit kit) {
        long lastTime = playerKit.getKitDelay().get(kit);
        long delay = kit.getDelay();
        final Calendar time = new GregorianCalendar();
        final Calendar delayTime = new GregorianCalendar();
        delayTime.setTimeInMillis(lastTime);
        delayTime.add(Calendar.SECOND, (int) delay);
        delayTime.add(Calendar.MILLISECOND, (int) ((delay * 1000.0) % 1000.0));
        if (lastTime == 0L || lastTime > time.getTimeInMillis()) {
            return 0L;
        } else if (delay < 0L) {
            return -1;
        } else if (delayTime.before(time)) {
            return 0L;
        } else {
            return delayTime.getTimeInMillis();
        }
    }

    @EventHandler
    public void onBufferElapsed(UpdateEvent event) {
        if (event.getType() == UpdateType.SLOW) {
            Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
                if(!updateBuffer.empty()) {
                    repository.updatePlayerKit(updateBuffer);
                }
            });
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Bukkit.getServer().getScheduler().runTaskAsynchronously(ELCore.getContext(), () -> {
            playersKits.put(event.getPlayer().getName(), repository.getPlayerKit(CoreClientManager.get(event.getPlayer()).getAccountId(), event.getPlayer()));
        });
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        playersKits.remove(event.getPlayer().getName());
    }
}