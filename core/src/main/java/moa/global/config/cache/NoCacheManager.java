package moa.global.config.cache;

import java.util.Collection;
import java.util.Collections;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;

public class NoCacheManager implements CacheManager {

    private final Cache noCache = new ConcurrentMapCache("nocache");

    public Cache getCache(String name) {
        noCache.clear();
        return noCache;
    }

    @Override
    public Collection<String> getCacheNames() {
        return Collections.emptyList();
    }
}
