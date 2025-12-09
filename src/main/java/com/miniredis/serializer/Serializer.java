package com.miniredis.serializer;
/**
 * Serializer interface.
 * MiniRedis uses serialization to store ANY object as byte[].
 * You can later plug in:
 *  - JSON com.miniredis.serializer
 *  - Kryo com.miniredis.serializer
 *  - Custom binary com.miniredis.serializer
 */
public interface Serializer {
	
	/** Serialize an object into byte[] */
	byte[] serialize(Object obj);
	
	/** Convert byte[] back into object of type T */
	<T> T deserialize(byte[] data, Class<T> type);
}
