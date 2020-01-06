package com.ellisiumx.elrankup.configuration;

import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elrankup.ELRankup;
import com.ellisiumx.elrankup.chat.ChatChannel;
import com.ellisiumx.elrankup.crate.CrateType;
import com.ellisiumx.elrankup.machine.MachineType;
import com.ellisiumx.elrankup.mapedit.BlockData;
import com.ellisiumx.elrankup.mine.MineData;
import com.ellisiumx.elcore.utils.UtilConvert;
import com.ellisiumx.elrankup.rankup.RankLevel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import javax.rmi.CORBA.Util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RankupConfiguration {

    public static boolean MinesEnabled;
    public static List<Integer> AlertTimes;
    public static List<MineData> Mines;

    public static ItemStack Fuel;
    public static List<MachineType> MachineTypes;

    public static MenuConfig MachineMainMenu;
    public static MenuConfig MachineShopMenu;
    public static MenuConfig MachineMenu;
    public static MenuConfig MachineInfoMenu;
    public static MenuConfig MachineDropsMenu;
    public static MenuConfig MachineFuelMenu;
    public static MenuConfig MachinePermissionsMenu;
    public static MenuConfig MachineFriendsMenu;

    public static String DefaultRank;
    public static List<RankLevel> Ranks;

    public static List<CrateType> CrateTypes;
    public static List<Location> CrateChestLocations;
    public static MenuConfig CrateMenu;

    public static List<ChatChannel> ChatChannels;
    public static ChatChannel defaultChatChannel;
    public static double minTellPrice;

    public static double clanCreationPrice;

    public RankupConfiguration() {
        FileConfiguration config = ELRankup.getContext().getConfig();

        MinesEnabled = config.getBoolean("mine-reseter.enabled");
        AlertTimes = config.getIntegerList("mine-reseter.alert-times");
        Mines = new ArrayList<>();
        for (String key : config.getConfigurationSection("mine-reseter.mines").getKeys(false)) {
            String name = config.getString("mine-reseter.mines." + key + ".name");
            boolean enabled = config.getBoolean("mine-reseter.mines." + key + ".enabled");
            int alertArea = config.getInt("mine-reseter.mines." + key + ".alert-area");
            Location point1 = UtilConvert.getLocationFromString(config.getString("mine-reseter.mines." + key + ".point1"));
            Location point2 = UtilConvert.getLocationFromString(config.getString("mine-reseter.mines." + key + ".point2"));
            int delay = config.getInt("mine-reseter.mines." + key + ".delay");
            MineData mineData = new MineData(key, name, enabled, alertArea, point1, point2, delay);
            for (String ore : config.getStringList("mine-reseter.mines." + key + ".ores")) {
                String[] datas = ore.split(",", 2);
                String[] item = datas[1].split(":", 2);
                mineData.getBlocks().put(new BlockData(Integer.parseInt(item[0]), Byte.parseByte(item[1])), Double.parseDouble(datas[0]));
            }
            Mines.add(mineData);
        }

        Fuel = UtilConvert.getItemStackFromConfig(config.getConfigurationSection("machines.fuel"));
        MachineTypes = new ArrayList<>();
        for (String key : config.getConfigurationSection("machines.types").getKeys(false)) {
            //try {
            String name = config.getString("machines.types." + key + ".name").replace('&', ChatColor.COLOR_CHAR);
            double price = config.getDouble("machines.types." + key + ".price");
            ItemStack drop = UtilConvert.getItemStackFromConfig(config.getConfigurationSection("machines.types." + key + ".drop"));
            ItemStack item = UtilConvert.getItemStackFromConfig(config.getConfigurationSection("machines.types." + key + ".item"));
            double dropPrice = config.getDouble("machines.types." + key + ".drop.price");
            ArrayList<MachineType.MachineLevel> levels = new ArrayList<>();
            for (String key2 : config.getConfigurationSection("machines.types." + key + ".levels").getKeys(false)) {
                int dropDelay = config.getInt("machines.types." + key + ".levels." + key2 + ".drop-delay");
                int dropQuantity = config.getInt("machines.types." + key + ".levels." + key2 + ".drop-quantity");
                int maxTank = config.getInt("machines.types." + key + ".levels." + key2 + ".max-tank");
                int maxDropCount = config.getInt("machines.types." + key + ".levels." + key2 + ".max-drop-count");
                double upgradeCost = config.getDouble("machines.types." + key + ".levels." + key2 + ".upgrade-cost");
                levels.add(new MachineType.MachineLevel(dropQuantity, dropDelay, maxTank, maxDropCount, upgradeCost));
            }
            MachineTypes.add(new MachineType(key, name, price, item, drop, dropPrice, levels));
            //} catch (Exception ex) {
            //    ex.printStackTrace();
            //}
        }
        MachineMainMenu = new MenuConfig(config.getConfigurationSection("machines.menus.main"));
        MachineShopMenu = new MenuConfig(config.getConfigurationSection("machines.menus.shop"));
        MachineMenu = new MenuConfig(config.getConfigurationSection("machines.menus.machines"));
        MachineInfoMenu = new MenuConfig(config.getConfigurationSection("machines.menus.machine"));
        MachineDropsMenu = new MenuConfig(config.getConfigurationSection("machines.menus.drops"));
        MachineFuelMenu = new MenuConfig(config.getConfigurationSection("machines.menus.fuel"));

        DefaultRank = config.getString("rankup.default-rank");
        Ranks = new ArrayList<>();
        for (String key : config.getConfigurationSection("rankup.ranks").getKeys(false)) {
            String rankName = config.getString("rankup.ranks." + key + ".name");
            String rankDisplayName = config.getString("rankup.ranks." + key + ".display-name");
            String color = config.getString("rankup.ranks." + key + ".color");
            double cost = config.getDouble("rankup.ranks." + key + ".cost");
            boolean canLevelUp = config.getBoolean("rankup.ranks." + key + ".can-level-up");
            Ranks.add(new RankLevel(rankName, rankDisplayName, color, cost, canLevelUp));
        }

        CrateTypes = new ArrayList<>();
        if(config.getConfigurationSection("crates.types") != null) {
            for (String key : config.getConfigurationSection("crates.types").getKeys(false)) {
                String name = config.getString("crates.types." + key + ".name").replace('&', ChatColor.COLOR_CHAR);
                List<String> itemsRaw = config.getStringList("crates.types." + key + ".items");
                ArrayList<ItemStack> items = new ArrayList<>();
                for(String item : itemsRaw) {
                    items.add(UtilConvert.deserializeItemStack(item));
                }
                CrateTypes.add(new CrateType(key, name, items));
            }
        }
        CrateChestLocations = new ArrayList<>();
        if(config.getStringList("crates.chests") != null) {
            for(String location : config.getStringList("crates.chests")) {
                CrateChestLocations.add(UtilConvert.getLocationFromString(location));
            }
        }
        CrateMenu = new MenuConfig(config.getConfigurationSection("crates.menus.main"));

        ChatChannels = new ArrayList<>();
        for (String key : config.getConfigurationSection("chat.channels").getKeys(false)) {
            String tag = config.getString("chat.channels." + key + ".tag");
            String format = config.getString("chat.channels." + key + ".format");
            int distance = config.getInt("chat.channels." + key + ".distance");
            boolean multiWorld = config.getBoolean("chat.channels." + key + ".multi-world");
            double minPrice = config.getDouble("chat.channels." + key + ".min-price");
            ChatChannels.add(new ChatChannel(key, tag, format, distance, multiWorld, minPrice));
        }
        for(ChatChannel chatChannel : ChatChannels) {
            if(chatChannel.tag.equalsIgnoreCase(config.getString("chat.default-channel"))) {
                defaultChatChannel = chatChannel;
                break;
            }
        }
        if(defaultChatChannel == null) defaultChatChannel = ChatChannels.get(0);
        minTellPrice = config.getDouble("chat.tell-min-price");

        clanCreationPrice = config.getDouble("clans.create-price");
    }

    public static void save() {
        ELRankup.getContext().getConfig().set("mine-reseter.mines", null);
        for (MineData mine : Mines) {
            ELRankup.getContext().getConfig().set("mine-reseter.mines." + mine.key + ".name", mine.name);
            ELRankup.getContext().getConfig().set("mine-reseter.mines." + mine.key + ".enabled", mine.enabled);
            ELRankup.getContext().getConfig().set("mine-reseter.mines." + mine.key + ".alert-area", mine.alertArea);
            ELRankup.getContext().getConfig().set("mine-reseter.mines." + mine.key + ".point1", UtilConvert.getStringFromLocation(mine.getPoint1()));
            ELRankup.getContext().getConfig().set("mine-reseter.mines." + mine.key + ".point2", UtilConvert.getStringFromLocation(mine.getPoint2()));
            ELRankup.getContext().getConfig().set("mine-reseter.mines." + mine.key + ".delay", mine.delay);
            List<String> ores = new ArrayList<>();
            for (BlockData blockData : mine.getBlocks().keySet()) {
                ores.add(mine.getBlocks().get(blockData) + "," + blockData.id + ":" + blockData.data);
            }
            ELRankup.getContext().getConfig().set("mine-reseter.mines." + mine.key + ".ores", ores);
        }

        ELRankup.getContext().getConfig().set("crates.types", null);
        for (CrateType crateType : CrateTypes) {
            ELRankup.getContext().getConfig().set("crates.types." + crateType.key + ".name", crateType.name);
            ArrayList<String> items = new ArrayList<>();
            for(ItemStack itemStack : crateType.items) {
                items.add(UtilConvert.serializeItemStack(itemStack));
            }
            ELRankup.getContext().getConfig().set("crates.types." + crateType.key + ".items", items);
        }
        ArrayList<String> crateChestsLocations = new ArrayList<>();
        for(Location location : CrateChestLocations) {
            crateChestsLocations.add(UtilConvert.getStringFromLocation(location));
        }
        ELRankup.getContext().getConfig().set("crates.chests", crateChestsLocations);

        ELRankup.getContext().saveConfig();
    }

    public static MachineType getMachineTypeByName(String name) {
        for (MachineType machineType : MachineTypes) {
            if (machineType.getKey().equalsIgnoreCase(name)) return machineType;
        }
        return null;
    }

    public static RankLevel getRankLevelByName(String name) {
        for (RankLevel rankLevel : Ranks) {
            if (rankLevel.name.equalsIgnoreCase(name)) return rankLevel;
        }
        return null;
    }

    public static CrateType getCrateTypeByName(String name) {
        for (CrateType crateType : CrateTypes) {
            if (crateType.key.equalsIgnoreCase(name)) return crateType;
        }
        return null;
    }

    public static ChatChannel getChatChannel(String data) {
        for (ChatChannel chatChannel : ChatChannels) {
            if (chatChannel.tag.equalsIgnoreCase(data) || chatChannel.key.equalsIgnoreCase(data)) return chatChannel;
        }
        return null;
    }
}
