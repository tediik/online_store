package com.jm.online_store.config.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Slf4j
public class LoginFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String exceptionMessage = exception.getMessage();
        log.info(exceptionMessage);
        if (exceptionMessage.contains("Bad credentials")) {
            response.sendRedirect("/login?loginBadCredentials=true");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        } else if (exceptionMessage.contains("Аккаунт заблокирован !!!")) {
            response.sendRedirect("/login?accountBlocked=true");
            response.setStatus(HttpStatus.LOCKED.value());
        }

    }
}
