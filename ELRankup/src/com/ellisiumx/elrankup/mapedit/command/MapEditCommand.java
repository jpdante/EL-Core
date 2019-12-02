package com.ellisiumx.elrankup.mapedit.command;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilMessage;
import com.ellisiumx.elcore.utils.UtilNBT;
import com.ellisiumx.elrankup.mapedit.MapEditManager;
import com.ellisiumx.elrankup.mapedit.PlayerPoints;
import com.ellisiumx.elrankup.mine.BlockData;
import com.ellisiumx.elrankup.mine.MineData;
import com.ellisiumx.elrankup.mine.PastedBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MapEditCommand extends CommandBase {
    public static Random rand = new Random();

    public MapEditCommand(JavaPlugin plugin) {
        super(plugin, Rank.DEVELOPER, "mapedit", "me");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        if(args != null && args.length > 0) {
            if(args.length == 1 && args[0] != null && !args[0].isEmpty()) {
                switch (args[0]) {
                    case "s":
                    case "set":
                        caller.sendMessage(UtilMessage.main("MapEdit", UtilChat.cGold + "/me set <id>:<data>,<id>,..."));
                        caller.sendMessage(UtilMessage.main("MapEdit", UtilChat.cGold + "Place blocks asynchronously"));
                        break;
                    case "t":
                    case "tool":
                        ItemStack itemStack = new ItemStack(Material.BLAZE_ROD);
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.addEnchant(Enchantment.DIG_SPEED, 1, true);
                        itemStack.setItemMeta(itemMeta);
                        itemStack = UtilNBT.set(itemStack, caller.getName(), "MapEditTool", "item", "owner");
                        caller.setItemInHand(itemStack);
                        caller.sendMessage(UtilMessage.main("MapEdit", UtilChat.cGold + "Here is your tool :)"));
                        break;
                }
            } else if(args.length == 2 && args[0] != null && !args[0].isEmpty() && args[1] != null && !args[1].isEmpty()) {
                switch (args[0]) {
                    case "s":
                    case "set":
                        setBlocks(caller, args[1]);
                        break;
                    case "t":
                    case "tool":
                        int id = getMaterial(args[1]);
                        if(id == -1) {
                            caller.sendMessage(UtilMessage.main("MapEdit", UtilChat.cRed + "This ID or Material does not exist!"));
                            return;
                        }
                        ItemStack itemStack = new ItemStack(id);
                        ItemMeta itemMeta = itemStack.getItemMeta();
                        itemMeta.addEnchant(Enchantment.DIG_SPEED, 1, true);
                        itemStack.setItemMeta(itemMeta);
                        itemStack = UtilNBT.set(itemStack, caller.getName(), "MapEditTool", "item", "owner");
                        caller.setItemInHand(itemStack);
                        break;
                }
            } else {

            }
        } else {
            caller.sendMessage(UtilMessage.main("MapEdit", UtilChat.cRed + "Invalid arguments!"));
            caller.sendMessage(UtilMessage.main("MapEdit", UtilChat.cRed + "/me <command> {args}"));
        }
    }

    public void setBlocks(Player player, String args) {
        PlayerPoints points = MapEditManager.getPlayerPoints(player);

        if(points.getPoint1() == null) {
            player.sendMessage(UtilMessage.main("MapEdit", UtilChat.cRed + "Point 1 is not defined!"));
            return;
        }

        if(points.getPoint2() == null) {
            player.sendMessage(UtilMessage.main("MapEdit", UtilChat.cRed + "Point 2 is not defined!"));
            return;
        }

        Location p1 = points.getPoint1().clone();
        Location p2 = points.getPoint2().clone();

        if(p1.getWorld() != p2.getWorld()) {
            player.sendMessage(UtilMessage.main("MapEdit", UtilChat.cRed + "The points are not in the same world!"));
            return;
        }

        if (p1.getX() > p2.getX()) {
            double x = p1.getX();
            p1.setX(p2.getX());
            p2.setX(x);
        }
        if (p1.getY() > p2.getY()) {
            double y = p1.getY();
            p1.setY(p2.getY());
            p2.setY(y);
        }
        if (p1.getZ() > p2.getZ()) {
            double z = p1.getZ();
            p1.setZ(p2.getZ());
            p2.setZ(z);
        }

        HashMap<BlockData, Double> blocks = new HashMap<>();
        try {
            String[] blockArgs = args.split(",");
            for(String blockRaw : blockArgs) {
                int id = getMaterial(blockRaw);
                if(id == -1) throw new Exception();
                blocks.put(new BlockData(id, getData(blockRaw)), 1d / (double) blockArgs.length);
            }
        } catch (Exception ex) {
            player.sendMessage(UtilMessage.main("MapEdit", UtilChat.cRed + "An error occurred converting text to blocks."));
            return;
        }

        List<MineData.CompositionEntry> probabilityMap = MineData.mapComposition(blocks);
        for (int x = p1.getBlockX(); x <= p2.getBlockX(); ++x) {
            for (int y = p1.getBlockY(); y <= p2.getBlockY(); ++y) {
                for (int z = p1.getBlockZ(); z <= p2.getBlockZ(); ++z) {
                    double r = rand.nextDouble();
                    for (MineData.CompositionEntry ce : probabilityMap) {
                        if (r <= ce.chance) {
                            PastedBlock.BlockQueue.getQueue(p1.getWorld()).add(new PastedBlock(x, y, z, ce.block.id, ce.block.data));
                            break;
                        }
                    }
                }
            }
        }
        player.sendMessage(UtilMessage.main("MapEdit", UtilChat.cGreen + "The blocks are being set asynchronously!"));
    }

    public int getMaterial(String s) {
        String materialRaw = s.split(":", 2)[0];
        try {
            int id = Integer.parseInt(materialRaw);
            return Material.getMaterial(id).getId();
        } catch (NumberFormatException ignored) { }
        try {
            return Material.getMaterial(materialRaw).getId();
        } catch (Exception ignored) { }
        return -1;
    }

    public byte getData(String s) {
        if(!s.contains(":")) return 0;
        String dataRaw = s.split(":", 2)[1];
        try {
            return Byte.parseByte(dataRaw);
        } catch (NumberFormatException ignored) { }
        return 0;
    }
}
