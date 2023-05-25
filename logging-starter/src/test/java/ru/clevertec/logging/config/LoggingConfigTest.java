package ru.clevertec.logging.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import ru.clevertec.logging.aspect.LoggingAspect;

import static org.assertj.core.api.Assertions.assertThat;

class LoggingConfigTest {

    @Test
    void shouldAutoconfigurationApplied() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(LoggingConfig.class))
                .run(context ->
                        assertThat(context).hasNotFailed()
                                .hasSingleBean(LoggingAspect.class)
                );
    }

    @Test
    void shouldAutoconfigurationNotAppliedByProperty() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(LoggingConfig.class))
                .withPropertyValues("spring.logging.enabled", "false")
                .run(context ->
                        assertThat(context).hasNotFailed()
                                .doesNotHaveBean(LoggingAspect.class)
                );
    }

    @Test
    void shouldAutoconfigurationNotAppliedByBean() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(LoggingConfig.class))
                .withBean(LoggingAspect.class)
                .run(context ->
                        assertThat(context).hasNotFailed()
                                .hasSingleBean(LoggingAspect.class)
                                .doesNotHaveBean(LoggingConfig.class)
                );
    }
}
