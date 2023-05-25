package ru.clevertec.news.cache.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AnnotationPointCut {

    @Pointcut("@annotation(org.springframework.cache.annotation.Cacheable)")
    void cacheGetPointCut() {
    }

    @Pointcut("@annotation(org.springframework.cache.annotation.CachePut)")
    void cachePutPointCut() {
    }

    @Pointcut("@annotation(org.springframework.cache.annotation.CacheEvict)")
    void cacheEvictPointCut() {
    }
}
