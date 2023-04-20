package org.freeone.k8s.web.knife.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;


public class UnsafePathFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(UnsafePathFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String requestUri = request.getRequestURI();
        if (requestUri.contains("..")) {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
//            response.setCharacterEncoding("UTF-8");
//            response.setContentType("application/json;charset=utf-8");
            response.setStatus(HttpStatus.NOT_IMPLEMENTED.value());
            return;
        }

        logger.debug("requestUri = {}", requestUri);

        filterChain.doFilter(request, servletResponse);
    }
}
