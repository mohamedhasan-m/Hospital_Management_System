package com.hospital.model;

import com.hospital.util.JsonUtil;

/**
 * A row of the appointments table.
 * patientName, doctorName and specialization are not columns of this table:
 * they are filled from a JOIN so the page can show names instead of ID numbers.
 */
public class Appointment implements Jsonable {

    private int id;
    private int patientId;
    private int doctorId;
    private String patientName;
    private String doctorName;
    private String specialization;
    private String appointmentDate;   // yyyy-MM-dd
    private String appointmentTime;   // HH:mm
    private String reason;
    private String status;            // Booked or Cancelled

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(String appointmentDate) { this.appointmentDate = appointmentDate; }

    public String getAppointmentTime() { return appointmentTime; }
    public void setAppointmentTime(String appointmentTime) { this.appointmentTime = appointmentTime; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toJson() {
        return "{\"id\":" + id
                + ",\"patientId\":" + patientId
                + ",\"doctorId\":" + doctorId
                + ",\"patientName\":" + JsonUtil.str(patientName)
                + ",\"doctorName\":" + JsonUtil.str(doctorName)
                + ",\"specialization\":" + JsonUtil.str(specialization)
                + ",\"appointmentDate\":" + JsonUtil.str(appointmentDate)
                + ",\"appointmentTime\":" + JsonUtil.str(appointmentTime)
                + ",\"reason\":" + JsonUtil.str(reason)
                + ",\"status\":" + JsonUtil.str(status) + "}";
    }
}
