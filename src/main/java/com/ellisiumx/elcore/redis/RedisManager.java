package com.ellisiumx.elcore.redis;

import com.ellisiumx.elcore.account.PlayerAccount;
import com.ellisiumx.elcore.permissions.Rank;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisDataException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class RedisManager implements Listener {
    private static RedisManager instance;
    private JedisPool pool;
    private Jedis jedis;

    public RedisManager() {
        instance = this;
    }

    public void Connect(String IP, String Port, String Password, int Database) {
        Bukkit.getLogger().log(Level.INFO, "[RedisManager] Connecting to redis...");
        pool = new JedisPool(IP, Integer.valueOf(Port));
        try {
            jedis = pool.getResource();
            Bukkit.getLogger().log(Level.INFO, "[RedisManager] Connecting accepted, starting auth!");
            jedis.auth(Password);
            jedis.select(Database);
        } catch (JedisDataException ex) {
            if(ex.getMessage().equalsIgnoreCase("ERR invalid password")) {
                Bukkit.getLogger().log(Level.SEVERE, "[RedisManager] Failed, password is incorrect!");
                Bukkit.shutdown();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (jedis.isConnected()) {
            Bukkit.getLogger().log(Level.INFO, "[RedisManager] Connected to redis successfully!");
        } else {
            Bukkit.getLogger().log(Level.SEVERE, "[RedisManager] Failed to connect to redis!");
            Bukkit.shutdown();
        }
    }

    public void Disconnect() {
        Bukkit.getLogger().log(Level.INFO, "[RedisManager] Disconnecting from redis...");
        jedis.disconnect();
        Bukkit.getLogger().log(Level.INFO, "[RedisManager] Successfully disconnected, cleaning ram...");
        jedis.close();
        pool.close();
        Bukkit.getLogger().log(Level.INFO, "[RedisManager] RedisManager closed successfully!");
    }

    public static RedisManager getContext() {
        return instance;
    }

    public boolean isConnected() {
        return jedis.isConnected();
    }

    public boolean hasPlayer(UUID uuid) {
        return jedis.exists("minecraft:xcore:" + uuid.toString());
    }

    /*public void addPlayer(UUID uuid, PlayerAccount account) {
        Map<String, String> userProperties = new HashMap<String, String>();
        userProperties.put("rank", String.valueOf(profiledata.rank.Index));
        userProperties.put("lang", account.language);
        userProperties.put("cugd", String.valueOf(profiledata.current_gadget));
        userProperties.put("repm", String.valueOf((profiledata.receive_pm)? 1 :0));
        userProperties.put("swpl", String.valueOf((profiledata.show_players)? 1 : 0));
        String enco = "";
        for(int i=0; i<account.enabled_collectibles.size(); i++) {
            if(i == (account.enabled_collectibles.size() - 1)) {
                enco += (account.enabled_collectibles.get(i) ? 1 : 0);
            } else {
                enco += (account.enabled_collectibles.get(i) ? 1 : 0) + ";";
            }
        }
        userProperties.put("enco", enco);
        jedis.hmset("minecraft:xcore:" + uuid.toString(), userProperties);
        jedis.expire("minecraft:xcore:" + uuid.toString(), 1800);
        enco = null;
    }

    public PlayerAccount getPlayer(UUID uuid) {
        Map<String, String> properties = jedis.hgetAll("minecraft:xcore:" + uuid.toString());
        ProfileData profileData = new ProfileData();
        for(Rank r : Rank.values()) {
            if(r.Index == Integer.valueOf(properties.get("rank"))) {
                profileData.rank = r;
                break;
            }
        }
        profileData.language = properties.get("lang");
        profileData.current_gadget = Integer.valueOf(properties.get("cugd"));
        profileData.receive_pm = Boolean.valueOf(properties.get("repm"));
        profileData.show_players = Boolean.valueOf(properties.get("swpl"));
        profileData.enabled_collectibles = new ArrayList<>();
        for(String s : properties.get("enco").split(";")) {
            if(s.equalsIgnoreCase("1")) profileData.enabled_collectibles.add(true);
            else profileData.enabled_collectibles.add(false);
        }
        return profileData;
    }*/
}
