package repository.database;
import domain.Customer;
import service.CustomerRepository;

import javax.swing.*;
import java.sql.*;
import java.util.*;

public class DatabaseCustomerRepository implements CustomerRepository {
    private static String serverName = "jdbc:mysql://localhost:3306/java103";
    private static String username = "root";
    private static String password = "Fong_33621";
    private static long nextCustomerId = 0;

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
    public DatabaseCustomerRepository() {
        try{Class.forName("com.mysql.cj.jdbc.Driver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        try (Connection connection = DriverManager.getConnection(serverName, username, password)) {
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "customers", null);
            if (!tables.next()) {
                // Table does not exist
                String createTableSQL = "CREATE TABLE customers (" +
                        "id VARCHAR(20) PRIMARY KEY, " +
                        "name VARCHAR(100), " +
                        "phoneNumber VARCHAR(15))";
                try (Statement statement = connection.createStatement()) {
                    statement.execute(createTableSQL);
                    System.out.println("Table 'customers' created.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Customer CreateCustomer(String customerName, String phoneNumber) {
        if(customerName == null || phoneNumber == null) return null;
        try{Class.forName("com.mysql.cj.jdbc.Driver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        String customerId = "C" + ++nextCustomerId;
        String insertSQL = "INSERT INTO customers (id, name, phoneNumber) VALUE (?, ?, ?)";
        try(Connection connection = DriverManager.getConnection(serverName,username,password);
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)){
            preparedStatement.setString(1,customerId);
            preparedStatement.setString(2,customerName);
            preparedStatement.setString(3,phoneNumber);
            int rowInserted = preparedStatement.executeUpdate();
            if(rowInserted > 0 ){
                return new Customer(customerId,customerName,phoneNumber);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public Customer findCustomer(String customerId) {
        if(customerId == null) return null;
        try{Class.forName("com.mysql.cj.jdbc.Driver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        String selectSQL = "SELECT * FROM customers WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(serverName,username,password);
                PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, customerId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String name = resultSet.getString("name");
                    String phoneNumber = resultSet.getString("phoneNumber");
                    Customer fromDB = new Customer(id,name,phoneNumber);
                    return fromDB;
                } else {
                    System.out.println("Customer not found.");
                    return null;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        if(customer == null) return null;
        try{Class.forName("com.mysql.cj.jdbc.Driver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        String id = customer.getId();
        String Name = customer.getName();
        String phoneNumber = customer.getPhoneNumber();
        String updateSQL = "UPDATE customers SET name = ?, phone_number = ? WHERE id = ?";
        try(Connection connection = DriverManager.getConnection(serverName,username,password);
        PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)){
            preparedStatement.setString(1,Name);
            preparedStatement.setString(2,phoneNumber);
            preparedStatement.setString(3,id);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Collection<Customer> allCustomers() {
        try{Class.forName("com.mysql.cj.jdbc.Driver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        List<Customer> customers = new ArrayList<>();
        String selectSQL = "SELECT * FROM customers";

        try (Connection connection = DriverManager.getConnection(serverName, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String phoneNumber = resultSet.getString("phoneNumber");
                Customer customer = new Customer(id, name, phoneNumber);
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }
}

