package com.ellisiumx.elrankup.mine.command;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilMessage;
import com.ellisiumx.elrankup.configuration.RankupConfiguration;
import com.ellisiumx.elrankup.mapedit.BlockData;
import com.ellisiumx.elrankup.mapedit.PlayerPoints;
import com.ellisiumx.elrankup.mine.MineData;
import com.ellisiumx.elrankup.utils.UtilCheck;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MineResetCommand extends CommandBase {

    public MineResetCommand(JavaPlugin javaPlugin) {
        super(javaPlugin, Rank.DEVELOPER, "minereset", "mr");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        if(args != null && args.length > 0) {
            switch (args[0]) {
                case "l":
                case "list":
                    list(caller, args);
                    break;
                case "c":
                case "create":
                    create(caller, args);
                    break;
                case "d":
                case "delete":
                    delete(caller, args);
                    break;
                case "r":
                case "reset":
                    reset(caller, args);
                    break;
                case "s":
                case "set":
                    set(caller, args);
                    break;
                case "g":
                case "get":
                    get(caller, args);
                    break;
            }
        } else {
            caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGold + "/mr list"));
            caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGold + "/mr create <name>"));
            caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGold + "/mr delete <name>"));
            caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGold + "/mr reset <name>"));
            caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGold + "/mr set <mine> <field> <value>"));
            caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGold + "/mr get <mine> <field>"));
        }
    }

    public void list(Player caller, String[] args) {
        for(int i = 0; i < RankupConfiguration.Mines.size(); i++) {
            MineData mineData = RankupConfiguration.Mines.get(i);
            if(mineData.enabled) caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGreen + "#" + i + " " + mineData.name));
            else caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cRed + "#" + i + " " + mineData.name));
        }
    }

    public void create(Player caller, String[] args) {
        if(args.length == 2) {
            try {
                PlayerPoints points = UtilCheck.getPoints(caller);
                MineData mineData = new MineData(args[1], "minaa", false, -1, points.getPoint1(), points.getPoint2(), 30);
                RankupConfiguration.Mines.add(mineData);
                RankupConfiguration.save();
                caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGreen + "'" + mineData.name + "' created!"));
            } catch (Exception ex) {
                caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cRed + ex.getMessage()));
            }
        } else {
            caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGold + "/mr create <name>"));
        }
    }

    public void delete(Player caller, String[] args) {
        if(args.length == 2) {
            boolean deleted = false;
            for(int i = 0; i < RankupConfiguration.Mines.size(); i++) {
                if(RankupConfiguration.Mines.get(i).name.equals(args[1])) {
                    RankupConfiguration.Mines.remove(i);
                    deleted = true;
                    caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGreen + "'" + args[1] + "' deleted!"));
                    break;
                }
            }
            if(!deleted) caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cRed + "'" + args[1] + "' not found!"));
            if(deleted) RankupConfiguration.save();
        } else {
            caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGold + "/mr delete <name>"));
        }
    }

    public void reset(Player caller, String[] args) {
        if(args.length == 2) {
            for(int i = 0; i < RankupConfiguration.Mines.size(); i++) {
                if(RankupConfiguration.Mines.get(i).name.equals(args[1])) {
                    RankupConfiguration.Mines.get(i).reset();
                    caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGreen + "'" + args[1] + "' reseted!"));
                    return;
                }
            }
            caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cRed + "'" + args[1] + "' not found!"));
        } else {
            caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGold + "/mr reset <name>"));
        }
    }

    public void set(Player caller, String[] args) {
        if(args.length == 4) {
            for(int i = 0; i < RankupConfiguration.Mines.size(); i++) {
                if(RankupConfiguration.Mines.get(i).name.equals(args[1])) {
                    MineData mineData = RankupConfiguration.Mines.get(i);
                    switch (args[2].toLowerCase()) {
                        case "name":
                            mineData.name = args[3];
                            RankupConfiguration.save();
                            caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGreen + "Name set: " + mineData.name));
                            return;
                        case "delay":
                            try {
                                mineData.delay = Integer.parseInt(args[3]);
                                RankupConfiguration.save();
                                caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGreen + "Delay set: " + mineData.delay));
                            } catch (NumberFormatException ex) {
                                caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cRed + "Failed to parse " + args[3]));
                            }
                            return;
                        case "alertarea":
                            try {
                                mineData.alertArea = Integer.parseInt(args[3]);
                                RankupConfiguration.save();
                                caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGreen + "Alert Area set: " + mineData.alertArea));
                            } catch (NumberFormatException ex) {
                                caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cRed + "Failed to parse " + args[3]));
                            }
                            return;
                        case "enabled":
                            try {
                                mineData.enabled = Boolean.parseBoolean(args[3]);
                                RankupConfiguration.save();
                                caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGreen + "Enabled set: " + mineData.enabled));
                            } catch (NumberFormatException ex) {
                                caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cRed + "Failed to parse " + args[3]));
                            }
                            return;
                        case "points":
                            try {
                                PlayerPoints points = UtilCheck.getPoints(caller);
                                mineData.setPoints(points.getPoint1(), points.getPoint2());
                                RankupConfiguration.save();
                                caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGreen + "Point1 set: " + RankupConfiguration.locationToString(mineData.getPoint1())));
                                caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGreen + "Point2 set: " + RankupConfiguration.locationToString(mineData.getPoint2())));
                            } catch (Exception ex) {
                                caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cRed + ex.getMessage()));
                            }
                            return;
                        case "ores":
                            mineData.getBlocks().clear();
                            for(String data : args[3].split(";")) {
                                String[] datas = args[3].split(",", 2);
                                String[] item = datas[1].split(":", 2);
                                mineData.getBlocks().put(new BlockData(Integer.parseInt(item[0]), Byte.parseByte(item[1])), Double.parseDouble(datas[0]));
                            }
                            caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGreen + "Ores set!"));
                            return;
                        default:
                            caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cRed + "Unknown field '" + args[2] + "'!"));
                            return;
                    }
                }
            }
            caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cRed + "'" + args[1] + "' not found!"));
        } else {
            caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGold + "/mr get <name> <field>"));
        }
    }

    public void get(Player caller, String[] args) {
        if(args.length == 3) {
            for(int i = 0; i < RankupConfiguration.Mines.size(); i++) {
                if(RankupConfiguration.Mines.get(i).name.equals(args[1])) {
                    MineData mineData = RankupConfiguration.Mines.get(i);
                    switch (args[2].toLowerCase()) {
                        case "name":
                            caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGreen + "Name: " + mineData.name));
                            return;
                        case "delay":
                            caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGreen + "Delay: " + mineData.delay));
                            return;
                        case "currentdelay":
                            caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGreen + "Current Delay: " + mineData.currentDelay));
                            return;
                        case "alertarea":
                            caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGreen + "Alert Area: " + mineData.alertArea));
                            return;
                        case "enabled":
                            caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGreen + "Enabled: " + mineData.enabled));
                            return;
                        case "points":
                            caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGreen + "Point1: " + RankupConfiguration.locationToString(mineData.getPoint1())));
                            caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGreen + "Point2: " + RankupConfiguration.locationToString(mineData.getPoint2())));
                            return;
                        case "ores":
                            int index = 0;
                            for(BlockData blockData : mineData.getBlocks().keySet()) {
                                caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGreen + "#" + index + " Ore: " + blockData.id + ":" + blockData.data + " - " + (mineData.getBlocks().get(blockData) * 100 ) + "%"));
                                index++;
                            }
                            return;
                        default:
                            caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cRed + "Unknown field '" + args[2] + "'!"));
                            return;
                    }
                }
            }
            caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cRed + "'" + args[1] + "' not found!"));
        } else {
            caller.sendMessage(UtilMessage.main("MineReset", UtilChat.cGold + "/mr get <name> <field>"));
        }
    }

}
