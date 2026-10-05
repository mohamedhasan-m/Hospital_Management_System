package com.hospital.controller;

import com.hospital.service.DashboardService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** URL: /api/dashboard (GET) - total patients, doctors and appointments. */
@WebServlet("/api/dashboard")
public class DashboardServlet extends BaseServlet {

    private final DashboardService service = new DashboardService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            sendJson(resp, 200, service.getStats().toJson());
        } catch (Exception e) {
            handleError(resp, e);
        }
    }
}
