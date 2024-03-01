package moa.global.config.cache;

import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableCaching
@Configuration
@RequiredArgsConstructor
public class CacheConfig {

    public static final String WINCUBE_ACCESS_TOKEN_CACHE_NAME = "wincubeAccessToken";
    public static final String WINCUBE_ACCESS_TOKEN_CACHE_MANAGER_NAME = "wincubeAccessTokenCacheManager";

    private static final String API = "api";

    private final RedisConnectionFactory redisConnectionFactory;

    @Value("${application.type:#{null}}")
    private String applicationType;

    @Bean
    public CacheManager wincubeAccessTokenCacheManager() {
        if (!API.equals(applicationType)) {
            return new NoCacheManager();
        }
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofHours(23));  // 캐시 저장 시간 23시간 설정, 윈큐브 토큰 지속시간이 24시간임
        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration)
                .build();
    }
}
