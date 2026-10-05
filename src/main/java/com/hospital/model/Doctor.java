package com.hospital.model;

import com.hospital.util.JsonUtil;

/** A row of the doctors table. */
public class Doctor implements Jsonable {

    private int id;
    private String name;
    private String specialization;
    private String phone;
    private String email;
    private double consultationFee;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }

    @Override
    public String toJson() {
        return "{\"id\":" + id
                + ",\"name\":" + JsonUtil.str(name)
                + ",\"specialization\":" + JsonUtil.str(specialization)
                + ",\"phone\":" + JsonUtil.str(phone)
                + ",\"email\":" + JsonUtil.str(email)
                + ",\"consultationFee\":" + JsonUtil.money(consultationFee) + "}";
    }
}
