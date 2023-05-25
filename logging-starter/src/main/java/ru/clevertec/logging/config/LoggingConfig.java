package ru.clevertec.logging.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.clevertec.logging.aspect.LoggingAspect;

@Configuration
@ConditionalOnMissingBean(LoggingAspect.class)
@ConditionalOnProperty(prefix = "spring.logging", name = "enabled", havingValue = "true", matchIfMissing = true)
public class LoggingConfig {

    @Bean
    public LoggingAspect loggingAspect() {
        return new LoggingAspect();
    }
}
