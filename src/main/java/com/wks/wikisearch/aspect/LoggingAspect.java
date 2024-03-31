package com.wks.wikisearch.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.wks.wikisearch.service.*.*(..))")
    public void logServiceMethods() {
    }

    @AfterThrowing(pointcut = "logServiceMethods()", throwing = "exception")
    public void logException(final JoinPoint joinPoint, final Throwable exception) {
        String methodName = joinPoint.getSignature().getName();
        LOGGER.error("Exception thrown in method: {} with message: {}", methodName, exception.getMessage());
    }

    @Before("logServiceMethods()")
    public void logMethodCall(final JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        LOGGER.info("Method called: {}", methodName);
    }

    @Around("logServiceMethods()")
    public Object logExecutionTime(final ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;

        LOGGER.info("{} executed in {}ms", joinPoint.getSignature(), executionTime);

        return proceed;
    }

    @AfterReturning(pointcut = "logServiceMethods()", returning = "result")
    public void logMethodReturn(final JoinPoint joinPoint, final Object result) {
        String methodName = joinPoint.getSignature().getName();
        LOGGER.info("Method return: {} returned {}", methodName, result);
    }
}
