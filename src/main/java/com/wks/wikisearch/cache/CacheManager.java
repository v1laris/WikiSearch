/*package com.wks.wikisearch.cache;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public abstract class CacheManager<K, V> {
    private final Map<K, V> cacheMap;
    private final Integer maxSize;
    public void put(final K key, final V value) {
        cacheMap.put(key, value);
    }

    public V get(final K key) {
        return cacheMap.get(key);
    }

    public void remove(final K key) {
        cacheMap.remove(key);
    }

    public boolean containsKey(final K key) {
        return cacheMap.containsKey(key);
    }

    protected CacheManager(@Value("${cache.maxSize}") final Integer size) {
            this.maxSize = size;
            this.cacheMap = new LinkedHashMap<>() {
                @Override
                protected boolean removeEldestEntry(
                        final Map.Entry eldest) {
                    return size() > CacheManager.this.maxSize;
                }
            };
    }
}*/

package com.wks.wikisearch.cache;

import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public final class CacheManager {
    private static final Map<String, Object> CACHE = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(
        final Map.Entry eldest) {
            return size() > CACHE_MAX_SIZE;
        }
    };
    private static final int CACHE_MAX_SIZE = 1000;

    public static void put(final String key, final Object value) {
        CACHE.put(key, value);
    }

    public static Object get(final String key) {
        return CACHE.get(key);
    }

    public static void remove(final String key) {
        CACHE.remove(key);
    }

    public static boolean containsKey(final String key) {
        return CACHE.containsKey(key);
    }

    private CacheManager() { };
}
