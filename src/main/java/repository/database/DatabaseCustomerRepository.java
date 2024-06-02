package repository.database;
import java.sql.*;
public class DatabaseCustomerRepository {
    private static String serverName = "jdbc:mysql://localhost:3306/java103";
    private static String username = "root";
    private static String password = "Fong_33621";

    public static void main(String[] args) throws SQLException {
        Connection connection = null;
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish the connection
            connection = DriverManager.getConnection(serverName, username, password);
            System.out.println("Connected to the database!");

            // Create a table
//            createTable(connection);

            // Insert a customer
//            insertCustomer(connection, "C1", "John Doe", "1234567890");

            // Find a customer
//            findCustomer(connection, "C1");

            // Update a customer
//            updateCustomer(connection, "C1", "John Doe Updated", "0987654321");

            // List all customers
//            listAllCustomers(connection);

        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            // Close the connection
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private static void createTable(Connection connection) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS customers (" +
                "id VARCHAR(20) PRIMARY KEY, " +
                "name VARCHAR(100), " +
                "phone_number VARCHAR(15))";

        try (Statement statement = connection.createStatement()) {
            statement.execute(createTableSQL);
            System.out.println("Table 'customers' created.");
        }
    }
    private static void insertCustomer(Connection connection, String id, String name, String phoneNumber) throws SQLException {
        String insertSQL = "INSERT INTO customers (id, name, phone_number) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)) {
            preparedStatement.setString(1, id);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, phoneNumber);
            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new customer was inserted successfully!");
            }
        }
    }

    private static void findCustomer(Connection connection, String id) throws SQLException {
        String selectSQL = "SELECT * FROM customers WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, id);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println("Customer ID: " + resultSet.getString("id"));
                    System.out.println("Customer Name: " + resultSet.getString("name"));
                    System.out.println("Customer Phone Number: " + resultSet.getString("phone_number"));
                } else {
                    System.out.println("Customer not found.");
                }
            }
        }
    }

    private static void updateCustomer(Connection connection, String id, String newName, String newPhoneNumber) throws SQLException {
        String updateSQL = "UPDATE customers SET name = ?, phone_number = ? WHERE id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)) {
            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, newPhoneNumber);
            preparedStatement.setString(3, id);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("An existing customer was updated successfully!");
            }
        }
    }

    private static void listAllCustomers(Connection connection) throws SQLException {
        String selectAllSQL = "SELECT * FROM customers";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectAllSQL)) {

            while (resultSet.next()) {
                System.out.println("Customer ID: " + resultSet.getString("id"));
                System.out.println("Customer Name: " + resultSet.getString("name"));
                System.out.println("Customer Phone Number: " + resultSet.getString("phone_number"));
                System.out.println("----------------------------");
            }
        }
    }
}

