package ru.clevertec.news.cache.algoritm.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.clevertec.news.cache.algoritm.CacheAlgorithm;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Scope("prototype")
@ConditionalOnProperty(prefix = "spring.cache", name = "algorithm", havingValue = "lru")
public class LruCache<ID, T> implements CacheAlgorithm<ID, T> {

    private final Map<ID, T> cache;
    private final Lock lock;

    public LruCache(@Value("${spring.cache.size}") int size) {
        this.lock = new ReentrantLock();
        this.cache = new LinkedHashMap<>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<ID, T> eldest) {
                return size < cache.size();
            }
        };
    }

    @Override
    public void put(ID id, T o) {
        if (Objects.isNull(id) || Objects.isNull(o)) {
            return;
        }
        lock.lock();
        try {
            cache.put(id, o);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Optional<T> get(ID id) {
        lock.lock();
        try {
            final T currentObj = cache.remove(id);
            final Optional<T> result = Optional.ofNullable(currentObj);
            result.ifPresent(o -> cache.put(id, o));
            return result;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void delete(ID id) {
        lock.lock();
        try {
            cache.remove(id);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clear() {
        cache.clear();
    }
}
