package com.hospital.service;

import com.hospital.dao.BillDAO;
import com.hospital.dao.PatientDAO;
import com.hospital.model.Bill;
import com.hospital.util.Validator;

import java.sql.SQLException;
import java.util.List;

/** Business rules for billing. The total is always calculated here on the server. */
public class BillService {

    private final BillDAO billDAO = new BillDAO();
    private final PatientDAO patientDAO = new PatientDAO();

    public List<Bill> getAll() throws SQLException {
        return billDAO.findAll();
    }

    /** Creates a bill and returns it (with the new id and the calculated total). */
    public Bill create(String patientId, String consultationFee, String medicineCharges,
                       String roomCharges, String otherCharges) throws SQLException {

        int pid = Validator.integer(patientId, "Patient", 1, Integer.MAX_VALUE);
        if (patientDAO.findById(pid) == null) {
            throw new IllegalArgumentException("Patient not found");
        }

        Bill bill = new Bill();
        bill.setPatientId(pid);
        bill.setConsultationFee(Validator.amount(consultationFee, "Consultation fee"));
        bill.setMedicineCharges(Validator.amount(medicineCharges, "Medicine charges"));
        bill.setRoomCharges(Validator.amount(roomCharges, "Room charges"));
        bill.setOtherCharges(Validator.amount(otherCharges, "Other charges"));

        // total = sum of all charges (rounded to 2 decimals)
        double total = bill.getConsultationFee() + bill.getMedicineCharges()
                + bill.getRoomCharges() + bill.getOtherCharges();
        total = Math.round(total * 100) / 100.0;

        if (total <= 0) {
            throw new IllegalArgumentException("Total amount must be greater than 0");
        }
        bill.setTotalAmount(total);

        bill.setId(billDAO.insert(bill));
        return bill;
    }
}
