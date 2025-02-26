package se.java.security.controllers;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/cache-stats")
public class CacheStatsController {

    private final CacheManager cacheManager;

    public CacheStatsController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @GetMapping
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();

        if (cacheManager instanceof CaffeineCacheManager) {
            CaffeineCacheManager caffeineCacheManager = (CaffeineCacheManager) cacheManager;

            caffeineCacheManager.getCacheNames().forEach(name -> {
                com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache =
                        (com.github.benmanes.caffeine.cache.Cache<Object, Object>)
                                caffeineCacheManager.getCache(name).getNativeCache();

                CacheStats cacheStats = nativeCache.stats();

                Map<String, Object> cacheData = new HashMap<>();
                cacheData.put("hitCount", cacheStats.hitCount());
                cacheData.put("missCount", cacheStats.missCount());
                cacheData.put("hitRate", cacheStats.hitRate());
                cacheData.put("missRate", cacheStats.missRate());
                cacheData.put("evictionCount", cacheStats.evictionCount());

                stats.put(name, cacheData);
            });
        }

        return stats;
    }
}