package com.jm.online_store.config.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
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

    /**
     * Метод обработки эксепшенов при попытке Аутентификации
     * которые приходят от SecurityUserDetailsService
     *
     * @param request   HttpServletRequest
     * @param response  HttpServletResponse
     * @param exception AuthenticationException
     * @throws IOException      e
     * @throws ServletException e
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        log.debug(exception.getMessage());
        if (exception.getClass().equals(BadCredentialsException.class)) {
            response.sendRedirect("/login?loginBadCredentials=true");
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        } else if (exception.getClass().equals(LockedException.class)) {
            response.sendRedirect("/login?accountBlocked=true");
            response.setStatus(HttpStatus.LOCKED.value());
        }

    }
}
