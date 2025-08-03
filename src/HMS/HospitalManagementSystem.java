package HMS;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url = "jdbc:mysql://localhost:3306/hospital";
    private static final String dbUsername = "root";
    private static final String dbPassword = "812666";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        label:
        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("\n=== Welcome to Hospital Management System ===");
            System.out.println("1. Signup");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose option: ");
            String input = scanner.nextLine();

            switch (input) {
                case "1" :
                    Auth.signup(scanner);
                    break;
                case "2":
                    if (Auth.login(scanner)) {
                        System.out.println("Access granted. HMS Features Loading...");
                        try (Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword)) {
                            runHMSMenu(scanner, connection);
                        } catch (SQLException e) {
                            System.out.println(e.getMessage());
                        }
                    }
                    break;
                case "3":
                    System.out.println("Exiting system. Goodbye!");
                    break label;
                default:
                    System.out.println("Invalid choice. Try again.");
                    break;
            }
        }
    }
    private static void runHMSMenu(Scanner scanner, Connection connection) {
        Pateint pateint = new Pateint(connection, scanner);
        Doctors doctors = new Doctors(connection);

        while (true) {
            System.out.println("\n--- HMS Main Menu ---");
            System.out.println("1. Add Patient");
            System.out.println("2. View Patient");
            System.out.println("3. View Doctor");
            System.out.println("4. Book Appointment");
            System.out.println("5. View Appointments by Patient ID and Name");
            System.out.println("6. Logout");

            System.out.print("Enter your choice: ");
            int option = scanner.nextInt();
            scanner.nextLine(); // consume leftover newline

            switch (option) {
                case 1:
                    pateint.add_patient();
                    break;
                case 2:
                    pateint.view_patient();
                    break;
                case 3:
                    doctors.view_doctor();
                    break;
                case 4:
                    bookAppointment(pateint, doctors, connection, scanner);
                    break;
                case 5:
                    viewAppointmentsByPatientIdAndName(connection, scanner);
                    break;
                case 6:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    public static void bookAppointment(Pateint pateint, Doctors doctors, Connection connection, Scanner scanner) {
        System.out.print("Enter Patient ID: ");
        int patient_id = scanner.nextInt();
        System.out.print("Enter Doctor ID: ");
        int doctorId = scanner.nextInt();
        System.out.print("Enter Appointment Date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();

        if (pateint.patientbyid(patient_id) && doctors.doctorbyid(doctorId)) {
            if (checkDoctorAvailability(doctorId, appointmentDate, connection)) {
                String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date) VALUES (?, ?, ?)";
                try {
                    PreparedStatement ps = connection.prepareStatement(sql);
                    ps.setInt(1, patient_id);
                    ps.setInt(2, doctorId);
                    ps.setString(3, appointmentDate);
                    int result = ps.executeUpdate();
                    System.out.println(result > 0 ? "Appointment Booked." : "Failed to book appointment.");
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                System.out.println("Doctor not available on that date.");
            }
        } else {
            System.out.println("Invalid doctor or patient ID.");
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String date, Connection connection) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date = ?";
        try {
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, doctorId);
            ps.setString(2, date);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    public static void viewAppointmentsByPatientIdAndName(Connection connection, Scanner scanner) {
        System.out.print("Enter Patient ID: ");
        int patientId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter Patient Name: ");
        String patientName = scanner.nextLine();

        String validateQuery = "SELECT name FROM patients WHERE id = ?";
        try {
            PreparedStatement validateStmt = connection.prepareStatement(validateQuery);
            validateStmt.setInt(1, patientId);
            ResultSet validateRs = validateStmt.executeQuery();

            if (validateRs.next()) {
                String actualName = validateRs.getString("name");
                if (!actualName.equalsIgnoreCase(patientName)) {
                    System.out.println("Error: Patient name does not match the ID.");
                    return;
                }
            } else {
                System.out.println("Error: Patient ID not found.");
                return;
            }

            String query = """
                SELECT p.name AS patient_name, d.name AS doctor_name, d.specialisation, d.RoomNumber, a.appointment_date
                FROM appointments a
                JOIN patients p ON a.patient_id = p.id
                JOIN doctors d ON a.doctor_id = d.id
                WHERE a.patient_id = ?
                """;

            PreparedStatement pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            System.out.printf("\n%-20s %-20s %-20s %-15s %-15s\n",
                    "Patient Name", "Doctor Name", "Specialization", "Room Number", "Appointment Date");
            System.out.println("-----------------------------------------------------------------------------------------------");

            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-20s %-20s %-20s %-15d %-15s\n",
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getString("specialisation"),
                        rs.getInt("RoomNumber"),
                        rs.getDate("appointment_date").toString());
            }

            if (!found) {
                System.out.println("No appointments found.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
