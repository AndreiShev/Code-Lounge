package ru.skillbox.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before("@annotation(loggable)")
    public void logMethod(JoinPoint joinPoint, Loggable loggable) {

        if (loggable.type() == LogType.CONTROLLER) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            log.info("controller: {} - method: {} - url: {} - http method {} - args: {}",
                    joinPoint.getTarget().getClass().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    request.getMethod(),
                    request.getRequestURI(),
                    joinPoint.getArgs());

        } else if (loggable.type() == LogType.SERVICE) {
            log.info("service {} - method: {} - args: {}",
                    joinPoint.getTarget().getClass().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    joinPoint.getArgs());
        }
    }

}
