import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "Hout@8850"; // Add your password here
    private static final String DATABASE_NAME = "student_management_system"; // Replace with your database name if you have one

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL + DATABASE_NAME, USERNAME, PASSWORD);
            System.out.println("Connected to the database successfully.");
        } catch (SQLException e) {
            System.err.println("Connection failed.");
            e.printStackTrace();
        }
        return connection;
    }

    public static void main(String[] args) {
        getConnection();
    }
}
