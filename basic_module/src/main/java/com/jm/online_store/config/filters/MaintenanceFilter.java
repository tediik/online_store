package com.jm.online_store.config.filters;

import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

@WebFilter(filterName = "performanceStatistics", urlPatterns = {"/*"})
public class MaintenanceFilter implements Filter {
    @Autowired
    private MaintenanceModel maintenanceModel;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

//        3. Create a Servlet Filter which always ask for this boolean, if it's true redirect, it's not bypass the request.

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }
}
