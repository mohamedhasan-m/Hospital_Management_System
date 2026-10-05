package com.hospital.controller;

import com.hospital.util.JsonUtil;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Parent class of all our servlets. It only holds small helper methods
 * so every servlet answers in the same JSON format.
 */
public abstract class BaseServlet extends HttpServlet {

    /** Sends any JSON text with the given HTTP status code. */
    protected void sendJson(HttpServletResponse resp, int status, String json) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json);
    }

    /** {"success":true,"message":"..."} with HTTP 200 */
    protected void success(HttpServletResponse resp, String message) throws IOException {
        sendJson(resp, 200, JsonUtil.message(true, message));
    }

    /**
     * Turns an exception into a JSON reply:
     *  - IllegalArgumentException = the user typed something wrong  -> HTTP 400 with the message
     *  - anything else (for example SQLException)                   -> HTTP 500 with a general message
     */
    protected void handleError(HttpServletResponse resp, Exception e) throws IOException {
        if (e instanceof IllegalArgumentException) {
            sendJson(resp, 400, JsonUtil.message(false, e.getMessage()));
        } else {
            e.printStackTrace();   // full details are visible in the Tomcat console
            sendJson(resp, 500, JsonUtil.message(false, "Server or database error. Check the Tomcat console."));
        }
    }
}
