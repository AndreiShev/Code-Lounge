package ru.skillbox.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class GlobalErrorLogAspect {
  @AfterThrowing(pointcut = "within(ru.skillbox.service..*) OR within(ru.skillbox.security..*)", throwing = "ex")
  public void errorLogging (JoinPoint jp, Exception ex) {
    log.error("Source: {} \n error: {}", jp.getSignature().toString(), ex.getMessage());
  }
}
