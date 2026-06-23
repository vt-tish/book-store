package com.vttish.bookstore.common.logging;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Locale;

@Aspect
@Component
@RequiredArgsConstructor
public class ServiceLoggingAspect {
    private final MessageSource messageSource;

    @Pointcut("execution(* com.vttish.bookstore..service.impl.*.*(..))")
    public void serviceImplementationMethods() {}

    @Around("serviceImplementationMethods()")
    public Object logServiceAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        Logger logger = LoggerFactory.getLogger(targetClass);
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        logger.info("Executing {}() with arguments: {}", methodName, Arrays.toString(args));

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            logger.info("Finished {}() in {} ms. Returned: {}", methodName, executionTime, result);
            return result;

        } catch (Throwable ex) {
            String message = messageSource.getMessage(
                    ex.getMessage(),
                    null,
                    ex.getMessage(),
                    Locale.ENGLISH
            );
            logger.error("Exception in {}() with cause: {}", methodName, message);
            throw ex;
        }
    }
}
