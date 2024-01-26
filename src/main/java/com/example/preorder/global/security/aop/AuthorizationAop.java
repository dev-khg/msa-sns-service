package com.example.preorder.global.security.aop;

import com.example.preorder.common.exception.BadRequestException;
import com.example.preorder.global.security.LoginUser;
import com.example.preorder.global.security.annotation.AuthorizationRequired;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class AuthorizationAop {

    @Before("@annotation(com.example.preorder.global.security.annotation.AuthorizationRequired)")
    public void processCustom(JoinPoint joinPoint) {

        if (!isValidAuthentication()) {
            throw new BadRequestException("please do login.");
        }
    }

    private AuthorizationRequired getAnnotation(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        return method.getAnnotation(AuthorizationRequired.class);
    }

    private boolean isValidAuthentication() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return principal != null && (principal instanceof LoginUser);
    }
}
