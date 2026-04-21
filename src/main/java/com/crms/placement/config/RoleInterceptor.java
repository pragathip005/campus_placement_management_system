package com.crms.placement.config;

import com.crms.placement.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Component
public class RoleInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();

        // 1. Allow public paths
        List<String> publicPaths = Arrays.asList("/login", "/register", "/logout", "/error", "/css/", "/js/", "/images/", "/favicon.ico");
        if (publicPaths.stream().anyMatch(uri::startsWith)) {
            return true;
        }

        // 2. Check session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("/login");
            return false;
        }

        User user = (User) session.getAttribute("user");
        String role = user.getRole().toUpperCase();

        // 3. Role-based path restriction
        if (uri.startsWith("/hr/")) {
            if (!"HR".equals(role) && !"ADMIN".equals(role)) {
                response.sendRedirect("/login?error=Access Denied (HR only)");
                return false;
            }
        } else if (uri.startsWith("/alumni/dashboard")) {
            if (!"ALUMNI".equals(role) && !"ADMIN".equals(role)) {
                response.sendRedirect("/login?error=Access Denied (Alumni only)");
                return false;
            }
        } else if (uri.startsWith("/admin/")) {
            if (!"ADMIN".equals(role)) {
                response.sendRedirect("/login?error=Access Denied (Admin only)");
                return false;
            }
        } else if (uri.startsWith("/job-board") || uri.startsWith("/job/")) {
            // Usually for Students, but let's allow Admin too
            if (!"STUDENT".equals(role) && !"ADMIN".equals(role)) {
                response.sendRedirect("/login?error=Access Denied (Students only)");
                return false;
            }
        }

        return true;
    }
}
