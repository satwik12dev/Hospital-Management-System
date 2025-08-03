# 🏥 Hospital Management System (Java Console App)

A simple and functional **Hospital Management System** built using **Java** with **MySQL** backend. Includes secure **OTP-based signup/login**, patient and appointment management, and doctor viewing features.

---

## 🚀 Features

- ✅ **User Signup & Login** with Email OTP verification (via `javax.mail`)
- 🧾 Add & View **Patient Details**
- 🩺 View **Doctor List**
- 📅 Book and View **Appointments**
- 🔐 Basic security with OTP and email validation
- 🗑️ Admin feature to delete a user from the database
- 📋 Console-based interface with intuitive menus

---

## 📁 Project Structure

### IntelliJ + Maven (Recommended)

```bash
HospitalManagementSystem/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── hms/
│                   ├── Auth.java
│                   ├── Appointment.java
│                   ├── Patient.java
│                   ├── Doctor.java
│                   ├── DatabaseConnection.java
│                   └── HospitalManagementSystem.java
├── resources/
│   └── application.properties
├── pom.xml
└── README.md
```

---

## 🛠️ Technologies Used

- Java 8+
- MySQL (via JDBC)
- Maven (for dependency management)
- `javax.mail` / `jakarta.mail` (for sending OTP emails)
- IntelliJ IDEA
- Console-based UI

---

## 📦 Maven Dependencies (`pom.xml`)

```xml
<dependencies>
    <dependency>
        <groupId>com.sun.mail</groupId>
        <artifactId>javax.mail</artifactId>
        <version>1.6.2</version>
    </dependency>
    <dependency>
        <groupId>com.mysql</groupId>
        <artifactId>mysql-connector-j</artifactId>
        <version>8.0.33</version>
    </dependency>
</dependencies>
```

> Add to `pom.xml` under `<dependencies>...</dependencies>`

---

## ⚙️ Setup Instructions

### 1. 📥 Clone the Repo

```bash
git clone https://github.com/yourusername/hospital-management-system-java.git
cd hospital-management-system-java
```

### 2. 🧷 Configure Database

- Create a MySQL database named `hms`.
- Run the following schema:

```sql
CREATE TABLE users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100),
  email VARCHAR(100) UNIQUE,
  password VARCHAR(255)
);

CREATE TABLE patients (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100),
  age INT,
  gender VARCHAR(10),
  disease VARCHAR(100)
);

CREATE TABLE doctors (
  id INT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100),
  specialty VARCHAR(100)
);

CREATE TABLE appointments (
  id INT AUTO_INCREMENT PRIMARY KEY,
  patient_id INT,
  doctor_id INT,
  date DATE,
  FOREIGN KEY (patient_id) REFERENCES patients(id),
  FOREIGN KEY (doctor_id) REFERENCES doctors(id)
);
```

### 3. ✉️ Configure Email for OTP

- Use Gmail App Password (Enable 2FA).
- Set up credentials in `Auth.java`:

```java
final String senderEmail = "your_email@gmail.com";
final String senderPassword = "your_app_password";
```

> Or load from `application.properties` file securely.

---

## 🧪 How to Run (IntelliJ)

1. Open the project in IntelliJ IDEA.
2. Make sure `pom.xml` is loaded and Maven dependencies are downloaded.
3. Right-click `HospitalManagementSystem.java` → Run.
4. Use the console interface to sign up, log in, and manage hospital data.

---

---

## 🚧 Future Improvements

- Web-based UI with Spring Boot or JSP
- Role-based login: Patient / Doctor / Admin
- Email templates with HTML support
- Password encryption (e.g., BCrypt)
- Docker container support
- Cloud deployment (Firebase, Render, EC2, Railway)

---

## 🔗 Useful Links

- [Javax Mail ](https://repo1.maven.org/maven2/com/sun/mail/javax.mail/1.6.2/javax.mail-1.6.2.jar)
- [Javax Activation](https://github.com/eclipse-ee4j/jaf/releases)
- [MySQL Connector/J](https://dev.mysql.com/downloads/connector/j/)
- [Gmail App Password Setup](https://support.google.com/accounts/answer/185833)
- [IntelliJ IDEA Download](https://www.jetbrains.com/idea/download/)
- [Maven Download](https://maven.apache.org/download.cgi)
- [Spring Boot Migration Guide](https://spring.io/guides)

---

## 🙋‍♂️ Author

**Satwik Saxena**  
[LinkedIn](https://www.linkedin.com/in/satwik-12-dev)
---

## 📄 License

MIT License – feel free to use and modify for learning or development.
