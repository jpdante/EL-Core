package com.ellisiumx.elcore.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.ConcurrentHashMap;


public class Utility {

    private static Gson _gson = new GsonBuilder().create();
    public static Gson getGson() {
        return _gson;
    }

    private static final ConcurrentHashMap<String, JedisPool> _pools = new ConcurrentHashMap<String, JedisPool>();
    private static JedisPool _masterPool;
    private static JedisPool _slavePool;

    public static String serialize(Object object) {
        return _gson.toJson(object);
    }

    public static <T> T deserialize(String serializedData, Class<T> type) {
        return _gson.fromJson(serializedData, type);
    }

    public static String concatenate(char delimiter, String... elements) {
        int length = elements.length;
        String result = length > 0 ? elements[0] : new String();

        for (int i = 1; i < length; i++) {
            result += delimiter + elements[i];
        }

        return result;
    }

    public static long currentTimeSeconds() {
        long currentTime = 0;
        JedisPool pool = getPool(false);
        Jedis jedis = pool.getResource();

        try {
            currentTime = Long.parseLong(jedis.time().get(0));
        } catch (JedisConnectionException exception) {
            exception.printStackTrace();
            pool.returnBrokenResource(jedis);
            jedis = null;
        } finally {
            if (pool != null) {
                pool.returnResource(jedis);
            }
        }

        return currentTime;
    }

    public static long currentTimeMillis() {
        return currentTimeSeconds() * 1000;
    }

    public static JedisPool generatePool(ConnectionData connData) {
        String key = getConnKey(connData);
        JedisPool pool = _pools.get(key);
        if (pool == null) {
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxWaitMillis(1000);
            jedisPoolConfig.setMinIdle(5);
            jedisPoolConfig.setTestOnBorrow(true);

            jedisPoolConfig.setMaxTotal(20);
            jedisPoolConfig.setBlockWhenExhausted(true);

            pool = new JedisPool(jedisPoolConfig, connData.getHost(), connData.getPort());
            _pools.put(key, pool);
        }

        return pool;
    }

    public static JedisPool getPool(boolean writeable) {
        if (writeable) {
            if (_masterPool == null) {
                _masterPool = generatePool(RedisManager.getMasterConnection());
            }
            return _masterPool;
        } else {
            if (_slavePool == null) {
                ConnectionData slave = RedisManager.getSlaveConnection();

                _slavePool = generatePool(slave);
            }
            return _slavePool;
        }
    }

    private static String getConnKey(ConnectionData connData) {
        return connData.getHost() + ":" + connData.getPort();
    }
}
