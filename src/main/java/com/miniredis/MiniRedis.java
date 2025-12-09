package com.miniredis;

import com.miniredis.serializer.JavaSerializer;

/*
 * main class for MiniRedis
 */
public class MiniRedis {

	// Singleton instance
		private static MiniRedisCache instance;
		
		public static MiniRedisCache getInstance() {
			if(instance ==null) {
				instance = new MiniRedisCache(new JavaSerializer());
				
				ExpiryCleaner.start(instance);
			}
			
			return instance;
		}
}
