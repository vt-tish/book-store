package com.vttish.bookstore.common.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class SchedulerLoggingAspect {

    @Pointcut("execution(* com.vttish.bookstore..scheduler.*.*(..)) && " +
            "!@annotation(org.springframework.scheduling.annotation.Scheduled)")
    public void schedulerPackageMethods() {}

    @Around("schedulerPackageMethods()")
    public Object logSchedulerExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        Class<?> targetClass = joinPoint.getTarget().getClass();
        Logger logger = LoggerFactory.getLogger(targetClass);
        String methodName = joinPoint.getSignature().getName();

        logger.info("[SCHEDULER START] Starting background task: {}()", methodName);

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            String message = "";
            if (result instanceof Number) {
                message = String.format(". Removed %d expired records", ((Number) result).intValue());
            }

            logger.info("[SCHEDULER SUCCESS] {}() finished in {} ms{}", methodName, executionTime, message);
            return result;
        } catch (Throwable ex) {
            logger.error("[SCHEDULER FAILED] {}() crashed with cause: {}", methodName, ex.getMessage(), ex);
            return null;
        }
    }
}
