package com.miniredis;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Background job that runs every 1 second.
 * It removes expired keys from the map.
 */
public class ExpiryCleaner {
	
	
	  /**
     * Starts the cleaner thread using ScheduledExecutorService.
     */
	public static void start(MiniRedisCache cache) {

        Executors.newSingleThreadScheduledExecutor()
            .scheduleAtFixedRate(() -> {

                // Iterate over all keys
                Iterator<Map.Entry<String, RedisValue>> it =
                        cache.getStore().entrySet().iterator();

                while (it.hasNext()) {
                    Map.Entry<String, RedisValue> entry = it.next();

                    // If expired → remove it
                    if (entry.getValue().isExpired()) {
                        it.remove();
                    }
                }

            }, 1, 1, TimeUnit.SECONDS); // run every 1 second
    }
	

}
