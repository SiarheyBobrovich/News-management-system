package ru.clevertec.exception.handling.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.clevertec.exception.handling.handler.GlobalHandlerAdvice;

@Configuration
@ConditionalOnProperty(prefix = "spring.exception.handling", name = "enabled", havingValue = "true", matchIfMissing = true)
public class HandlerConfig {

    @Bean
    @ConditionalOnMissingBean
    public GlobalHandlerAdvice globalHandlerAdvice() {
        return new GlobalHandlerAdvice();
    }
}
