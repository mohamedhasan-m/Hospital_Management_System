# Hospital Management System

A small, beginner-friendly web application for managing patients, doctors, appointments and bills.
Built with plain **HTML / CSS / JavaScript** on the front end and **Java Servlets + JDBC + MySQL** on the back end.
No Spring Boot, React, Node.js, Bootstrap or Tailwind is used.

| Part | Technology |
|---|---|
| Frontend | HTML, CSS, JavaScript (`fetch`) |
| Backend | Java Servlets (Servlet 4.0, `javax.servlet`) |
| Database | MySQL |
| Connectivity | JDBC (`PreparedStatement` everywhere) |
| Server | Apache **Tomcat 9** |
| Build tool | Maven (packages a WAR file) |

## Features

- **Login** - admin login, `HttpSession`, logout, 30 minute session timeout
- **Dashboard** - total patients, doctors, appointments + next upcoming appointments
- **Patients** - add, view, edit, delete, search
- **Doctors** - add, view, edit, delete, search
- **Appointments** - book, view, cancel (a doctor cannot be booked twice at the same date/time)
- **Billing** - create a bill (consultation + medicine + room + other), total is calculated, view all bills

## Project structure

```
hospital-management/
├── pom.xml                         Maven build file
├── database.sql                    creates the 5 tables + sample data
├── README.md
└── src/main/
    ├── java/com/hospital/
    │   ├── controller/             Servlets + login filter   (receive requests, send JSON)
    │   │   ├── BaseServlet.java        helper methods shared by all servlets
    │   │   ├── AuthFilter.java         blocks pages/APIs when not logged in
    │   │   ├── LoginServlet.java       /api/login      (GET status, POST login)
    │   │   ├── LogoutServlet.java      /api/logout
    │   │   ├── DashboardServlet.java   /api/dashboard
    │   │   ├── PatientServlet.java     /api/patients
    │   │   ├── DoctorServlet.java      /api/doctors
    │   │   ├── AppointmentServlet.java /api/appointments
    │   │   └── BillServlet.java        /api/bills
    │   ├── service/                Business rules + validation
    │   ├── dao/                    ALL SQL / JDBC code lives here
    │   ├── model/                  Plain Java classes (Patient, Doctor, ...)
    │   └── util/                   DBUtil, Validator, JsonUtil, PasswordUtil
    ├── resources/db.properties     database URL, user, password
    └── webapp/
        ├── login.html, dashboard.html, patients.html,
        │   doctors.html, appointments.html, billing.html
        ├── css/style.css
        ├── js/                     common.js + one JS file per page
        └── WEB-INF/web.xml
```

## How to run

### 1. Install
- JDK 11 or newer
- Maven 3.6 or newer
- MySQL 5.7 or 8.x
- **Apache Tomcat 9.x** (important: *not* Tomcat 10 or 11, see Troubleshooting)

### 2. Create the database
```bash
mysql -u root -p < database.sql
```
This creates the database `hospital_db`, the 5 tables and some sample data.
(Running it again resets everything to the sample data.)

### 3. Set your MySQL password
Open `src/main/resources/db.properties` and change `db.user` / `db.password` to your MySQL login:
```properties
db.url=jdbc:mysql://localhost:3306/hospital_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=root
```

### 4. Build the WAR
```bash
mvn clean package
```
This creates `target/hospital.war`.

### 5. Deploy on Tomcat
Copy `target/hospital.war` into `<TOMCAT_HOME>/webapps/` and start Tomcat
(`bin/startup.bat` on Windows, `bin/startup.sh` on Linux/Mac).

### 6. Open the app
<http://localhost:8080/hospital/>

**Login:** username `admin` , password `admin123`

## How it works

### Layers (simple MVC)

```
Browser (HTML + JavaScript)          <- View
        |  fetch()  (JSON)
        v
PatientServlet                       <- Controller  (reads request, sends JSON answer)
        |
        v
PatientService                       <- validation + business rules
        |
        v
PatientDAO                           <- only place with SQL (PreparedStatement)
        |  JDBC
        v
MySQL
```

`model/` classes (Patient, Doctor, ...) carry data between the layers.
Every other feature (doctors, appointments, bills) follows exactly the same path.

### Example: adding a patient
1. User fills the popup form in `patients.html` and clicks **Save**.
2. `patients.js` sends `POST api/patients` with `action=add&name=...&age=...`.
3. `AuthFilter` checks that the user is logged in, then `PatientServlet.doPost` runs.
4. The servlet calls `PatientService.add(...)`, which validates every field
   (name required, age 0-150, phone exactly 10 digits, ...). A mistake throws `IllegalArgumentException`.
5. `PatientDAO.insert(...)` runs `INSERT INTO patients ... VALUES (?, ?, ?, ?, ?, ?)` with a `PreparedStatement`.
6. The servlet answers `{"success":true,"message":"Patient added successfully"}`
   (or HTTP 400 with the validation message) and the page shows it as a small notification and reloads the table.

### Login and sessions
- `LoginServlet` (POST) hashes the typed password with SHA-256 and looks for a matching row in `users`.
- On success it stores the user in the `HttpSession` (`session.setAttribute("user", user)`).
- `AuthFilter` runs before **every** request. Only `login.html`, `css/`, `js/` and `/api/login` are public.
  Without a session, pages redirect to `login.html` and `/api/...` calls get HTTP 401.
- Logout calls `session.invalidate()`.

### Frontend <-> backend contract
- **GET** `/api/<thing>` returns a JSON array.
- **POST** `/api/<thing>` with form fields and an `action` field returns `{"success": true/false, "message": "..."}`.

| URL | GET | POST `action` |
|---|---|---|
| `/api/login` | is someone logged in? | (no action) username + password |
| `/api/logout` | - | (no action) |
| `/api/dashboard` | the three totals | - |
| `/api/patients` | all patients | `add`, `update`, `delete` |
| `/api/doctors` | all doctors | `add`, `update`, `delete` |
| `/api/appointments` | all appointments | `book`, `cancel` |
| `/api/bills` | all bills | `create` |

## Database tables

| Table | Main columns |
|---|---|
| `users` | id, username, password (SHA-256 hash), full_name, role |
| `patients` | id, name, age, gender, phone, address, disease, created_at |
| `doctors` | id, name, specialization, phone, email, consultation_fee |
| `appointments` | id, patient_id (FK), doctor_id (FK), appointment_date, appointment_time, reason, status |
| `bills` | id, patient_id (FK), consultation_fee, medicine_charges, room_charges, other_charges, total_amount, bill_date |

Relationships: one patient has many appointments and many bills; one doctor has many appointments.
Foreign keys use `ON DELETE CASCADE`, so deleting a patient or doctor also deletes their appointments (and the patient's bills).
Cancelling an appointment only changes its `status` to `Cancelled`; the row stays.

## Viva questions and short answers

**What is JDBC and what are the steps?**
Java's standard API for talking to databases. Load driver -> get `Connection` -> create `PreparedStatement` -> execute -> read `ResultSet` -> close (we use try-with-resources). See `DBUtil` and any DAO.

**Why `PreparedStatement` and not `Statement`?**
Values are sent separately from the SQL (`?` placeholders), so user input can never change the query. This prevents SQL injection. It is also precompiled.

**What is a DAO? Why a Service layer?**
DAO (Data Access Object) holds all SQL for one table. The Service holds rules and validation, so servlets stay short and DAOs stay pure database code.

**Where is MVC here?**
Model = `model` classes (+ `dao`/`service`), View = HTML/CSS/JS pages, Controller = servlets.

**How does login/session work?**
Password hash is compared in the database; on success the user object is saved in `HttpSession`. The browser keeps only a session cookie (`JSESSIONID`). `AuthFilter` checks the session on every request.

**What is a Servlet Filter?**
Code that runs before a request reaches a page or servlet. `AuthFilter` is our security guard.

**Why are passwords hashed?**
So the database never contains real passwords. We use SHA-256 for simplicity (a real system should use BCrypt with a salt).

**How is the bill total calculated?**
In `BillService.create`: consultation + medicine + room + other, rounded to 2 decimals. The page shows a live total for convenience, but the server always recalculates it.

**How is double booking prevented?**
`AppointmentDAO.isSlotTaken` counts booked appointments of that doctor at that date and time before inserting.

**Why does the project use `fetch` and JSON?**
The HTML pages are static; JavaScript asks the servlets for data and builds the tables. This keeps the servlets free of HTML.

## Troubleshooting

| Problem | Fix |
|---|---|
| Everything under `/api/...` gives 404 on Tomcat 10/11 | Use **Tomcat 9**. Tomcat 10+ uses `jakarta.servlet`, this project uses `javax.servlet`. |
| Page not found | The URL must contain the WAR name: `http://localhost:8080/hospital/` |
| "Server or database error" toast | Look at `<TOMCAT_HOME>/logs/catalina.out` (or the Tomcat console) for the real exception. |
| `Access denied for user` | Wrong `db.user` / `db.password` in `db.properties`. Rebuild the WAR after changing it. |
| `Unknown database 'hospital_db'` or empty tables | Run `database.sql` (step 2). |
| `admin` / `admin123` is rejected | `database.sql` was not run, or the `users` table was changed. |
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | The WAR was not built with Maven, so `WEB-INF/lib` is missing the MySQL driver. Run `mvn clean package` again. |
| Port 8080 already in use | Change the port in `<TOMCAT_HOME>/conf/server.xml` or stop the other program. |

## Ideas for improvement
Use BCrypt for passwords, add roles (doctor / receptionist), pagination, a connection pool (e.g. Tomcat JNDI DataSource), print/PDF bills, CSRF tokens.
