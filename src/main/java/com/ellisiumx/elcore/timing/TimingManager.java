package com.ellisiumx.elcore.timing;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class TimingManager implements Listener {
    private static TimingManager instance;

    private static HashMap<String, Long> timingList = new HashMap<String, Long>();
    private static HashMap<String, TimeData> totalList = new HashMap<String, TimeData>();

    private static final Object timingLock = new Object();
    private static final Object totalLock = new Object();

    public static boolean Debug = true;

    public TimingManager(JavaPlugin plugin) {
        instance = this;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static TimingManager Initialize(JavaPlugin plugin) {
        if (instance == null) instance = new TimingManager(plugin);
        return instance;
    }

    public static TimingManager instance() {
        return instance;
    }

    public static void startTotal(String title) {
        if (!Debug) return;
        synchronized (totalLock) {
            if (totalList.containsKey(title)) {
                TimeData data = totalList.get(title);
                data.LastMarker = System.currentTimeMillis();

                totalList.put(title, data);
            } else {
                TimeData data = new TimeData(title, System.currentTimeMillis());
                totalList.put(title, data);
            }
        }
    }

    public static void stopTotal(String title) {
        if (!Debug) return;
        synchronized (totalLock) {
            if (totalList.containsKey(title)) {
                totalList.get(title).addTime();
            }
        }
    }

    public static void printTotal(String title) {
        if (!Debug) return;
        synchronized (totalLock) {
            totalList.get(title).printInfo();
        }
    }

    public static void endTotal(String title, boolean print) {
        if (!Debug) return;
        synchronized (totalLock) {
            TimeData data = totalList.remove(title);
            if (data != null && print) data.printInfo();
        }
    }

    public static void printTotals() {
        if (!Debug) return;
        synchronized (totalLock) {
            for (Map.Entry<String, TimeData> entry : totalList.entrySet()) {
                entry.getValue().printInfo();
            }
        }
    }

    public static void start(String title) {
        if (!Debug) return;
        synchronized (timingLock) {
            timingList.put(title, System.currentTimeMillis());
        }
    }

    public static void stop(String title) {
        if (!Debug) return;
        synchronized (timingLock) {
            System.out.println("[TIMING] [" + title + " took " + (System.currentTimeMillis() - timingList.get(title)) + "ms]");
            timingList.remove(title);
        }
    }
}

