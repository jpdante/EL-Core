package com.ellisiumx.elcore.redis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisDataRepository<T extends Data> implements DataRepository<T> {

    public final char KEY_DELIMITER = '.';
    private JedisPool _writePool;
    private JedisPool _readPool;
    private Class<T> _elementType;
    private String _elementLabel;

    public RedisDataRepository(ConnectionData writeConn, ConnectionData readConn, Class<T> elementType, String elementLabel) {
        _writePool = Utility.generatePool(writeConn);
        _readPool = (writeConn == readConn) ? _writePool : Utility.generatePool(readConn);
        _elementType = elementType;
        _elementLabel = elementLabel;
    }

    public RedisDataRepository(ConnectionData conn, Class<T> elementType, String elementLabel) {
        this(conn, conn, elementType, elementLabel);
    }

    public String getElementSetKey() {
        return concatenate("data", _elementLabel);
    }

    public String generateKey(T element) {
        return generateKey(element.getDataId());
    }

    public String generateKey(String dataId) {
        return concatenate(getElementSetKey(), dataId);
    }

    @Override
    public Collection<T> getElements() {
        return getElements(getActiveElements());
    }

    @Override
    public Collection<T> getElements(Collection<String> dataIds) {
        Collection<T> elements = new HashSet<T>();
        Jedis jedis = _readPool.getResource();

        try {
            Pipeline pipeline = jedis.pipelined();

            List<Response<String>> responses = new ArrayList<Response<String>>();
            for (String dataId : dataIds) {
                responses.add(pipeline.get(generateKey(dataId)));
            }

            // Block until all requests have received pipelined responses
            pipeline.sync();

            for (Response<String> response : responses) {
                String serializedData = response.get();
                T element = deserialize(serializedData);

                if (element != null) {
                    elements.add(element);
                }
            }
        } catch (JedisConnectionException exception) {
            exception.printStackTrace();
            _readPool.returnBrokenResource(jedis);
            jedis = null;
        } finally {
            if (jedis != null) {
                _readPool.returnResource(jedis);
            }
        }

        return elements;
    }

    @Override
    public T getElement(String dataId) {
        T element = null;
        Jedis jedis = _readPool.getResource();

        try {
            String key = generateKey(dataId);
            String serializedData = jedis.get(key);
            element = deserialize(serializedData);
        } catch (JedisConnectionException exception) {
            exception.printStackTrace();
            _readPool.returnBrokenResource(jedis);
            jedis = null;
        } finally {
            if (jedis != null) {
                _readPool.returnResource(jedis);
            }
        }

        return element;
    }

    @Override
    public void addElement(T element, int timeout) {
        Jedis jedis = _writePool.getResource();
        try {
            String serializedData = serialize(element);
            String dataId = element.getDataId();
            String setKey = getElementSetKey();
            String dataKey = generateKey(element);
            long expiry = currentTime() + timeout;

            Transaction transaction = jedis.multi();
            transaction.set(dataKey, serializedData);
            transaction.zadd(setKey, expiry, dataId.toString());
            transaction.exec();
        } catch (JedisConnectionException exception) {
            exception.printStackTrace();
            _writePool.returnBrokenResource(jedis);
            jedis = null;
        } finally {
            if (jedis != null) {
                _writePool.returnResource(jedis);
            }
        }
    }

    @Override
    public void addElement(T element) {
        addElement(element, 60 * 60 * 24 * 7 * 4 * 12 * 10);    // Set the timeout to 10 years
    }

    @Override
    public void removeElement(T element) {
        removeElement(element.getDataId());
    }

    @Override
    public void removeElement(String dataId) {
        Jedis jedis = _writePool.getResource();

        try {
            String setKey = getElementSetKey();
            String dataKey = generateKey(dataId);

            Transaction transaction = jedis.multi();
            transaction.del(dataKey);
            transaction.zrem(setKey, dataId);
            transaction.exec();
        } catch (JedisConnectionException exception) {
            exception.printStackTrace();
            _writePool.returnBrokenResource(jedis);
            jedis = null;
        } finally {
            if (jedis != null) {
                _writePool.returnResource(jedis);
            }
        }
    }

    @Override
    public boolean elementExists(String dataId) {
        return getElement(dataId) != null;
    }

    @Override
    public int clean() {
        Jedis jedis = _writePool.getResource();

        try {
            for (String dataId : getDeadElements()) {
                String dataKey = generateKey(dataId);

                Transaction transaction = jedis.multi();
                transaction.del(dataKey);
                transaction.zrem(getElementSetKey(), dataId);
                transaction.exec();
            }
        } catch (JedisConnectionException exception) {
            exception.printStackTrace();
            _writePool.returnBrokenResource(jedis);
            jedis = null;
        } finally {
            if (jedis != null) {
                _writePool.returnResource(jedis);
            }
        }

        return 0;
    }

    protected Set<String> getActiveElements() {
        Set<String> dataIds = new HashSet<String>();
        Jedis jedis = _readPool.getResource();

        try {
            String min = "(" + currentTime();
            String max = "+inf";
            dataIds = jedis.zrangeByScore(getElementSetKey(), min, max);
        } catch (JedisConnectionException exception) {
            exception.printStackTrace();
            _readPool.returnBrokenResource(jedis);
            jedis = null;
        } finally {
            if (jedis != null) {
                _readPool.returnResource(jedis);
            }
        }

        return dataIds;
    }

    protected Set<String> getDeadElements() {
        Set<String> dataIds = new HashSet<String>();
        Jedis jedis = _readPool.getResource();

        try {
            String min = "-inf";
            String max = currentTime() + "";
            dataIds = jedis.zrangeByScore(getElementSetKey(), min, max);
        } catch (JedisConnectionException exception) {
            exception.printStackTrace();
            _readPool.returnBrokenResource(jedis);
            jedis = null;
        } finally {
            if (jedis != null) {
                _readPool.returnResource(jedis);
            }
        }

        return dataIds;
    }

    protected T deserialize(String serializedData) {
        return Utility.deserialize(serializedData, _elementType);
    }

    protected String serialize(T element) {
        return Utility.serialize(element);
    }

    protected Long currentTime() {
        return Utility.currentTimeSeconds();
    }

    protected String concatenate(String... elements) {
        return Utility.concatenate(KEY_DELIMITER, elements);
    }
}
