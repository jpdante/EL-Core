package com.ellisiumx.elrankup.essentials;

import com.ellisiumx.elcore.account.CoreClient;
import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.utils.*;
import com.ellisiumx.elrankup.mapedit.command.MapEditCommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class GiveCommand extends CommandBase {

    public GiveCommand(JavaPlugin plugin) {
        super(plugin, Rank.ADMIN, "give");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        if(args != null && args.length > 0) {
            if(args.length == 1) {
                try {
                    Material material = getMaterial(args[0]);
                    byte data = MapEditCommand.getData(args[0]);
                    if(material == null) {
                        caller.sendMessage(UtilMessage.main("Give", UtilChat.cRed + "Invalid input!"));
                        return;
                    }
                    caller.getInventory().addItem(new ItemStack(material, 1, data));
                } catch (Exception ex) {
                    caller.sendMessage(UtilMessage.main("Give", UtilChat.cRed + "Invalid input!"));
                }
            } else if(args.length == 2) {
                Player player = UtilPlayer.searchExact(args[0]);
                if(player == null) {
                    try {
                        Material material = getMaterial(args[0]);
                        byte data = MapEditCommand.getData(args[0]);
                        if(material == null) {
                            caller.sendMessage(UtilMessage.main("Give", UtilChat.cRed + "Invalid input!"));
                            return;
                        }
                        int quantity = Integer.parseInt(args[1]);
                        caller.getInventory().addItem(new ItemStack(material, quantity, data));
                    } catch (Exception ex) {
                        caller.sendMessage(UtilMessage.main("Give", UtilChat.cRed + "Invalid input!"));
                    }
                } else {
                    try {
                        Material material = getMaterial(args[1]);
                        byte data = MapEditCommand.getData(args[1]);
                        if(material == null) {
                            caller.sendMessage(UtilMessage.main("Give", UtilChat.cRed + "Invalid input!"));
                            return;
                        }
                        player.getInventory().addItem(new ItemStack(material, 1, data));
                    } catch (Exception ex) {
                        caller.sendMessage(UtilMessage.main("Give", UtilChat.cRed + "Invalid input!"));
                    }
                }
            } if(args.length == 3) {
                Player player = UtilPlayer.searchExact(args[0]);
                if(player == null) {
                    caller.sendMessage(UtilMessage.main("Give", UtilChat.cRed + "Player not found!"));
                    return;
                } else {
                    try {
                        Material material = getMaterial(args[1]);
                        byte data = MapEditCommand.getData(args[1]);
                        if(material == null) {
                            caller.sendMessage(UtilMessage.main("Give", UtilChat.cRed + "Invalid input!"));
                            return;
                        }
                        int quantity = Integer.parseInt(args[2]);
                        player.getInventory().addItem(new ItemStack(material, quantity, data));
                    } catch (Exception ex) {
                        caller.sendMessage(UtilMessage.main("Give", UtilChat.cRed + "Invalid input!"));
                    }
                }
            }
        } else {
            caller.sendMessage(UtilMessage.main("Give", UtilChat.cGold + "/give <item>:<data>"));
            caller.sendMessage(UtilMessage.main("Give", UtilChat.cGold + "/give <item>:<data> <quantity>"));
            caller.sendMessage(UtilMessage.main("Give", UtilChat.cGold + "/give <player> <item>:<data> <quantity>"));
        }
    }

    public Material getMaterial(String s) {
        String materialRaw = s.toUpperCase().split(":", 2)[0];
        try {
            int id = Integer.parseInt(materialRaw);
            return Material.getMaterial(id);
        } catch (NumberFormatException ignored) { }
        try {
            return Material.getMaterial(materialRaw);
        } catch (Exception ignored) { }
        return null;
    }


}