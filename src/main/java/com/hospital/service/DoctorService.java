package com.hospital.service;

import com.hospital.dao.DoctorDAO;
import com.hospital.model.Doctor;
import com.hospital.util.Validator;

import java.sql.SQLException;
import java.util.List;

/** Business rules for doctors: validate the input, then call the DAO. */
public class DoctorService {

    private final DoctorDAO doctorDAO = new DoctorDAO();

    public List<Doctor> getAll() throws SQLException {
        return doctorDAO.findAll();
    }

    public void add(String name, String specialization, String phone, String email, String fee)
            throws SQLException {
        Doctor d = buildDoctor(null, name, specialization, phone, email, fee);
        doctorDAO.insert(d);
    }

    public void update(String id, String name, String specialization, String phone, String email, String fee)
            throws SQLException {
        Doctor d = buildDoctor(id, name, specialization, phone, email, fee);
        if (!doctorDAO.update(d)) {
            throw new IllegalArgumentException("Doctor not found");
        }
    }

    public void delete(String id) throws SQLException {
        int doctorId = Validator.integer(id, "Doctor ID", 1, Integer.MAX_VALUE);
        if (!doctorDAO.delete(doctorId)) {
            throw new IllegalArgumentException("Doctor not found");
        }
    }

    private Doctor buildDoctor(String id, String name, String specialization, String phone, String email,
                               String fee) {
        Doctor d = new Doctor();
        if (id != null) {
            d.setId(Validator.integer(id, "Doctor ID", 1, Integer.MAX_VALUE));
        }
        d.setName(Validator.required(name, "Name", 100));
        d.setSpecialization(Validator.required(specialization, "Specialization", 100));
        d.setPhone(Validator.phone(phone));
        d.setEmail(Validator.email(email));
        d.setConsultationFee(Validator.amount(fee, "Consultation fee"));
        return d;
    }
}
