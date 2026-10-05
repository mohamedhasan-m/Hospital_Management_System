package com.hospital.model;

import com.hospital.util.JsonUtil;

/** A row of the bills table (patientName comes from a JOIN with the patients table). */
public class Bill implements Jsonable {

    private int id;
    private int patientId;
    private String patientName;
    private double consultationFee;
    private double medicineCharges;
    private double roomCharges;
    private double otherCharges;
    private double totalAmount;
    private String billDate;   // yyyy-MM-dd

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPatientId() { return patientId; }
    public void setPatientId(int patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(double consultationFee) { this.consultationFee = consultationFee; }

    public double getMedicineCharges() { return medicineCharges; }
    public void setMedicineCharges(double medicineCharges) { this.medicineCharges = medicineCharges; }

    public double getRoomCharges() { return roomCharges; }
    public void setRoomCharges(double roomCharges) { this.roomCharges = roomCharges; }

    public double getOtherCharges() { return otherCharges; }
    public void setOtherCharges(double otherCharges) { this.otherCharges = otherCharges; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getBillDate() { return billDate; }
    public void setBillDate(String billDate) { this.billDate = billDate; }

    @Override
    public String toJson() {
        return "{\"id\":" + id
                + ",\"patientId\":" + patientId
                + ",\"patientName\":" + JsonUtil.str(patientName)
                + ",\"consultationFee\":" + JsonUtil.money(consultationFee)
                + ",\"medicineCharges\":" + JsonUtil.money(medicineCharges)
                + ",\"roomCharges\":" + JsonUtil.money(roomCharges)
                + ",\"otherCharges\":" + JsonUtil.money(otherCharges)
                + ",\"totalAmount\":" + JsonUtil.money(totalAmount)
                + ",\"billDate\":" + JsonUtil.str(billDate) + "}";
    }
}
