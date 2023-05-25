package ru.clevertec.logging.aspect;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import ru.clevertec.logging.util.FakeAnnotatedService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(classes = {
        LoggingAspect.class,
        AnnotationAwareAspectJAutoProxyCreator.class,
        FakeAnnotatedService.FakeAnnotatedMethodServiceImpl.class,
        FakeAnnotatedService.FakeAnnotatedClassServiceImpl.class
})
class LoggingAspectTest {


    @Autowired
    @Qualifier("fakeAnnotatedService.FakeAnnotatedMethodServiceImpl")
    private FakeAnnotatedService fakeAnnotatedMethodServiceImpl;

    @Autowired
    @Qualifier("fakeAnnotatedService.FakeAnnotatedClassServiceImpl")
    private FakeAnnotatedService fakeAnnotatedClassService;

    @SpyBean
    private LoggingAspect aspect;

    @Test
    @SneakyThrows
    void logClass() {
        fakeAnnotatedClassService.fakeMethod("Hello");

        verify(aspect, times(1)).log(any());
    }

    @Test
    @SneakyThrows
    void logMethod() {
        fakeAnnotatedMethodServiceImpl.fakeMethod("Hello");

        verify(aspect, times(1)).log(any());
    }
}
