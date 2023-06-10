package com.websecurity.websecurity.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class WSLoggerAspect {

    Logger logger = LoggerFactory.getLogger(WSLoggerAspect.class);

    @Before("@annotation(WSLogger)")
    public void checkUserIdentity(JoinPoint joinPoint) {
        String[] tokens = joinPoint.getSignature().toString().replace(")", "").split("\\(")[1].split(",");
        for (String token : tokens) {
            logger.info("Only this message");
        }
    }

}
