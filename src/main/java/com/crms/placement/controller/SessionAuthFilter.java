package com.crms.placement.controller;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SessionAuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();

        // Allow access to login, register, api, test, and static resources
        if (requestURI.equals("/") || requestURI.startsWith("/login") || requestURI.startsWith("/register") ||
            requestURI.startsWith("/api") || requestURI.startsWith("/test") ||
            requestURI.startsWith("/css") || requestURI.startsWith("/images") ||
            requestURI.startsWith("/js") || requestURI.startsWith("/favicon")) {
            chain.doFilter(request, response);
            return;
        }

        // Check session
        HttpSession session = httpRequest.getSession(false);
        if (session != null && session.getAttribute("loggedInUser") != null) {
            chain.doFilter(request, response);
        } else {
            httpResponse.sendRedirect("/login");
        }
    }
}