package repository.database.databaseonline;

import domain.Customer;
import service.CustomerRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DatabaseCustomerRepository implements CustomerRepository {
    private static final String hostName = "javadatabase.database.windows.net";
    private static final String dbName = "javaproject";
    private static final String user = "Fong";
    private static final String password = "Darkkiller_204";
    private static final String url = String.format("jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;", hostName, dbName, user, password);

    private static long nextCustomerId;

    public DatabaseCustomerRepository() {
        try{Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        try (Connection connection = DriverManager.getConnection(url)) {
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "customers", null);
            if (!tables.next()) {
                // Table does not exist
                String createTableSQL = "CREATE TABLE customers (" +
                        "id VARCHAR(20) PRIMARY KEY, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "phonenumber VARCHAR(15) NOT NULL)";
                try (Statement statement = connection.createStatement()) {
                    statement.execute(createTableSQL);
                    System.out.println("Table 'customers' created.");
                    nextCustomerId = 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Customer createCustomer(String customerName, String phoneNumber) {
        if(customerName == null || phoneNumber == null) return null;
        try{Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        nextCustomerId = getCustomerCount();
        String customerId = "C" + ++nextCustomerId;
        String insertSQL = "INSERT INTO customers (id, name, phonenumber) VALUES (?, ?, ?)";
        try(Connection connection = DriverManager.getConnection(url);
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
        try{Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        String selectSQL = "SELECT * FROM customers WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, customerId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String name = resultSet.getString("name");
                    String phoneNumber = resultSet.getString("phonenumber");
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
        try{
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        String id = customer.getId();
        String Name = customer.getName();
        String phoneNumber = customer.getPhoneNumber();
        String updateSQL = "UPDATE customers SET name = ?, phonenumber = ? WHERE id = ?";
        try(Connection connection = DriverManager.getConnection(url);
            PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)){
            preparedStatement.setString(1,Name);
            preparedStatement.setString(2,phoneNumber);
            preparedStatement.setString(3,id);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("An existing customer was updated successfully!");
                return customer;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Collection<Customer> allCustomers() {
        try{Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        List<Customer> customers = new ArrayList<>();
        String selectSQL = "SELECT * FROM customers";

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String name = resultSet.getString("name");
                String phoneNumber = resultSet.getString("phonenumber");
                Customer customer = new Customer(id, name, phoneNumber);
                customers.add(customer);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }
    public int getCustomerCount() {
        int count = 0;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String countSQL = "SELECT COUNT(*) AS count FROM customers";
        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(countSQL)) {
            if (resultSet.next()) {
                count = resultSet.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}

