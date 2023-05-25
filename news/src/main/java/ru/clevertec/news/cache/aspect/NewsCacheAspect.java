package ru.clevertec.news.cache.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import ru.clevertec.news.cache.factory.CacheAlgorithmFactory;
import ru.clevertec.news.data.ResponseNewsView;

@Aspect
@Component
@ConditionalOnBean(CacheAlgorithmFactory.class)
public class NewsCacheAspect extends AbstractCacheAspect<Long, ResponseNewsView> {

    public NewsCacheAspect(CacheAlgorithmFactory algorithmFactory) {
        super(algorithmFactory);
    }

    @Pointcut(value = "execution(public * ru.clevertec.news.service.NewsService.get*(..)) && args(id)", argNames = "id")
    void getMethodPointcut(Long id) {
    }

    @Pointcut(value = "execution(public * ru.clevertec.news.service.NewsService.create(..))")
    void createMethodPointcut() {
    }

    @Pointcut(value = "execution(public * ru.clevertec.news.service.NewsService.update(..))")
    void updateMethodPointcut() {
    }

    @Pointcut(value = "execution(public * ru.clevertec.news.service.NewsService.delete(..)) && args(id)", argNames = "id")
    void deleteMethodPointcut(Long id) {
    }

    @Around(value = "ru.clevertec.news.cache.aspect.AnnotationPointCut.cacheGetPointCut() &&" +
            "getMethodPointcut(id)", argNames = "pjp,id")
    public Object getCached(ProceedingJoinPoint pjp, Long id) {
        return super.getCached(pjp, id);
    }

    @AfterReturning(value = "ru.clevertec.news.cache.aspect.AnnotationPointCut.cacheEvictPointCut() &&" +
            "deleteMethodPointcut(id)", argNames = "id")
    public void deleteFromCache(Long id) {
        super.deleteFromCache(id);
    }

    @AfterReturning(value = "ru.clevertec.news.cache.aspect.AnnotationPointCut.cachePutPointCut()" +
            " && createMethodPointcut() || updateMethodPointcut()", returning = "retVal")
    public void putToCache(ResponseNewsView retVal) {
        super.putToCache(retVal.id(), retVal);
    }
}
