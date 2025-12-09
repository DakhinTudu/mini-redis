//package com.miniredis;
//
//public class DemoMain {
//
//    public static void main(String[] args) throws Exception {
//
//        MiniRedisCache cache = MiniRedis.getInstance();
//
//        System.out.println("Setting key 'name' with TTL 3 seconds...");
//        cache.set("name", "Mini Redis Demo", 3000);
//
//        System.out.println("Getting key: " + cache.get("name", String.class));
//
//        Thread.sleep(4000); // wait 4 seconds (key will expire)
//
//        System.out.println("After expiry, value: " + cache.get("name", String.class));
//        
//        
//        
//        // another example 
//        
//        System.out.println("Setting key 'name1' with TTL 3 seconds...");
//        cache.set("name1", "Mini Redis Demo2", 3000);
//
//        System.out.println("Getting key: " + cache.get("name1", String.class));
//
//        Thread.sleep(4000); // wait 4 seconds (key will expire)
//
//        System.out.println("After expiry, value: " + cache.get("name1", String.class));
//        
//    }
//}
