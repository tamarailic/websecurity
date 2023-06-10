package com.websecurity.websecurity.logging;

import com.websecurity.websecurity.security.jwt.JwtTokenUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@Aspect
@Component
public class WSLoggerAspect {

    Logger logger = LoggerFactory.getLogger(WSLoggerAspect.class);
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Before("@annotation(WSLogger)")
    public void checkUserIdentity(JoinPoint joinPoint) {
        String action = joinPoint.toShortString().split("\\.")[1].replace("(", "");
        int indexOfData = findIndexOfStringParameter(joinPoint);

        String value = "";
        String parameter = "";
        if (indexOfData != -1) {
            value = (String) joinPoint.getArgs()[indexOfData];
            String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
            parameter = parameterNames[indexOfData];
        }

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        String authorization = request.getHeader("Authorization");
        if (authorization == null) return;

        String token = authorization.split(" ")[1];
        String username = jwtTokenUtil.getUsernameFromToken(token);

        logger.info(String.format("User %s has executed %s action on %s %s", username, action, value, parameter));

    }

    private int findIndexOfStringParameter(JoinPoint joinPoint) {
        String[] tokens = joinPoint.getSignature().toString().replace(")", "").split("\\(")[1].split(",");
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equals("String")) {
                return i;
            }
        }
        return -1;
    }
}
