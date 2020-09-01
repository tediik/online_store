package com.jm.online_store.config.handler;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.StringJoiner;

/**
 * Перехватчик для получения имейла и ролей залогиненного пользователя из секьюрного контекста.
 * Оба значения передаются в js-файл через куки.
 */
@Component
public class CurrentUserInterceptor implements HandlerInterceptor {
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            StringJoiner roles = new StringJoiner("|");
            authentication.getAuthorities().forEach(authority -> roles.add(authority.getAuthority()
                    .replace("ROLE_", "")));
            Cookie cookieEmail = new Cookie("email", authentication.getName());
            Cookie cookieRoles = new Cookie("roles", roles.toString());
            response.addCookie(cookieEmail);
            response.addCookie(cookieRoles);
        }
    }
}
