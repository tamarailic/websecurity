package com.websecurity.websecurity.logging;

import com.websecurity.websecurity.DTO.CredentialsDTO;
import com.websecurity.websecurity.DTO.OauthInfoDTO;
import com.websecurity.websecurity.DTO.UserDTO;
import com.websecurity.websecurity.security.jwt.JwtTokenUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class WSLoggerAuthAspect {

    Logger logger = LoggerFactory.getLogger(WSLoggerAspect.class);
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Before("@annotation(WSLoggerAuth)")
    public void logActivity(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String[] parameterNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();

        String importantParam = parameterNames[0];
        Object importantArgument = args[0];

        String who;
        String action = joinPoint.toShortString().split("\\.")[1].replace("(", "");

        switch (importantParam) {
            case "userDTO":
                who = ((UserDTO) importantArgument).username;
                break;
            case "credentialsDTO":
                who = ((CredentialsDTO) importantArgument).getEmail();
                break;
            case "oauthInfoDTO":
                who = ((OauthInfoDTO) importantArgument).getUsername();
                break;
            default:
                return;
        }

        logger.info(String.format("User %s has %s", who, action));

    }
}
