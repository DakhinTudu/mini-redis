# MiniRedis – Lightweight In-Memory Cache for Java

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-blue.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![JitPack](https://jitpack.io/v/DakhinTudu/mini-redis.svg)](https://jitpack.io/#DakhinTudu/mini-redis)

MiniRedis is a lightweight, zero-dependency, in-memory key–value cache built in Java. It provides a Redis-like API with TTL support, auto-expiry, and thread-safe operations. Perfect for development, testing, or production scenarios where you need a simple caching solution without the overhead of a full Redis server.

---

## ✨ Features

- **🔑 Key-Value Storage**: Store any serializable Java object
- **⏱️ TTL Expiration**: Set time-to-live for keys (in milliseconds)
- **🧹 Automatic Cleanup**: Background thread removes expired keys every second
- **🔒 Thread-Safe**: Built on `ConcurrentHashMap` for concurrent access
- **🚀 Zero Dependencies**: No external dependencies (except Spring for optional integration)
- **💾 Type-Safe Retrieval**: Generic API for type-safe value retrieval
- **📦 Lightweight**: Minimal footprint (~few KB)
- **🔌 Spring Boot Ready**: Easy integration with Spring Boot applications

---

## 📋 Table of Contents

- [Installation](#-installation)
- [Quick Start](#-quick-start)
- [Basic Usage](#-basic-usage)
- [Spring Boot Integration](#-spring-boot-integration)
- [API Reference](#-api-reference)
- [Real-World Example](#-real-world-example)
- [Architecture](#-architecture)
- [Performance Considerations](#-performance-considerations)
- [Limitations](#-limitations)
- [Roadmap](#-roadmap)
- [Contributing](#-contributing)
- [License](#-license)

---

## 📦 Installation

### Using JitPack (Recommended)

#### Step 1: Add JitPack Repository

Add the JitPack repository to your `pom.xml`:

```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

#### Step 2: Add Dependency

Add MiniRedis to your dependencies:

```xml
<dependency>
    <groupId>com.github.DakhinTudu</groupId>
    <artifactId>mini-redis</artifactId>
    <version>1.0.0</version>
</dependency>
```

### For Gradle Users

```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.DakhinTudu:mini-redis:1.0.0'
}
```

---

## 🚀 Quick Start

### Basic Example

```java
import com.miniredis.MiniRedis;
import com.miniredis.MiniRedisCache;

public class QuickStart {
    public static void main(String[] args) {
        // Get singleton instance (auto-starts expiry cleaner)
        MiniRedisCache cache = MiniRedis.getInstance();
        
        // Store a value without expiration
        cache.set("username", "John Doe", -1);
        
        // Store a value with 30-second TTL (30000 milliseconds)
        cache.set("otp", "123456", 30000);
        
        // Retrieve value (type-safe)
        String username = cache.get("username", String.class);
        String otp = cache.get("otp", String.class);
        
        System.out.println("Username: " + username);  // John Doe
        System.out.println("OTP: " + otp);            // 123456
        
        // Check if key exists
        boolean exists = cache.exists("otp");
        System.out.println("OTP exists: " + exists);  // true
        
        // Get remaining TTL
        long ttl = cache.ttl("otp");
        System.out.println("TTL remaining: " + ttl + " ms");
    }
}
```

---

## 📖 Basic Usage

### 1. Get Cache Instance

**Option A: Singleton Pattern (Recommended)**

```java
MiniRedisCache cache = MiniRedis.getInstance();
// ExpiryCleaner is automatically started
```

**Option B: Create Custom Instance**

```java
import com.miniredis.serializer.JavaSerializer;
import com.miniredis.ExpiryCleaner;

MiniRedisCache cache = new MiniRedisCache(new JavaSerializer());
ExpiryCleaner.start(cache); // Start background cleanup
```

### 2. Store Values

**Without TTL (Never expires):**

```java
cache.set("key", "value", -1);
```

**With TTL (milliseconds):**

```java
// Store for 30 seconds
cache.set("session_token", "abc123xyz", 30000);

// Store for 5 minutes
cache.set("user_data", userObject, 300000);

// Store for 1 hour
cache.set("api_key", "secret", 3600000);
```

### 3. Retrieve Values

**Type-Safe Retrieval:**

```java
String value = cache.get("key", String.class);
Integer number = cache.get("count", Integer.class);
User user = cache.get("user_123", User.class);
```

**Returns `null` if:**
- Key doesn't exist
- Key has expired
- Type mismatch

### 4. Check Key Existence

```java
if (cache.exists("key")) {
    // Key exists and is not expired
    String value = cache.get("key", String.class);
}
```

### 5. Delete Keys

```java
cache.delete("key");
```

### 6. Get Remaining TTL

```java
long ttl = cache.ttl("key");

// Return values:
// -1  → Key has no expiration
// -2  → Key doesn't exist
// >0  → Remaining milliseconds until expiration
```

### 7. Complete Example

```java
import com.miniredis.MiniRedis;
import com.miniredis.MiniRedisCache;

public class Example {
    public static void main(String[] args) throws InterruptedException {
        MiniRedisCache cache = MiniRedis.getInstance();
        
        // Set with TTL
        cache.set("temp_data", "This will expire", 5000); // 5 seconds
        
        // Check immediately
        System.out.println(cache.get("temp_data", String.class)); 
        // Output: This will expire
        
        System.out.println("TTL: " + cache.ttl("temp_data") + " ms");
        // Output: TTL: ~5000 ms
        
        // Wait for expiration
        Thread.sleep(6000);
        
        // Check after expiration
        System.out.println(cache.get("temp_data", String.class)); 
        // Output: null
        
        System.out.println(cache.exists("temp_data")); 
        // Output: false
    }
}
```

---

## 🌱 Spring Boot Integration

### Step 1: Configuration Class

Create a configuration class to register MiniRedis as a Spring bean:

```java
package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.miniredis.MiniRedisCache;
import com.miniredis.ExpiryCleaner;

@Configuration
public class CacheConfig {
    
    @Bean
    public MiniRedisCache miniRedisCache() {
        MiniRedisCache cache = new MiniRedisCache(new JavaSerializer());
        // Start automatic expiry removal (runs every 1 second)
        ExpiryCleaner.start(cache);
        return cache;
    }
}
```

### Step 2: Use in Service

```java
package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.miniredis.MiniRedisCache;

@Service
public class UserService {
    
    @Autowired
    private MiniRedisCache cache;
    
    public User getUser(int id) {
        String key = "USER_" + id;
        
        // Try cache first
        User cached = cache.get(key, User.class);
        if (cached != null) {
            return cached; // Cache HIT
        }
        
        // Cache MISS - fetch from database
        User user = userRepository.findById(id);
        
        // Cache for 10 minutes (600000 ms)
        if (user != null) {
            cache.set(key, user, 600000);
        }
        
        return user;
    }
    
    public void invalidateUser(int id) {
        cache.delete("USER_" + id);
    }
}
```

### Step 3: REST Controller Example

```java
package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.example.service.UserService;
import com.example.model.User;

@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/{id}")
    public User getUser(@PathVariable int id) {
        return userService.getUser(id);
    }
    
    @DeleteMapping("/{id}/cache")
    public String clearCache(@PathVariable int id) {
        userService.invalidateUser(id);
        return "Cache cleared for user " + id;
    }
}
```

### Real-World Demo Project

Check out the complete Spring Boot integration example:

🔗 **[mini-redis-demo](https://github.com/DakhinTudu/mini-redis-demo)**

This demo project shows:
- Full Spring Boot integration
- MySQL database with JPA
- Custom Spring Cache Manager implementation
- RESTful API with caching
- Cache monitoring and logging

---

## 📚 API Reference

### `MiniRedisCache` Class

#### Constructor

```java
MiniRedisCache(Serializer serializer)
```

Creates a new cache instance with the specified serializer.

#### Methods

##### `void set(String key, Object value, long ttlMillis)`

Stores a key-value pair in the cache.

**Parameters:**
- `key` - The cache key (String)
- `value` - The value to store (must be Serializable)
- `ttlMillis` - Time-to-live in milliseconds. Use `-1` for no expiration.

**Example:**
```java
cache.set("user:1", userObject, 300000); // 5 minutes
cache.set("config", configObject, -1);   // Never expires
```

##### `<T> T get(String key, Class<T> clazz)`

Retrieves a value from the cache with type safety.

**Parameters:**
- `key` - The cache key
- `clazz` - The expected class type

**Returns:**
- The cached value, or `null` if key doesn't exist or has expired

**Example:**
```java
String value = cache.get("key", String.class);
User user = cache.get("user:1", User.class);
```

##### `boolean exists(String key)`

Checks if a key exists and is not expired.

**Returns:**
- `true` if key exists and is valid
- `false` if key doesn't exist or has expired

**Example:**
```java
if (cache.exists("session_token")) {
    // Process session
}
```

##### `void delete(String key)`

Removes a key from the cache.

**Example:**
```java
cache.delete("user:1");
```

##### `long ttl(String key)`

Returns the remaining time-to-live for a key.

**Returns:**
- `-1` - Key has no expiration
- `-2` - Key doesn't exist
- `>0` - Remaining milliseconds until expiration

**Example:**
```java
long remaining = cache.ttl("session");
if (remaining < 60000) {
    // Refresh session if less than 1 minute remaining
}
```

### `MiniRedis` Class (Singleton Helper)

#### `static MiniRedisCache getInstance()`

Returns a singleton instance of MiniRedisCache with automatic expiry cleaner.

**Example:**
```java
MiniRedisCache cache = MiniRedis.getInstance();
```

### `ExpiryCleaner` Class

#### `static void start(MiniRedisCache cache)`

Starts the background thread that removes expired keys every second.

**Note:** This is automatically called when using `MiniRedis.getInstance()`.

**Example:**
```java
MiniRedisCache cache = new MiniRedisCache(new JavaSerializer());
ExpiryCleaner.start(cache);
```

---

## 🏗️ Architecture

### Core Components

#### 1. **MiniRedisCache**
- Main cache engine
- Uses `ConcurrentHashMap<String, RedisValue>` for storage
- Thread-safe operations
- Handles serialization/deserialization

#### 2. **RedisValue**
- Wraps cached values with expiration metadata
- Stores:
  - Serialized byte array of the value
  - Expiration timestamp
- Provides `isExpired()` and `getRemainingTTL()` methods

#### 3. **ExpiryCleaner**
- Background scheduler using `ScheduledExecutorService`
- Runs every 1 second
- Iterates through all keys and removes expired entries
- Prevents memory leaks from expired keys

#### 4. **Serializer Interface**
- Abstraction for serialization
- Default implementation: `JavaSerializer` (Java serialization)
- Allows custom serialization strategies

### Data Flow

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │
       │ set(key, value, ttl)
       ▼
┌─────────────────────┐
│  MiniRedisCache     │
│  ┌───────────────┐  │
│  │ Serializer    │  │
│  └───────┬───────┘  │
│          │          │
│          ▼          │
│  ┌───────────────┐  │
│  │ RedisValue    │  │
│  │ - value[]     │  │
│  │ - expiryTime  │  │
│  └───────┬───────┘  │
│          │          │
│          ▼          │
│  ┌───────────────┐  │
│  │ Concurrent    │  │
│  │ HashMap       │  │
│  └───────────────┘  │
└─────────────────────┘
       │
       │
       ▼
┌─────────────────────┐
│  ExpiryCleaner      │
│  (Every 1 second)   │
│  Removes expired    │
└─────────────────────┘
```

---

## ⚡ Performance Considerations

### Strengths

- **Fast Reads**: O(1) average time complexity for get operations
- **Thread-Safe**: ConcurrentHashMap provides excellent concurrency
- **Low Memory Overhead**: Minimal object overhead per entry
- **No Network Latency**: In-memory operations are extremely fast

### Best Practices

1. **Key Naming Convention**
   ```java
   // Good: Namespaced keys
   cache.set("user:123", user, 300000);
   cache.set("session:abc", session, 3600000);
   
   // Avoid: Generic keys
   cache.set("data", data, 300000); // Too generic
   ```

2. **TTL Selection**
   ```java
   // Short-lived data (sessions, OTPs)
   cache.set("otp:123", otp, 30000); // 30 seconds
   
   // Medium-lived data (user data)
   cache.set("user:123", user, 300000); // 5 minutes
   
   // Long-lived data (configuration)
   cache.set("config:app", config, 3600000); // 1 hour
   ```

3. **Memory Management**
   - Set appropriate TTLs to prevent unbounded growth
   - Monitor cache size in production
   - Use `delete()` to manually remove keys when no longer needed

4. **Type Safety**
   ```java
   // Always specify the expected type
   User user = cache.get("user:1", User.class);
   
   // Avoid raw Object casting
   // Object obj = cache.get("user:1", Object.class); // Not recommended
   ```

---

## ⚠️ Limitations

1. **In-Memory Only**: Data is lost on application restart
2. **Single JVM**: Not distributed; each application instance has its own cache
3. **No Persistence**: No disk-based persistence
4. **No Clustering**: No support for cache replication or clustering
5. **Serialization Required**: All values must be Serializable
6. **Memory Bound**: Limited by available heap memory
7. **No Advanced Features**: No support for lists, sets, hashes, or pub/sub

### When to Use MiniRedis

✅ **Good For:**
- Development and testing environments
- Small to medium applications
- Single-instance deployments
- Simple caching needs
- Prototyping and demos
- When you want zero external dependencies

❌ **Not Suitable For:**
- Distributed systems requiring shared cache
- Large-scale applications with millions of keys
- Applications requiring persistence
- Complex data structures (lists, sets, hashes)
- Production systems requiring high availability

### When to Use Full Redis

Consider using full Redis when you need:
- Distributed caching across multiple instances
- Persistence to disk
- Advanced data structures
- Pub/Sub messaging
- Clustering and high availability
- Large-scale deployments

---

## 🗺️ Roadmap

### Planned Features

- [ ] **Wildcard Key Search**: Pattern matching (e.g., `keys("user:*")`)
- [ ] **Atomic Operations**: `incr()`, `decr()`, `incrementBy()`
- [ ] **JSON Serialization Mode**: Optional JSON-based serialization
- [ ] **Persistence Options**: Optional disk-based persistence
- [ ] **Hash Data Type**: Redis-like HSET/HGET operations
- [ ] **List Operations**: LPUSH, RPUSH, LPOP, RPOP
- [ ] **Set Operations**: SADD, SMEMBERS, SREM
- [ ] **Cache Statistics**: Hit/miss ratios, size metrics
- [ ] **Eviction Policies**: LRU, LFU eviction strategies
- [ ] **TTL in Seconds**: Convenience method for seconds-based TTL

### Version History

- **v1.0.0** (Current)
  - Core key-value operations
  - TTL support
  - Automatic expiry cleanup
  - Thread-safe operations
  - Spring Boot integration support

---

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

### Getting Started

1. **Fork the Repository**
   ```bash
   git clone https://github.com/DakhinTudu/mini-redis.git
   cd mini-redis
   ```

2. **Create a Feature Branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```

3. **Make Your Changes**
   - Write clean, well-documented code
   - Follow existing code style
   - Add tests for new features
   - Update documentation

4. **Commit Your Changes**
   ```bash
   git commit -m "Add amazing feature"
   ```

5. **Push to Your Fork**
   ```bash
   git push origin feature/amazing-feature
   ```

6. **Open a Pull Request**
   - Describe your changes clearly
   - Reference any related issues
   - Wait for review and feedback

### Contribution Guidelines

- Follow Java coding conventions
- Write meaningful commit messages
- Add Javadoc comments for public APIs
- Include unit tests for new features
- Update README.md if adding new features
- Ensure all tests pass before submitting

### Reporting Issues

Found a bug? Have a feature request? Please open an issue on GitHub with:
- Clear description of the problem/feature
- Steps to reproduce (for bugs)
- Expected vs actual behavior
- Java version and environment details

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2025 Dakhin Tudu

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## 🙏 Acknowledgments

- Inspired by Redis - the popular in-memory data structure store
- Built with Java 17 and modern Java best practices
- Thanks to all contributors and users of this library

---

## 📞 Support

- **GitHub Issues**: [Report bugs or request features](https://github.com/DakhinTudu/mini-redis/issues)
- **Demo Project**: [See MiniRedis in action](https://github.com/DakhinTudu/mini-redis-demo)
- **Documentation**: This README and inline Javadoc comments

---

## ⭐ Show Your Support

If you find MiniRedis useful, please consider:
- ⭐ Starring this repository
- 🐛 Reporting bugs
- 💡 Suggesting new features
- 🔧 Contributing code
- 📢 Sharing with others

---

**Happy Caching! 🚀**

Made with ❤️ by [Dakhin Tudu](https://github.com/DakhinTudu)

