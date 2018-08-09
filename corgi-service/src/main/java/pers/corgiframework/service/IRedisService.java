package pers.corgiframework.service;

import java.util.List;
import java.util.Map;

/**
 * Created by syk on 2017/8/18.
 */
public interface IRedisService {
    void setString(String key, int seconds, String value);
    String getString(String key);
    void setMap(String key, Map<String, String> map);
    List<String> getMap(String key, String... fields);
    void setList(String key, String... values);
    List<String> getList(String key);
    void delByKey(String key);
    void set(String key, int seconds, Object value);
    void set(String key, Object value);
    Object get(String key);
    Long getIncrementId(String key);
    void set(String key, int seconds, Object value, int index);
    Object get(String key, int index);
    String generateTradeNo();
    //获得key剩余过期时间
    Long getExpireTime(String key);
}
