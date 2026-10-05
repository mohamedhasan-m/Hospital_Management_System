package com.hospital.controller;

import com.hospital.service.AppointmentService;
import com.hospital.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * URL: /api/appointments
 *   GET  -> list of all appointments
 *   POST -> action = book | cancel
 */
@WebServlet("/api/appointments")
public class AppointmentServlet extends BaseServlet {

    private final AppointmentService service = new AppointmentService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            sendJson(resp, 200, JsonUtil.array(service.getAll()));
        } catch (Exception e) {
            handleError(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getParameter("action");
        try {
            if ("book".equals(action)) {
                service.book(req.getParameter("patientId"), req.getParameter("doctorId"),
                        req.getParameter("date"), req.getParameter("time"), req.getParameter("reason"));
                success(resp, "Appointment booked successfully");

            } else if ("cancel".equals(action)) {
                service.cancel(req.getParameter("id"));
                success(resp, "Appointment cancelled");

            } else {
                throw new IllegalArgumentException("Unknown action");
            }
        } catch (Exception e) {
            handleError(resp, e);
        }
    }
}
