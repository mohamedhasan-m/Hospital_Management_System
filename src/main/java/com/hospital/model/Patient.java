package com.hospital.model;

import com.hospital.util.JsonUtil;

/** A row of the patients table. */
public class Patient implements Jsonable {

    private int id;
    private String name;
    private int age;
    private String gender;
    private String phone;
    private String address;
    private String disease;

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getDisease() { return disease; }
    public void setDisease(String disease) { this.disease = disease; }

    @Override
    public String toJson() {
        return "{\"id\":" + id
                + ",\"name\":" + JsonUtil.str(name)
                + ",\"age\":" + age
                + ",\"gender\":" + JsonUtil.str(gender)
                + ",\"phone\":" + JsonUtil.str(phone)
                + ",\"address\":" + JsonUtil.str(address)
                + ",\"disease\":" + JsonUtil.str(disease) + "}";
    }
}
