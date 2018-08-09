package pers.corgiframework.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pers.corgiframework.service.IRedisService;
import pers.corgiframework.tool.utils.DateTimeUtil;
import pers.corgiframework.tool.utils.SerializationUtil;
import pers.corgiframework.tool.utils.StringUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Map;

/**
 * Created by syk on 2017/8/18.
 */
@Service
public class RedisServiceImpl implements IRedisService {

    final static Logger LOGGER = LoggerFactory.getLogger(RedisServiceImpl.class);

    @Autowired
    private JedisPool jedisPool;

    @Override
    public void setString(String key, int seconds, String value) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.setex(key, seconds, value);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    @Override
    public String getString(String key) {
        Jedis jedis = jedisPool.getResource();
        String result = null;
        try {
            result = jedis.get(key);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return result;
    }

    @Override
    public void setMap(String key, Map<String, String> map) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.hmset(key, map);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    @Override
    public List<String> getMap(String key, String... fields) {
        Jedis jedis = jedisPool.getResource();
        List<String> result = null;
        try {
            result = jedis.hmget(key, fields);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return result;
    }

    @Override
    public void setList(String key, String... values) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.rpush(key, values);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    @Override
    public List<String> getList(String key) {
        Jedis jedis = jedisPool.getResource();
        List<String> result = null;
        try {
            // jedis.lrange是按范围取出，
            // 第一个是key，第二个是起始位置，第三个是结束位置，jedis.llen获取长度 -1表示取得所有
            result = jedis.lrange(key, 0, -1);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return result;
    }

    @Override
    public void delByKey(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.del(key);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    @Override
    public void set(String key, int seconds, Object value) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.set(key.getBytes(), SerializationUtil.serialize(value));
            jedis.expire(key, seconds);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    @Override
    public void set(String key,Object value) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.set(key.getBytes(), SerializationUtil.serialize(value));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    @Override
    public Object get(String key) {
        Jedis jedis = jedisPool.getResource();
        Object result = null;
        try {
            byte[] in = jedis.get(key.getBytes());
            result = SerializationUtil.deserialize(in);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return result;
    }

    @Override
    public Long getIncrementId(String key) {
        Jedis jedis = jedisPool.getResource();
        Long result = 0L;
        try {
            result = jedis.incr(key);
            if (result == 1) {
                jedis.expire(key, 60*60*24*2);
            }
            result = result + 100000L;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return result;
    }

    @Override
    public void set(String key, int seconds, Object value, int index) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.select(index);
            jedis.set(key.getBytes(), SerializationUtil.serialize(value));
            jedis.expire(key, seconds);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
    }

    @Override
    public Object get(String key, int index) {
        Jedis jedis = jedisPool.getResource();
        jedis.select(index);
        Object result = null;
        try {
            byte[] in = jedis.get(key.getBytes());
            result = SerializationUtil.deserialize(in);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return result;
    }

    @Override
    public Long getExpireTime(String key) {
        Jedis jedis = jedisPool.getResource();
        Long ttl = 0L;
        try {
            ttl = jedis.ttl(key);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }finally {
            jedis.close();
        }
        return ttl;
    }

    @Override
    public String generateTradeNo() {
        Jedis jedis = jedisPool.getResource();
        String tradeNo = "";
        try {
            String key = DateTimeUtil.getNowDate(DateTimeUtil.FORMAT_SHORT);
            Long result = jedis.incr(key);
            if (result == 1) {
                jedis.expire(key, 60*60*24*2);
            }
            result = result + 100000000L;
            tradeNo = key + StringUtil.generateRandomNumber(6) + result;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            jedis.close();
        }
        return tradeNo;
    }
}
