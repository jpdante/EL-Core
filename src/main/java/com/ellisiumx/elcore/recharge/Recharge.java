package com.ellisiumx.elcore.recharge;

import com.ellisiumx.elcore.account.event.ClientUnloadEvent;
import com.ellisiumx.elcore.updater.UpdateType;
import com.ellisiumx.elcore.updater.event.UpdateEvent;
import com.ellisiumx.elcore.utils.UtilMessage;
import com.ellisiumx.elcore.utils.UtilPlayer;
import com.ellisiumx.elcore.utils.UtilServer;
import com.ellisiumx.elcore.utils.UtilTime;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

public class Recharge implements Listener {
    public static Recharge context;

    public HashSet<String> informSet = new HashSet<String>();
    public HashMap<String, HashMap<String, RechargeData>> rechargeDatas = new HashMap<String, HashMap<String, RechargeData>>();

    public Recharge(JavaPlugin plugin) {
        context = this;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void PlayerDeath(PlayerDeathEvent event) {
        get(event.getEntity().getName()).clear();
    }

    public static HashMap<String, RechargeData> get(String name) {
        if (!context.rechargeDatas.containsKey(name)) context.rechargeDatas.put(name, new HashMap<String, RechargeData>());
        return context.rechargeDatas.get(name);
    }

    public static HashMap<String, RechargeData> get(Player player) {
        return get(player.getName());
    }

    @EventHandler
    public void update(UpdateEvent event) {
        if (event.getType() != UpdateType.TICK) return;
        recharge();
    }

    public static void recharge() {
        for (Player cur : UtilServer.getPlayers()) {
            LinkedList<String> rechargeList = new LinkedList<String>();
            //Check Recharged
            for (String ability : get(cur).keySet()) {
                if (get(cur).get(ability).update()) rechargeList.add(ability);
            }
            //Inform Recharge
            for (String ability : rechargeList) {
                get(cur).remove(ability);
                //Event
                RechargedEvent rechargedEvent = new RechargedEvent(cur, ability);
                UtilServer.getServer().getPluginManager().callEvent(rechargedEvent);
                if (context.informSet.contains(ability)) UtilPlayer.message(cur, UtilMessage.main("Recharge", "You can use " + UtilMessage.skill(ability) + "."));
            }
        }
    }

    public static boolean use(Player player, String ability, long recharge, boolean inform, boolean attachItem) {
        return use(player, ability, ability, recharge, inform, attachItem);
    }

    public static boolean use(Player player, String ability, String abilityFull, long recharge, boolean inform, boolean attachItem) {
        return use(player, ability, abilityFull, recharge, inform, attachItem, false);
    }

    public static boolean use(Player player, String ability, long recharge, boolean inform, boolean attachItem, boolean attachDurability) {
        return use(player, ability, ability, recharge, inform, attachItem, attachDurability);
    }

    public static boolean use(Player player, String ability, String abilityFull, long recharge, boolean inform, boolean attachItem, boolean attachDurability) {
        if (recharge == 0) return true;
        //Ensure Expirey
        recharge();
        //Lodge Recharge Msg
        if (inform && recharge > 1000) context.informSet.add(ability);
        //Recharging
        if (get(player).containsKey(ability)) {
            if (inform) {
                UtilPlayer.message(player, UtilMessage.main("Recharge", "You cannot use " + UtilMessage.skill(abilityFull) + " for " +
                        UtilMessage.time(UtilTime.convertString((get(player).get(ability).getRemaining()), 1, UtilTime.TimeUnit.FIT)) + "."));
            }
            return false;
        }
        //Insert
        useRecharge(player, ability, recharge, attachItem, attachDurability);
        return true;
    }

    public static void useForce(Player player, String ability, long recharge) {
        useForce(player, ability, recharge, false);
    }

    public static void useForce(Player player, String ability, long recharge, boolean attachItem) {
        useRecharge(player, ability, recharge, attachItem, false);
    }

    public static boolean usable(Player player, String ability) {
        return usable(player, ability, false);
    }

    public static boolean usable(Player player, String ability, boolean inform) {
        if (!get(player).containsKey(ability))
            return true;

        if (get(player).get(ability).getRemaining() <= 0) {
            return true;
        } else {
            if (inform)
                UtilPlayer.message(player, UtilMessage.main("Recharge", "You cannot use " + UtilMessage.skill(ability) + " for " +
                        UtilMessage.time(UtilTime.convertString((get(player).get(ability).getRemaining()), 1, UtilTime.TimeUnit.FIT)) + "."));

            return false;
        }
    }

    public static void useRecharge(Player player, String ability, long recharge, boolean attachItem, boolean attachDurability) {
        //Event
        RechargeEvent rechargeEvent = new RechargeEvent(player, ability, recharge);
        UtilServer.getServer().getPluginManager().callEvent(rechargeEvent);
        get(player).put(ability, new RechargeData(context, player, ability, player.getItemInHand(), rechargeEvent.getRecharge(), attachItem, attachDurability));
    }

    public static void recharge(Player player, String ability) {
        get(player).remove(ability);
    }

    @EventHandler
    public void clearPlayer(ClientUnloadEvent event) {
        rechargeDatas.remove(event.GetName());
    }

    public static void setDisplayForce(Player player, String ability, boolean displayForce) {
        if (!context.rechargeDatas.containsKey(player.getName())) return;
        if (!context.rechargeDatas.get(player.getName()).containsKey(ability)) return;
        context.rechargeDatas.get(player.getName()).get(ability).DisplayForce = displayForce;
    }

    public static void setCountdown(Player player, String ability, boolean countdown) {
        if (!context.rechargeDatas.containsKey(player.getName())) return;
        if (!context.rechargeDatas.get(player.getName()).containsKey(ability)) return;
        context.rechargeDatas.get(player.getName()).get(ability).Countdown = countdown;
    }

    public static void reset(Player player) {
        context.rechargeDatas.put(player.getName(), new HashMap<String, RechargeData>());
    }

    public static void reset(Player player, String stringContains) {
        HashMap<String, RechargeData> data = context.rechargeDatas.get(player.getName());
        if (data == null) return;
        Iterator<String> rechargeIter = data.keySet().iterator();
        while (rechargeIter.hasNext()) {
            String key = rechargeIter.next();
            if (key.toLowerCase().contains(stringContains.toLowerCase())) {
                rechargeIter.remove();
            }
        }
    }

    public static void debug(Player player, String ability) {
        if (!context.rechargeDatas.containsKey(player.getName())) {
            player.sendMessage("No Recharge Map.");
            return;
        }
        if (!context.rechargeDatas.get(player.getName()).containsKey(ability)) {
            player.sendMessage("Ability Not Found.");
            return;
        }
        context.rechargeDatas.get(player.getName()).get(ability).debug(player);
    }
}

