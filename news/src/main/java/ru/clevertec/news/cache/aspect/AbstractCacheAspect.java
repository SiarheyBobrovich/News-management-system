package ru.clevertec.news.cache.aspect;

import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import ru.clevertec.news.cache.algoritm.CacheAlgorithm;
import ru.clevertec.news.cache.factory.CacheAlgorithmFactory;

public abstract class AbstractCacheAspect<K, R> {

    private final CacheAlgorithm<Object, Object> cache;

    public AbstractCacheAspect(CacheAlgorithmFactory algorithmFactory) {
        cache = algorithmFactory.getCacheAlgorithm();
    }

    /**
     * Возвращает объект из кэша по заданному идентификатору или вызывает целевой метод и сохраняет его результат в кэш.
     *
     * @param pjp объект {@link ProceedingJoinPoint}, содержащий информацию о целевом методе
     * @param id  идентификатор объекта типа K
     * @return объект из кэша или результат вызова целевого метода
     */
    public Object getCached(ProceedingJoinPoint pjp, K id) {
        return cache.get(id)
                .orElseGet(() -> {
                    Object proceed = proceed(pjp);
                    cache.put(id, proceed);
                    return proceed;
                });
    }

    /**
     * Удаляет объект из кэша по заданному идентификатору.
     *
     * @param id идентификатор объекта типа K
     */
    public void deleteFromCache(K id) {
        cache.delete(id);
    }

    /**
     * Сохраняет объект в кэш по заданному идентификатору.
     *
     * @param k идентификатор объекта типа K
     * @param o объект, который нужно сохранить в кэш
     */
    protected void putToCache(K k, Object o) {
        cache.put(k, o);
    }

    /**
     * Вызывает целевой метод и возвращает его результат.
     *
     * @param point объект {@link ProceedingJoinPoint}, содержащий информацию о целевом методе
     * @return результат вызова целевого метода
     */
    @SneakyThrows
    protected Object proceed(ProceedingJoinPoint point) {
        return point.proceed();
    }

    /**
     * Очищает кэш от объектов
     */
    public void clear() {
        cache.clear();
    }
}
