package ru.clevertec.news.util;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public interface RedisTestContainer {

    GenericContainer<?> container = new GenericContainer<>("redis:7.2-rc2-alpine")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void setPort(DynamicPropertyRegistry registry) {
        container.start();
        registry.add("spring.data.redis.port", () -> container.getMappedPort(6379));
        registry.add("spring.data.redis.host", container::getHost);
    }
}
