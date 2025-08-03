package HMS;

import javax.mail.*;
import javax.mail.internet.*;
import java.sql.*;
import java.util.*;

public class Auth {

    public static void signup(Scanner scanner) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.print("Enter username: ");
            String username = scanner.nextLine();

            System.out.print("Enter email: ");
            String email = scanner.nextLine();

            // Check if email already exists
            String checkQuery = "SELECT * FROM users WHERE email = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, email);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                System.out.println("This email is already registered. Please login instead.");
                return;
            }

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            String otp = generateOTP();
            if (!sendEmail(email, otp)) {
                System.out.println("Failed to send OTP.");
                return;
            }

            System.out.print("Enter OTP sent to your email: ");
            String enteredOtp = scanner.nextLine();

            if (!otp.equals(enteredOtp)) {
                System.out.println("OTP mismatch. Signup failed.");
                return;
            }

            String sql = "INSERT INTO users (username, email, password, is_verified) VALUES (?, ?, ?, true)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.executeUpdate();

            System.out.println("Signup successful.");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }


    public static boolean login(Scanner scanner) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.print("Enter email: ");
            String email = scanner.nextLine();

            System.out.print("Enter password: ");
            String password = scanner.nextLine();

            String sql = "SELECT * FROM users WHERE email = ? AND password = ? AND is_verified = true";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                System.out.println("Login successful. Welcome " + rs.getString("username"));
                return true;
            } else {
                System.out.println("Invalid email or password.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
            return false;
        }
    }

    private static String generateOTP() {
        Random rand = new Random();
        int otp = 1000 + rand.nextInt(9000);
        return String.valueOf(otp);
    }

    private static boolean sendEmail(String to, String otp) {
        final String from = "your email id";            // change this
        final String appPassword = "your 16 app passowrd";       // change this

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, appPassword);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject("Hospital Management System - OTP Verification");

            message.setText(
                    "Dear User,\n\n" +
                            "Thank you for signing up for the Hospital Management System (HMS).\n" +
                            "Your One-Time Password (OTP) for verification is: " + otp + "\n\n" +
                            "Please enter this OTP to complete your registration.\n" +
                            "Note: This OTP is valid for 10 minutes.\n\n" +
                            "If you did not initiate this request, please ignore this email.\n\n" +
                            "Regards,\n" +
                            "HMS Support Team"
            );

            Transport.send(message);
            System.out.println("OTP sent to " + to);
            return true;
        } catch (MessagingException e) {
            System.out.println("Email error: " + e.getMessage());
            return false;
        }
    }
}
