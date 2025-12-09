package com.miniredis;

import java.util.concurrent.ConcurrentHashMap;

import com.miniredis.serializer.Serializer;

/**
 * Core MiniRedis in-memory cache engine.
 * Thread-safe using ConcurrentHashMap.
 */
public class MiniRedisCache {

	private final ConcurrentHashMap<String, RedisValue> store = new ConcurrentHashMap<>();
	
	// serializer used for all objects
	private final Serializer serializer;
	
	public MiniRedisCache(Serializer serializer) {
		this.serializer = serializer;
	}
	
	
	 /**
     * SET operation - stores a value with TTL.
     * @param key key
     * @param value any Java object (must be Serializable)
     * @param ttlMillis TTL in milliseconds (-1 means never expire)
     */
	public void set(String key, Object value, long ttlMillis) {
		byte[] data = serializer.serialize(value);
		store.put(key, new RedisValue(data,ttlMillis));
	}
	

    /**
     * GET operation
     */	
	public <T> T get(String key, Class<T> clazz) {
		RedisValue value = store.get(key);
		
		if(value==null || value.isExpired()) {
			store.remove(key);
			return null;
		}
		return serializer.deserialize(value.getValue(), clazz);
				
	}
	
	 /** Check if a key exists AND is not expired */
	public boolean exists(String key) {
		RedisValue value = store.get(key);
		return value != null && !value.isExpired();
	}
	
	 /** DELETE key */
	public void delete(String key) {
		store.remove(key);
	}
	
	public long ttl(String key) {
		
		RedisValue value = store.get(key);
		if(value==null)
			return -2;
		return value.getRemainingTTL();
		
	}
	
	
	
	public ConcurrentHashMap<String, RedisValue> getStore(){
		return store;
	}
	
	
	
	
//	/** INCR operation (Redis-style) */
//    public long incr(String key) {
//        Long val = get(key, Long.class);
//
//        long newVal = (val == null ? 1 : val + 1);
//
//        set(key, newVal, -1); // rewrite with no expiry
//        return newVal;
//    }
//
//    /** DECR operation (Redis-style) */
//    public long decr(String key) {
//        Long val = get(key, Long.class);
//
//        long newVal = (val == null ? -1 : val - 1);
//
//        set(key, newVal, -1); // rewrite with no expiry
//        return newVal;
//    }

}
