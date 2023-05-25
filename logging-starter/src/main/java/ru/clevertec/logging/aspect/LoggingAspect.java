package ru.clevertec.logging.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Slf4j
@Aspect
public class LoggingAspect {

    @Pointcut("@annotation(ru.clevertec.logging.annotation.Logging)")
    private void annotationPointcut() {
    }

    @Pointcut("@within(ru.clevertec.logging.annotation.Logging)")
    private void loggingByType() {
    }

    @Around("annotationPointcut() || loggingByType()")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        Object methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        log.info("Before: {}.{}({})", className, methodName, args);

        Object result = joinPoint.proceed();

        log.info("After: result::{}", result);
        return result;
    }
}
