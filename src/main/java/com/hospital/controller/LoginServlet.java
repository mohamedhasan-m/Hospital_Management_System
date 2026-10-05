package com.hospital.controller;

import com.hospital.model.User;
import com.hospital.service.AuthService;
import com.hospital.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * URL: /api/login
 *   GET  -> tells the browser if somebody is logged in
 *   POST -> checks username + password and starts the session
 */
@WebServlet("/api/login")
public class LoginServlet extends BaseServlet {

    private final AuthService authService = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            sendJson(resp, 200, "{\"loggedIn\":true,\"name\":" + JsonUtil.str(user.getFullName()) + "}");
        } else {
            sendJson(resp, 200, "{\"loggedIn\":false}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            User user = authService.login(req.getParameter("username"), req.getParameter("password"));

            if (user == null) {
                sendJson(resp, 401, JsonUtil.message(false, "Invalid username or password"));
                return;
            }

            // Throw away any old session and start a fresh one (safer)
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession session = req.getSession(true);
            session.setAttribute("user", user);              // this attribute means "logged in"
            session.setMaxInactiveInterval(30 * 60);         // 30 minutes

            success(resp, "Login successful");
        } catch (Exception e) {
            handleError(resp, e);
        }
    }
}
