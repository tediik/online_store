package com.jm.online_store.config.filters;

import com.jm.online_store.exception.CommonSettingsNotFoundException;
import com.jm.online_store.model.CommonSettings;
import com.jm.online_store.repository.CommonSettingsRepository;
import lombok.RequiredArgsConstructor;
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
 * роли не совпадают с указанными в параметре rolesMode, которые приходит с фронта в базу common_settings.
 */
@Component
@WebFilter("/admin")
@RequiredArgsConstructor
public class MaintenanceFilter implements Filter {
    private static final boolean STATUS_NORMAL_OPERATION = false;
    private boolean status = STATUS_NORMAL_OPERATION;
    private Set<String> userRoles = new HashSet<>();
    private final CommonSettingsRepository commonSettingsRepository;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain)
            throws IOException, ServletException {
        response.setContentType("application/json; charset=UTF-8;");

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

        CommonSettings commonSettings = commonSettingsRepository.findBySettingName("maintenance_mode")
                .orElseThrow(CommonSettingsNotFoundException::new);
        status = commonSettings.isStatus();
        userRoles = Set.of(commonSettings.getTextValue().split(","));

        if (roles.stream().anyMatch(userRoles::contains) || "/maintenanceMode".equals(path) || path.contains("login") ||
                path.contains("maintenancemode.css")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (status != STATUS_NORMAL_OPERATION) {
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/maintenanceMode");
            return;
        }

        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
