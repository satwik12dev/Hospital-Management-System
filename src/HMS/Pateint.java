package HMS;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Pateint {
    private Connection connection;
    private Scanner scanner;

    public Pateint(Connection connection, Scanner scanner){
        this.connection=connection;
        this.scanner=scanner;
    }

    public void add_patient(){
        System.out.print("Enter Patient name:");
        String name = scanner.next();
        System.out.print("Enter Patient Age:");
        int age = scanner.nextInt();
        System.out.print("Enter Patient Gender:");
        String gender = scanner.next();

        try{
            String Query = "INSERT INTO patients(name,age,gender) VALUES(?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(Query);
            preparedStatement.setString(1,name);
            preparedStatement.setInt(2,age);
            preparedStatement.setString(3,gender);

            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows>0){
                System.out.println("Patient Added Succesfully!!");
            }else{
                System.out.println("Failed To Add Patient!!");
            }
        }catch(SQLException e){
            e.printStackTrace();
        }
    }

    public void view_patient(){
        String Query ="SELECT * FROM patients";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(Query);
            ResultSet rs = preparedStatement.executeQuery();
            System.out.println("Patients:");
            System.out.println("+------------+--------------------+----------+-------------+");
            System.out.println("| Patient_Id | Name               | Age      | Gender      |");
            System.out.println("+------------+--------------------+----------+-------------+");
            while (rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String gender = rs.getString("gender");
                System.out.printf( "|%-12s|%-20s|%-10s|%-12s|\n",id,name,age,gender);
                System.out.println("+------------+--------------------+----------+-------------+");
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean patientbyid(int id) {
        String query = "SELECT * FROM patients WHERE id=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                return true;
            }else{
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
