package br.com.drinkwater.config;

import br.com.drinkwater.config.properties.CacheProperties;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    private final CacheProperties cacheProperties;

    public CacheConfig(CacheProperties cacheProperties) {
        this.cacheProperties = cacheProperties;
    }

    @Bean
    public CacheManager cacheManager() {
        var caffeine =
                Caffeine.newBuilder()
                        .maximumSize(cacheProperties.maxSize())
                        .expireAfterWrite(
                                Duration.ofMinutes(cacheProperties.expireAfterWriteMinutes()))
                        .recordStats();

        var manager = new CaffeineCacheManager("userIdByPublicId");
        manager.setCaffeine(caffeine);
        return manager;
    }
}
