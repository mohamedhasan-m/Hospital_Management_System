package com.hospital.controller;

import com.hospital.model.Bill;
import com.hospital.service.BillService;
import com.hospital.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * URL: /api/bills
 *   GET  -> list of all bills
 *   POST -> action = create
 */
@WebServlet("/api/bills")
public class BillServlet extends BaseServlet {

    private final BillService service = new BillService();

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
            if ("create".equals(action)) {
                Bill bill = service.create(req.getParameter("patientId"), req.getParameter("consultationFee"),
                        req.getParameter("medicineCharges"), req.getParameter("roomCharges"),
                        req.getParameter("otherCharges"));
                success(resp, "Bill #" + bill.getId() + " created. Total amount: " + JsonUtil.money(bill.getTotalAmount()));

            } else {
                throw new IllegalArgumentException("Unknown action");
            }
        } catch (Exception e) {
            handleError(resp, e);
        }
    }
}
