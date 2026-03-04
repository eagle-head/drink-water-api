package br.com.drinkwater.config;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.drinkwater.config.properties.CacheProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringJUnitConfig({CacheConfig.class, CacheConfigTest.TestCachePropertiesConfig.class})
final class CacheConfigTest {

    @Configuration
    static class TestCachePropertiesConfig {

        @Bean
        CacheProperties cacheProperties() {
            return new CacheProperties(10_000, 5);
        }
    }

    @Autowired private CacheManager cacheManager;

    @Test
    void cacheManagerShouldBeCaffeineCacheManager() {
        assertThat(cacheManager).isInstanceOf(CaffeineCacheManager.class);
    }

    @Test
    void userIdByPublicIdCacheShouldExist() {
        assertThat(cacheManager.getCache("userIdByPublicId")).isNotNull();
    }

    @Test
    void cacheNamesShouldContainOnlyUserIdByPublicId() {
        assertThat(cacheManager.getCacheNames()).containsExactly("userIdByPublicId");
    }
}
