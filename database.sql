-- =====================================================
--  Hospital Management System - MySQL database script
--  Run:  mysql -u root -p < database.sql
--  WARNING: running it again recreates the tables and
--           resets all data to the sample data below.
-- =====================================================

CREATE DATABASE IF NOT EXISTS hospital_db;
USE hospital_db;

-- Drop children first, then parents (because of foreign keys)
DROP TABLE IF EXISTS bills;
DROP TABLE IF EXISTS appointments;
DROP TABLE IF EXISTS patients;
DROP TABLE IF EXISTS doctors;
DROP TABLE IF EXISTS users;

-- 1. users : admin accounts that can log in
CREATE TABLE users (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(50)  NOT NULL UNIQUE,
    password  VARCHAR(64)  NOT NULL,            -- SHA-256 hash, never plain text
    full_name VARCHAR(100) NOT NULL,
    role      VARCHAR(20)  NOT NULL DEFAULT 'ADMIN'
);

-- 2. patients
CREATE TABLE patients (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    age        INT          NOT NULL,
    gender     VARCHAR(10)  NOT NULL,
    phone      VARCHAR(15)  NOT NULL,
    address    VARCHAR(255),
    disease    VARCHAR(255),
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 3. doctors
CREATE TABLE doctors (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    name             VARCHAR(100)  NOT NULL,
    specialization   VARCHAR(100)  NOT NULL,
    phone            VARCHAR(15)   NOT NULL,
    email            VARCHAR(100),
    consultation_fee DECIMAL(10,2) NOT NULL DEFAULT 0
);

-- 4. appointments : links one patient with one doctor
CREATE TABLE appointments (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    patient_id       INT         NOT NULL,
    doctor_id        INT         NOT NULL,
    appointment_date DATE        NOT NULL,
    appointment_time TIME        NOT NULL,
    reason           VARCHAR(255),
    status           VARCHAR(20) NOT NULL DEFAULT 'Booked',   -- Booked / Cancelled
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id)  REFERENCES doctors(id)  ON DELETE CASCADE
);

-- 5. bills : one bill belongs to one patient
CREATE TABLE bills (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    patient_id       INT           NOT NULL,
    consultation_fee DECIMAL(10,2) NOT NULL DEFAULT 0,
    medicine_charges DECIMAL(10,2) NOT NULL DEFAULT 0,
    room_charges     DECIMAL(10,2) NOT NULL DEFAULT 0,
    other_charges    DECIMAL(10,2) NOT NULL DEFAULT 0,
    total_amount     DECIMAL(10,2) NOT NULL,
    bill_date        TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES patients(id) ON DELETE CASCADE
);

-- =====================================================
--  Sample data
-- =====================================================

-- Login:  username = admin   password = admin123
INSERT INTO users (username, password, full_name, role)
VALUES ('admin', SHA2('admin123', 256), 'Administrator', 'ADMIN');

INSERT INTO patients (name, age, gender, phone, address, disease) VALUES
('Ravi Kumar',   45, 'Male',   '9876543210', '12 Gandhi Road, Chennai',   'Chest pain'),
('Priya Sharma', 29, 'Female', '9876501234', '45 MG Road, Bengaluru',     'Skin allergy'),
('Arun Prakash',  8, 'Male',   '9812345678', '7 Lake View, Coimbatore',   'Fever and cold'),
('Meena Devi',   52, 'Female', '9898989898', '21 Temple Street, Madurai', 'Knee pain'),
('Suresh Babu',  36, 'Male',   '9790012345', '3 Park Avenue, Salem',      'Migraine');

INSERT INTO doctors (name, specialization, phone, email, consultation_fee) VALUES
('Dr. Anita Rao',   'Cardiologist',  '9000011111', 'anita.rao@hospital.com',   600.00),
('Dr. Vikram Singh','Paediatrician', '9000022222', 'vikram.singh@hospital.com',400.00),
('Dr. Neha Gupta',  'Dermatologist', '9000033333', 'neha.gupta@hospital.com',  500.00),
('Dr. Rahul Verma', 'Orthopedic',    '9000044444', 'rahul.verma@hospital.com', 700.00);

-- Dates are relative to today so the sample data always looks current
INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, reason, status) VALUES
(1, 1, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '10:00:00', 'Chest pain follow-up', 'Booked'),
(2, 3, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '11:30:00', 'Skin allergy check',   'Booked'),
(3, 2, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '09:30:00', 'Fever and cold',       'Booked'),
(4, 4, CURDATE(),                           '16:00:00', 'Knee pain',            'Cancelled');

INSERT INTO bills (patient_id, consultation_fee, medicine_charges, room_charges, other_charges, total_amount) VALUES
(1, 600.00, 250.00,    0.00,   0.00,  850.00),
(4, 700.00, 450.00, 3000.00, 100.00, 4250.00);
