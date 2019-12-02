package com.ellisiumx.elrankup.mapedit;

import com.ellisiumx.elcore.account.CoreClient;
import com.ellisiumx.elcore.account.CoreClientManager;
import com.ellisiumx.elcore.permissions.Rank;
import com.ellisiumx.elcore.utils.UtilChat;
import com.ellisiumx.elcore.utils.UtilMessage;
import com.ellisiumx.elcore.utils.UtilNBT;
import com.ellisiumx.elrankup.mapedit.command.MapEditCommand;
import net.minecraft.server.v1_8_R3.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import javax.rmi.CORBA.Util;
import java.util.HashMap;

public class MapEditManager implements Listener {

    private static MapEditManager context;

    private HashMap<String, PlayerPoints> points;

    public MapEditManager(JavaPlugin plugin) {
        context = this;
        points = new HashMap<>();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        new MapEditCommand(plugin);
    }

    @EventHandler
    public void playerInteract(PlayerInteractEvent event) {
        if(event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(event.getItem() == null) return;
        if(event.getClickedBlock() == null) return;
        if(!UtilNBT.contains(event.getItem(), "MapEditTool")) return;
        Player player = event.getPlayer();
        CoreClient client = CoreClientManager.get(player);
        if(!client.getRank().has(Rank.DEVELOPER)) return;
        if(!points.containsKey(client.getPlayerName())) points.put(player.getName(), new PlayerPoints(player));
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            points.get(player.getName()).setPoint1(event.getClickedBlock().getLocation().clone());
            player.sendMessage(UtilMessage.main("MapEdit", UtilChat.cGold + "Point 1 set to " + UtilChat.cAqua + locationToString(event.getClickedBlock().getLocation())));
        }
        if(event.getAction() == Action.LEFT_CLICK_BLOCK) {
            points.get(player.getName()).setPoint2(event.getClickedBlock().getLocation().clone());
            player.sendMessage(UtilMessage.main("MapEdit", UtilChat.cGold + "Point 2 set to " + UtilChat.cAqua + locationToString(event.getClickedBlock().getLocation())));
        }
    }

    @EventHandler
    public void blockBreak(BlockBreakEvent event) {
        if(event.getPlayer().getItemInHand() == null) return;
        if(event.getPlayer().getItemInHand().getType() == Material.AIR) return;
        if(!UtilNBT.contains(event.getPlayer().getItemInHand(), "MapEditTool")) return;
        event.setCancelled(true);
    }

    public static PlayerPoints getPlayerPoints(Player player) {
        return context.points.get(player.getName());
    }

    public static PlayerPoints getPlayerPoints(String playerName) {
        return context.points.get(playerName);
    }

    private String locationToString(Location location) {
        return location.getWorld().getName() + ", " + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ();
    }
}
