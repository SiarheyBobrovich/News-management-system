package ru.clevertec.news.cache.factory;

import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.clevertec.news.cache.algoritm.CacheAlgorithm;

@Component
@ConditionalOnProperty(prefix = "spring.cache", name = "algorithm")
@Profile("!prod")
public abstract class CacheAlgorithmFactory {

    /**
     * Возвращает объект {@link CacheAlgorithm}, который определяет алгоритм работы с кэшем.
     *
     * @return объект {@link CacheAlgorithm}, содержащий логику кэширования
     */
    @Lookup
    public abstract CacheAlgorithm<Object, Object> getCacheAlgorithm();
}
