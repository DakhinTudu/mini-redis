package com.miniredis;

public class RedisValue {
	private final byte[] value;
	private final long expiryTime; 
	
	public RedisValue(byte[] value, long ttlMillis) {
		this.value = value;
		// If TTL > 0, calculate expiry timestamp
        // If TTL <= 0, store -1 (no expiry)
		this.expiryTime = ttlMillis > 0 ? System.currentTimeMillis() + ttlMillis : -1;
	}
	
	/** Return raw byte[] value */
	public byte[] getValue() {
		return value;
	}
	
	
	 /**
     * Returns true if this key has expired.
     */
	public boolean isExpired() {
		return expiryTime != -1 && System.currentTimeMillis() > expiryTime;
	}
	
	/**
     * Returns remaining TTL in milliseconds.
     * -1 → no expiry
     */
	public long getRemainingTTL() {
	return expiryTime == -1 ? -1 : expiryTime - System.currentTimeMillis();	
	}
	

}
