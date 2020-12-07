package com.jm.online_store.config.filters;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Фильтр для закрытия сайта на техническое обслуживание.
 * Сравнивает роли аутентицифированного пользователя и закрывает доступ к страницам сайта, если
 * роли не совпадают с указанными в параметре rolesMode, который приходит с фронта.
 */
@Component
@WebFilter("/admin")
public class MaintenanceFilter implements Filter {
    private static final int MODE_NORMAL_OPERATION = 0;
    private int mode = MODE_NORMAL_OPERATION;
    private Set<String> userRoles = new HashSet<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        if (request.getParameter("maintenance") != null) {
            mode = Integer.parseInt(request.getParameter("maintenance"));
            userRoles = Set.of(request.getParameter("rolesMode").split(","));
            return;
        }

        if (roles.stream().anyMatch(userRoles::contains) || "/maintenanceMode".equals(path) || path.contains("login")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (mode != MODE_NORMAL_OPERATION) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/maintenanceMode");
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
