package com.hospital.service;

import com.hospital.dao.PatientDAO;
import com.hospital.model.Patient;
import com.hospital.util.Validator;

import java.sql.SQLException;
import java.util.List;

/** Business rules for patients: validate the input, then call the DAO. */
public class PatientService {

    private final PatientDAO patientDAO = new PatientDAO();

    public List<Patient> getAll() throws SQLException {
        return patientDAO.findAll();
    }

    public void add(String name, String age, String gender, String phone, String address, String disease)
            throws SQLException {
        Patient p = buildPatient(null, name, age, gender, phone, address, disease);
        patientDAO.insert(p);
    }

    public void update(String id, String name, String age, String gender, String phone, String address, String disease)
            throws SQLException {
        Patient p = buildPatient(id, name, age, gender, phone, address, disease);
        if (!patientDAO.update(p)) {
            throw new IllegalArgumentException("Patient not found");
        }
    }

    public void delete(String id) throws SQLException {
        int patientId = Validator.integer(id, "Patient ID", 1, Integer.MAX_VALUE);
        if (!patientDAO.delete(patientId)) {
            throw new IllegalArgumentException("Patient not found");
        }
    }

    /** Checks every field and puts the clean values into a Patient object. */
    private Patient buildPatient(String id, String name, String age, String gender, String phone,
                                 String address, String disease) {
        Patient p = new Patient();
        if (id != null) {
            p.setId(Validator.integer(id, "Patient ID", 1, Integer.MAX_VALUE));
        }
        p.setName(Validator.required(name, "Name", 100));
        p.setAge(Validator.integer(age, "Age", 0, 150));
        p.setGender(Validator.oneOf(gender, "Gender", "Male", "Female", "Other"));
        p.setPhone(Validator.phone(phone));
        p.setAddress(Validator.optional(address, "Address", 255));
        p.setDisease(Validator.optional(disease, "Disease", 255));
        return p;
    }
}
