package com.ecnu.center.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class RedisService {
    @Autowired
    public RedisTemplate redisTemplate;

    /**
     * 缓存基本的对象，Integer、String、实体类等
     * @param key
     * @param value
     * @return
     * @param <T>
     */
    public <T>ValueOperations<String, T> setCacheObject(String key, T value){
        ValueOperations<String, T> operations = redisTemplate.opsForValue();
        operations.set(key,value);
        return operations;
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     * @param key
     * @param value
     * @param timeout
     * @param timeUnit
     * @return
     * @param <T>
     */
    public <T> ValueOperations<String, T> setCacheObject(String key, T value, Integer timeout, TimeUnit timeUnit){
        ValueOperations<String, T> operations = redisTemplate.opsForValue();
        operations.set(key, value, timeout, timeUnit);
        return operations;
    }

    /**
     * 获取缓存的基本对象
     * @param key
     * @return
     * @param <T>
     */
    public <T> T getCacheObject(String key){
        ValueOperations<String, T> operations = redisTemplate.opsForValue();
        return operations.get(key);
    }

    /**
     * 删除单个对象
     * @param key
     */
    public void deleteObject(String key){
        redisTemplate.delete(key);
    }

    /**
     * 删除集合对象
     * @param collection
     */
    public void deleteObject(Collection collection){
        redisTemplate.delete(collection);
    }

    /**
     * 缓存List数据
     * @param key
     * @param dataList
     * @return
     * @param <T>
     */
    public <T> ListOperations<String, T> setCacheList(String key, List<T> dataList){
        ListOperations listOperations = redisTemplate.opsForList();
        if(dataList != null){
            int size = dataList.size();
            for (int i = 0; i < size; i++) {
                listOperations.leftPush(key, dataList.get(i));
            }
        }
        return listOperations;
    }

    /**
     * 获取缓存的list对象
     * @param key
     * @return
     * @param <T>
     */

    public <T> List<T> getCacheList(String key){
        List<T> dataList = new ArrayList<>();
        ListOperations listOperations = redisTemplate.opsForList();
        Long size = listOperations.size(key);
        for (int i = 0; i < size; i++) {
            dataList.add((T) listOperations.index(key, i));
        }
        return dataList;
    }

    /**
     * 获取缓存的set
     * @param key
     * @param dataSet
     * @return
     * @param <T>
     */
    public <T> BoundSetOperations<String, T> setCacheSet(String key, Set<T> dataSet){
        BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
        Iterator<T> it = dataSet.iterator();
        while(it.hasNext()){
            setOperation.add(it.next());
        }
        return setOperation;
    }

    /**
     * 获取缓存的set
     * @param key
     * @return
     * @param <T>
     */

    public <T> Set<T> getCacheSet(String key){
        Set<T> dataSet = new HashSet<>();
        BoundSetOperations<String, T> operations = redisTemplate.boundSetOps(key);
        dataSet = operations.members();
        return dataSet;
    }

    /**
     * 缓存Map
     * @param key
     * @param dataMap
     * @return
     * @param <T>
     */
    public <T> HashOperations<String, String, T> setCacheMap(String key, Map<String, T> dataMap){
        HashOperations hashOperations = redisTemplate.opsForHash();
        if(dataMap != null){
            for(Map.Entry<String, T> entry : dataMap.entrySet()){
                hashOperations.put(key, entry.getKey(), entry.getValue());
            }
        }
        return  hashOperations;
    }

    /**
     * 获取缓存的map
     * @param key
     * @return
     * @param <T>
     */
    public <T> Map<String, T> getCacheMap(String key){
        Map<String, T> map = redisTemplate.opsForHash().entries(key);
        return map;
    }

    /**
     * 获取缓存的基本对象列表
     * @param pattern 前缀
     * @return
     */
    public Collection<String> keys(String pattern){
        return redisTemplate.keys(pattern);
    }

    /**
     * 存在key
     * @param key
     * @return
     */
    public boolean hashkey(String key){
        return redisTemplate.hasKey(key);
    }

    /**
     * 这个方法，是使用redisTemplate获取到设置的过期时间
     * @param key
     * @return
     */
    public Long getExpire(String key){
        return redisTemplate.getExpire(key);
    }

    public <T> ValueOperations<String, T> setBillObject(String key, List<Map<String, Object>> value) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        operation.set(key, (T) value);
        return operation;
    }
    /**
     * 缓存list<Map<String, Object>>
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     * @return 缓存的对象
     */
    public <T> ValueOperations<String, T> setBillObject(String key, List<Map<String, Object>> value, Integer timeout, TimeUnit timeUnit) {
        ValueOperations<String, T> operation = redisTemplate.opsForValue();
        operation.set(key, (T) value, timeout, timeUnit);
        return operation;
    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     * @return
     */
    public <T> HashOperations<String, String, T> setCKdBillMap(String key, Map<String, T> dataMap) {
        HashOperations hashOperations = redisTemplate.opsForHash();
        if (null != dataMap) {
            for (Map.Entry<String, T> entry : dataMap.entrySet()) {
                hashOperations.put(key, entry.getKey(), entry.getValue());
            }
        }
        return hashOperations;
    }
}
