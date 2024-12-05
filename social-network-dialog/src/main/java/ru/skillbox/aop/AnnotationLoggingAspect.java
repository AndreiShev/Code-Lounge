package ru.skillbox.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Aspect
@Component
public class AnnotationLoggingAspect {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Pointcut("@annotation(ru.skillbox.annotation.Loggable)")
    public void loggableMethods() {}

    @Before("loggableMethods()")
    public void logBeforeMethod(JoinPoint joinPoint) {
        logger.info("Вызов метода: {}", joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "loggableMethods()", returning = "result")
    public void logAfterMethod(JoinPoint joinPoint, Object result) {
        logger.info("Метод {} выполнен успешно, возвращаемое значение: {}", joinPoint.getSignature().getName(), result);
    }
}
