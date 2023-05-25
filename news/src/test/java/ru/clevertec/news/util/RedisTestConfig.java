package ru.clevertec.news.util;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;

@Profile("test")
@EnableCaching
@TestConfiguration
@ImportAutoConfiguration(RedisAutoConfiguration.class)
@TestPropertySource(properties = {
        "spring.cache.type=redis"
})
public class RedisTestConfig {

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(30))
                .disableCachingNullValues();
    }
}
