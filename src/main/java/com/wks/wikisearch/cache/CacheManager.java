package com.wks.wikisearch.cache;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@AllArgsConstructor
public class CacheManager {
    private static final Map<String, Object> cache = new ConcurrentHashMap<>();

    public static void put(String key, Object value) {
        cache.put(key, value);
    }

    public static Object get(String key) {
        return cache.get(key);
    }

    public static void remove(String key) {
        cache.remove(key);
    }

    public static boolean containsKey(String key) {
        return cache.containsKey(key);
    }
}
