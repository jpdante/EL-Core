package com.ellisiumx.elrankup.essentials;

import com.ellisiumx.elcore.command.CommandBase;
import com.ellisiumx.elcore.command.CommandCenter;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilMessage;
import com.ellisiumx.elcore.utils.UtilPlayer;
import com.ellisiumx.elrankup.economy.EconomyManager;
import net.minecraft.server.v1_8_R3.Tuple;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SpeedCommand extends CommandBase {

    public SpeedCommand(JavaPlugin plugin) {
        super(plugin, Rank.HELPER, "speed");
        this.setCommandCenter(CommandCenter.context);
        CommandCenter.addCommand(this);
    }

    @Override
    public void execute(Player caller, String[] args) {
        if(args != null && args.length == 1 && args[0] != null && !args[0].isEmpty()) {
            float speed = getMoveSpeed(args[0], !caller.isFlying());
            if(speed == -1) {
                caller.sendMessage(UtilMessage.main("Speed", UtilChat.cRed + "Invalid arguments!"));
                caller.sendMessage(UtilMessage.main("Speed", UtilChat.cRed + "/speed <number>"));
            } else {
                setSpeed(caller, speed);
                caller.sendMessage(UtilMessage.main("Speed", UtilChat.cGreen + "Speed set!"));
            }
        } else if(args != null && args.length == 2 && args[0] != null && !args[0].isEmpty() && args[1] != null && !args[1].isEmpty()) {
            Player player = UtilPlayer.searchExact(args[0]);
            if(player != null) {
                float speed = getMoveSpeed(args[0], !player.isFlying());
                if(speed == -1) {
                    setSpeed(player, speed);
                    caller.sendMessage(UtilMessage.main("Speed", UtilChat.cGreen + args[0] + "' Speed set!"));
                } else {
                    caller.sendMessage(UtilMessage.main("Speed", UtilChat.cRed + "Invalid arguments!"));
                    caller.sendMessage(UtilMessage.main("Speed", UtilChat.cRed + "/speed <player> <number>"));
                }
            } else {
                caller.sendMessage(UtilMessage.main("Speed", UtilChat.cRed + "Player '" + args[0] + "' does not exist!"));
            }
        } else {
            caller.sendMessage(UtilMessage.main("Speed", UtilChat.cRed + "Invalid arguments!"));
            caller.sendMessage(UtilMessage.main("Speed", UtilChat.cRed + "/speed <number>"));
            caller.sendMessage(UtilMessage.main("Speed", UtilChat.cRed + "/speed <player> <number>"));
        }
    }

    public void setSpeed(Player player, float speed) {
        if(player.isFlying()) {
            player.setFlySpeed(speed);
        } else {
            player.setWalkSpeed(speed);
        }
    }

    private float getMoveSpeed(final String moveSpeed, boolean isWalking) {
        float userSpeed;
        try {
            userSpeed = Float.parseFloat(moveSpeed);
            userSpeed /= 10.0f;
            if(isWalking && userSpeed != 0.0f) {
                userSpeed += 0.1f;
            }
            if (userSpeed > 1.0f) {
                userSpeed = 1.0f;
            } else if (userSpeed < 0.0001f) {
                userSpeed = 0.0001f;
            }
        } catch (NumberFormatException e) {
            return -1;
        }
        return userSpeed;
    }
}