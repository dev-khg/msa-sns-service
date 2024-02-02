package hg.userservice.infrastructure.security.aop;

import com.example.commonproject.exception.UnAuthorizedException;
import hg.userservice.infrastructure.security.LoginUser;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthorizationAop {

//    @Before("@annotation(hg.userservice.infrastructure.security.annotation.AuthorizationRequired)")
    public void process(JoinPoint joinPoint) {
        if (!isValidAuthentication()) {
            throw new UnAuthorizedException("please do login first.");
        }
    }

    private boolean isValidAuthentication() {
        return (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof LoginUser);
    }
}
