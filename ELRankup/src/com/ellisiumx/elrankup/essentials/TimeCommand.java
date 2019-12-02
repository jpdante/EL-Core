package com.ellisiumx.elrankup.essentials;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilInv;
import com.ellisiumx.elcore.utils.UtilMessage;
import com.ellisiumx.elcore.utils.UtilPlayer;
import com.ellisiumx.elrankup.utils.UtilWorldTime;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.LocalTime;

public class TimeCommand extends CommandBase {

    public TimeCommand(JavaPlugin plugin) {
        super(plugin, Rank.HELPER, "time");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        if (args == null || args.length == 0) {
            String time = UtilWorldTime.format24(caller.getWorld().getTime());
            caller.sendMessage(UtilMessage.main("Time", UtilChat.cGreen + caller.getWorld().getName() + "'s current time is " + time + "(" + caller.getWorld().getTime() + " ticks)"));
        } else if (args.length == 1 && args[0] != null && !args[0].isEmpty()) {
            switch (args[0].toLowerCase()) {
                case "set":
                    caller.sendMessage(UtilMessage.main("Time", UtilChat.cGold + "/time set <time or ticks>"));
                    caller.sendMessage(UtilMessage.main("Time", UtilChat.cGold + "Set world time"));
                    break;
                case "add":
                    caller.sendMessage(UtilMessage.main("Time", UtilChat.cGold + "/time add <time or ticks>"));
                    caller.sendMessage(UtilMessage.main("Time", UtilChat.cGold + "Add world time"));
                    break;
                case "day":
                    caller.sendMessage(UtilMessage.main("Time", UtilChat.cGold + "/time day"));
                    caller.sendMessage(UtilMessage.main("Time", UtilChat.cGold + "Set to day time"));
                    break;
                case "night":
                    caller.sendMessage(UtilMessage.main("Time", UtilChat.cGold + "/time night"));
                    caller.sendMessage(UtilMessage.main("Time", UtilChat.cGold + "Add to night time"));
                    break;
            }
        } else if (args.length == 2 && args[0] != null && !args[0].isEmpty() && args[1] != null && !args[1].isEmpty()) {
            long ticks = 0;
            try {
                ticks = UtilWorldTime.parse(args[1]);
            } catch (Exception ex) {
                caller.sendMessage(UtilMessage.main("Time", UtilChat.cRed + "Invalid time or ticks!"));
                return;
            }
            switch (args[0].toLowerCase()) {
                case "set":
                    serWorldTime(caller.getWorld(), ticks, false);
                    break;
                case "add":
                    serWorldTime(caller.getWorld(), ticks, true);
                    break;
                case "day":
                    serWorldTime(caller.getWorld(), UtilWorldTime.parseAlias("day"), false);
                    break;
                case "night":
                    serWorldTime(caller.getWorld(), UtilWorldTime.parseAlias("night"), false);
                    break;
            }
        } else {
            caller.sendMessage(UtilMessage.main("Time", UtilChat.cRed + "Invalid arguments!"));
            caller.sendMessage(UtilMessage.main("Time", UtilChat.cRed + "/time"));
            caller.sendMessage(UtilMessage.main("Time", UtilChat.cRed + "/time set <time or ticks>"));
            caller.sendMessage(UtilMessage.main("Time", UtilChat.cRed + "/time add <time or ticks>"));
        }
    }

    public void serWorldTime(World world, long ticks, boolean add) {
        long time = world.getTime();
        if (!add) {
            time -= time % 24000;
        }
        world.setTime(time + (add ? 0 : 24000) + ticks);
    }
}