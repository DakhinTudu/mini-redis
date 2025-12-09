# MiniRedis – Lightweight In-Memory Cache for Java

MiniRedis is a lightweight, zero-dependency, in-memory key–value cache built in Java.  
It provides a Redis-like API with TTL support, auto-expiry, and thread-safe operations.

---

## Features

- Store key-value pairs  
- TTL expiration for keys  
- Automatic background cleanup  
- Thread-safe using ConcurrentHashMap  
- Works in any Java or Spring Boot project  
- Extremely lightweight (few KB)

---

## Installation (Using JitPack)

### Step 1 — Add JitPack repository

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

### Step 2 — Add the MiniRedis dependency

```xml
<dependency>
    <groupId>com.github.DakhinTudu</groupId>
    <artifactId>mini-redis</artifactId>
    <version>1.0.0</version>
</dependency>
```

---

# Basic Usage

## 1. Create a MiniRedis instance

```java
MiniRedisCache cache = new MiniRedisCache();
```

## 2. Set a value without TTL

```java
cache.set("username", "Daxin Tudu");
```

## 3. Set a value with TTL (seconds)

```java
cache.set("otp", "7283", 30); // expires in 30 seconds
```

## 4. Get a value

```java
String otp = (String) cache.get("otp");
```

If the key is expired or missing → returns null.

## 5. Delete a key

```java
cache.delete("username");
```

## 6. Check if key exists

```java
boolean exists = cache.exists("otp");
```

---

# Integration With Spring Boot

## Step 1 — Register Bean

```java
@Configuration
public class CacheConfig {

    @Bean
    public MiniRedisCache miniRedisCache() {
        MiniRedisCache cache = new MiniRedisCache();

        // Start automatic expiry removal (runs every 1 second)
        ExpiryCleaner.start(cache);

        return cache;
    }
}
```

## Step 2 — Use in a Service

```java
@Service
public class UserService {

    @Autowired
    private MiniRedisCache cache;

    public String getUser(int id) {

        String key = "USER_" + id;

        Object cached = cache.get(key);
        if (cached != null) {
            return (String) cached;
        }

        // Simulate DB fetch
        String user = "Daxin Tudu";

        // Cache for 10 minutes
        cache.set(key, user, 600);

        return user;
    }
}
```

---

# API Reference

### set(String key, Object value)
Store a value.

### set(String key, Object value, long ttlSeconds)
Store a value with an expiration time.

### get(String key)
Get a value. Returns null if expired/not found.

### delete(String key)
Remove a key.

### exists(String key)
Check if the key exists and is not expired.

### clear()
Clear all keys.

---

# Internal Architecture

### 1. MiniRedisCache
- Holds a ConcurrentHashMap of all keys
- Handles set/get/delete logic

### 2. RedisValue
- Stores actual value + expiry timestamp
- Method `isExpired()` returns true/false

### 3. ExpiryCleaner
- Background scheduler using ScheduledExecutorService
- Runs every 1 second
- Removes expired keys automatically

---

# Roadmap (Future Versions)

- Wildcard key search (ex: keys("user:*"))
- Atomic increment/decrement
- JSON serialization mode
- Optional persistence
- Hash data type (like Redis HSET)

---

# Contributing

1. Fork the repository  
2. Create a feature branch  
3. Commit changes  
4. Open a Pull Request  

---

# License

MIT License  
Free to use, modify, and distribute.

