package com.hospital.controller;

import com.hospital.service.DoctorService;
import com.hospital.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * URL: /api/doctors
 *   GET  -> list of all doctors
 *   POST -> action = add | update | delete
 */
@WebServlet("/api/doctors")
public class DoctorServlet extends BaseServlet {

    private final DoctorService service = new DoctorService();

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
            if ("add".equals(action)) {
                service.add(req.getParameter("name"), req.getParameter("specialization"),
                        req.getParameter("phone"), req.getParameter("email"), req.getParameter("fee"));
                success(resp, "Doctor added successfully");

            } else if ("update".equals(action)) {
                service.update(req.getParameter("id"), req.getParameter("name"),
                        req.getParameter("specialization"), req.getParameter("phone"),
                        req.getParameter("email"), req.getParameter("fee"));
                success(resp, "Doctor updated successfully");

            } else if ("delete".equals(action)) {
                service.delete(req.getParameter("id"));
                success(resp, "Doctor deleted successfully");

            } else {
                throw new IllegalArgumentException("Unknown action");
            }
        } catch (Exception e) {
            handleError(resp, e);
        }
    }
}
