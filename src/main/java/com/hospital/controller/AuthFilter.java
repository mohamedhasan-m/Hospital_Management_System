package com.hospital.controller;

import com.hospital.util.JsonUtil;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Filter initialization
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        req.setCharacterEncoding("UTF-8");

        String path = req.getRequestURI()
                .substring(req.getContextPath().length());

        if (isPublic(path)) {
            chain.doFilter(req, resp);
            return;
        }

        HttpSession session = req.getSession(false);

        boolean loggedIn =
                session != null &&
                        session.getAttribute("user") != null;

        if (loggedIn) {

            resp.setHeader("Cache-Control", "no-store");

            chain.doFilter(req, resp);

        } else if (path.startsWith("/api/")) {

            resp.setStatus(401);
            resp.setContentType("application/json");

            resp.getWriter().write(
                    JsonUtil.message(false, "Please login first")
            );

        } else {

            resp.sendRedirect(
                    req.getContextPath() + "/login.html"
            );
        }
    }

    private boolean isPublic(String path) {

        return path.isEmpty()
                || path.equals("/")
                || path.equals("/login.html")
                || path.equals("/api/login")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.equals("/favicon.ico");
    }

    @Override
    public void destroy() {
        // Filter cleanup
    }
}