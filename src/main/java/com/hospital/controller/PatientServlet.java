package com.hospital.controller;

import com.hospital.service.PatientService;
import com.hospital.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * URL: /api/patients
 *   GET  -> list of all patients
 *   POST -> action = add | update | delete
 *
 * Flow:  PatientServlet -> PatientService -> PatientDAO -> JDBC -> MySQL
 */
@WebServlet("/api/patients")
public class PatientServlet extends BaseServlet {

    private final PatientService service = new PatientService();

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
                service.add(req.getParameter("name"), req.getParameter("age"), req.getParameter("gender"),
                        req.getParameter("phone"), req.getParameter("address"), req.getParameter("disease"));
                success(resp, "Patient added successfully");

            } else if ("update".equals(action)) {
                service.update(req.getParameter("id"), req.getParameter("name"), req.getParameter("age"),
                        req.getParameter("gender"), req.getParameter("phone"), req.getParameter("address"),
                        req.getParameter("disease"));
                success(resp, "Patient updated successfully");

            } else if ("delete".equals(action)) {
                service.delete(req.getParameter("id"));
                success(resp, "Patient deleted successfully");

            } else {
                throw new IllegalArgumentException("Unknown action");
            }
        } catch (Exception e) {
            handleError(resp, e);
        }
    }
}
