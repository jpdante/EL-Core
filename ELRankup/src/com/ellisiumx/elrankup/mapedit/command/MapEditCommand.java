package com.ellisiumx.elrankup.mapedit.command;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilMessage;
import com.ellisiumx.elcore.utils.UtilNBT;
import com.ellisiumx.elrankup.mapedit.*;
import com.ellisiumx.elrankup.utils.UtilCheck;
import net.minecraft.server.v1_8_R3.ExceptionWorldConflict;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
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
            if(args.length == 1) {
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
            } else {
                switch (args[0]) {
                    case "s":
                    case "set":
                        setBlocks(caller, args);
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
            }
        } else {
            caller.sendMessage(UtilMessage.main("MapEdit", UtilChat.cRed + "Invalid arguments!"));
            caller.sendMessage(UtilMessage.main("MapEdit", UtilChat.cRed + "/me <command> {args}"));
        }
    }

    public void setBlocks(Player player, String[] args) {
        try {
            PlayerPoints points = UtilCheck.getPoints(player);
            ArrayList<BlockData> blocks = new ArrayList<>();
            String[] blockArgs = args[1].split(",");
            for(String blockRaw : blockArgs) {
                int id = getMaterial(blockRaw);
                if(id == -1) throw new ExceptionWorldConflict("An error occurred converting text to blocks.");
                blocks.add(new BlockData(id, getData(blockRaw)));
            }
            boolean async = true;
            for(String argument : args) {
                if (argument.contains("nonasync")) {
                    async = false;
                    break;
                }
            }
            MapEditor.setRetangle(points.getPoint1(), points.getPoint2(), async, blocks.toArray(new BlockData[0]));
            if(async) player.sendMessage(UtilMessage.main("MapEdit", UtilChat.cGreen + "The blocks are being set asynchronously!"));
            else player.sendMessage(UtilMessage.main("MapEdit", UtilChat.cGreen + "The blocks have been successfully set!"));
        } catch (Exception ex) {
            player.sendMessage(UtilMessage.main("MapEdit", UtilChat.cRed + ex.getMessage()));
        }
    }

    public static int getMaterial(String s) {
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

    public static byte getData(String s) {
        if(!s.contains(":")) return 0;
        String dataRaw = s.split(":", 2)[1];
        try {
            return Byte.parseByte(dataRaw);
        } catch (NumberFormatException ignored) { }
        return 0;
    }
}
