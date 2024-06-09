package repository.database;

import domain.Customer;
import domain.Menu;
import domain.Order;
import service.OrderRepository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseOrderRepository implements OrderRepository {
    private static String serverName = "jdbc:mysql://localhost:3306/java103";
    private static String userName;
    private static String passWord;
    private static double nextOrderCode = 0;
    public DatabaseOrderRepository(){
        try{Class.forName("com.mysql.cj.jdbc.Driver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        Path path = Paths.get("credentials.txt");
        if (!Files.exists(path)) {
            try {
                Files.createFile(path);
                try (BufferedWriter writer = Files.newBufferedWriter(path)) {
                    writer.write("defaultUsername\n");
                    writer.write("defaultPassword\n");
                }
            } catch (IOException e) {
                System.out.println("An error occurred while creating the credentials file.");
                e.printStackTrace();
            }
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader("credentials.txt"));

            userName = reader.readLine();
            passWord = reader.readLine();

            reader.close();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        try (Connection connection = DriverManager.getConnection(serverName, userName, passWord)) {
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "ordersdb", null);
            if (!tables.next()) {
                // Table does not exist
                String createTableSQL = "CREATE TABLE ordersdb (" +
                        "ocode VARCHAR(20) PRIMARY KEY, " +
                        "customerid VARCHAR(255) NOT NULL, " +
                        "FOREIGN KEY (customerid) REFERENCES customersdb(id)," +
                        "totalamount DOUBLE NOT NULL, menuid VARCHAR(255))"; //+
//                        "FOREIGN KEY (menuid) REFERENCES menus(code))";
                try (Statement statement = connection.createStatement()) {
                    statement.execute(createTableSQL);
                    System.out.println("Table 'ordersdb' created.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public Order addOrder(Customer c) {
        if(c == null) return null;
        try{Class.forName("com.mysql.cj.jdbc.Driver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        nextOrderCode = getOrderCount();
        String orderCode = "O" + ++nextOrderCode;
        Order order = new Order(orderCode,c);
        String insertSQL = "INSERT INTO ordersdb (ocode, customerid, totalamount, menuid) VALUE (?, ?, ?, ?)";
        try(Connection connection = DriverManager.getConnection(serverName,userName,passWord);
            PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)){
            preparedStatement.setString(1, orderCode);
            preparedStatement.setString(2,c.getId());
            preparedStatement.setDouble(3,order.getTotalAmount());
            preparedStatement.setString(4,order.getItems().toString());
            int rowInserted = preparedStatement.executeUpdate();
            if(rowInserted > 0 ){
                return new Order(orderCode,c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Order updateOrder(Order order) {
        if(order == null) return null;
        try{Class.forName("com.mysql.cj.jdbc.Driver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        String orderCode = order.getOrderCode();
        Customer customer = order.getCustomer();
        double totalAmount = order.getTotalAmount();
        String updateSQL = "UPDATE ordersdb SET customerid = ?, totalamount = ?, menuid = ? WHERE ocode = ?";
        try(Connection connection = DriverManager.getConnection(serverName,userName,passWord);
            PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)){
            preparedStatement.setString(1,customer.getId());
            preparedStatement.setDouble(2,totalAmount);
            preparedStatement.setString(3,order.getItems().toString());
            preparedStatement.setString(4,orderCode);
            int rowsUpdated = preparedStatement.executeUpdate();
            if(rowsUpdated > 0){
                System.out.println("Total amount after finish query :" + totalAmount);
                return order;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Order findByCode(String orderCode) {
        if(orderCode == null) return null;
        try{Class.forName("com.mysql.cj.jdbc.Driver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        String selectSQL = "SELECT * FROM ordersdb WHERE ocode = ?";

        try (Connection connection = DriverManager.getConnection(serverName,userName,passWord);
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, orderCode);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String orderCodeDb = resultSet.getString("ocode");
                    String customerId = resultSet.getString("customerid");
                    DatabaseCustomerRepository dcr = new DatabaseCustomerRepository();
                    Customer customer = dcr.findCustomer(customerId);
                    Order fromDB = new Order(orderCodeDb,customer);
                    String menuid = resultSet.getString("menuid");
                    String patternString = "Menu\\{Code : (.*?), Name : (.*?), price : ([\\d\\.]+)\\}=(\\d+)";
                    Pattern pattern = Pattern.compile(patternString);
                    Matcher matcher = pattern.matcher(menuid);
//                    try {
                        if (matcher.find()) {
                            String code = matcher.group(1);
                            String name = matcher.group(2);
                            double price = Double.parseDouble(matcher.group(3));
                            int quantity = Integer.parseInt(matcher.group(4));
                            Menu item = new Menu(code, name, price);
                            fromDB.addItem(item, quantity);
                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
                    return fromDB;
                } else {
                    System.out.println("orders not found.");
                    return null;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Collection<Order> listAllOrderOwnedBy(String customerId) {
        if(customerId == null) return null;
        List<Order> orders = new ArrayList<>();
        String selectSQL = "SELECT * FROM ordersdb WHERE customerid = ?";

        try (Connection connection = DriverManager.getConnection(serverName, userName, passWord);
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, customerId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String orderCode = resultSet.getString("ocode");
                    double totalAmount = resultSet.getDouble("totalamount");
                    String customerId2 = resultSet.getString("customerid");
                    DatabaseCustomerRepository dcr = new DatabaseCustomerRepository();
                    Customer customer = dcr.findCustomer(customerId2);
                    Order order = new Order(orderCode, customer);
                    String menuid = resultSet.getString("menuid");
                    String patternString = "Menu\\{Code : (.*?), Name : (.*?), price : ([\\d\\.]+)\\}=(\\d+)";
                    Pattern pattern = Pattern.compile(patternString);
                    Matcher matcher = pattern.matcher(menuid);
                    if (matcher.find()) {
                        String code = matcher.group(1);
                        String name = matcher.group(2);
                        double price = Double.parseDouble(matcher.group(3));
                        int quantity = Integer.parseInt(matcher.group(4));
                        Menu item = new Menu(code, name, price);
                        order.addItem(item, quantity);
                    }
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public Collection<Order> listAllCustomerOrder() {
        try{Class.forName("com.mysql.cj.jdbc.Driver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        List<Order> orders = new ArrayList<>();
        String selectSQL = "SELECT * FROM ordersdb";

        try (Connection connection = DriverManager.getConnection(serverName, userName, passWord);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {

            while (resultSet.next()) {
                String orderCode = resultSet.getString("ocode");
                String customerId2 = resultSet.getString("customerid");
                DatabaseCustomerRepository dcr = new DatabaseCustomerRepository();
                Customer customer = dcr.findCustomer(customerId2);
                Order order = new Order(orderCode, customer);
                String menuid = resultSet.getString("menuid");
                String patternString = "Menu\\{Code : (.*?), Name : (.*?), price : ([\\d\\.]+)\\}=(\\d+)";
                Pattern pattern = Pattern.compile(patternString);
                Matcher matcher = pattern.matcher(menuid);
                if (matcher.find()) {
                    String code = matcher.group(1);
                    String name = matcher.group(2);
                    double price = Double.parseDouble(matcher.group(3));
                    int quantity = Integer.parseInt(matcher.group(4));
                    Menu item = new Menu(code, name, price);
                    order.addItem(item, quantity);
                }
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public boolean removeOrder(String orderCode) {
        if(orderCode == null) return false;
        boolean isRemoved = false;

        // Prepare the SQL statement
        String deleteSQL = "UPDATE ordersdb SET customerid = ?, totalamount = ?, menuid = ? WHERE ocode = ?";
        String updated = "Not Available";
        double totalAmount = 0;
        try {
            // Load the JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection(serverName, userName, passWord);
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {

            // Set the order code in the prepared statement
            preparedStatement.setString(1, updated);
            preparedStatement.setDouble(2, totalAmount);
            preparedStatement.setString(3,updated);
            preparedStatement.setString(4, orderCode);

            // Execute the delete statement
            int rowsAffected = preparedStatement.executeUpdate();

            // If one or more rows were deleted, the order was removed
            isRemoved = rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return isRemoved;
    }

    public int getOrderCount() {
        int count = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String countSQL = "SELECT MAX(ocode) FROM ordersdb";
        try (Connection connection = DriverManager.getConnection(serverName, userName, passWord);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(countSQL)) {
            if (resultSet.next()) {
                String result = resultSet.getString(1);
                if(result != null) {
                    Pattern pattern = Pattern.compile("(\\d+\\.\\d+)$");
                    Matcher matcher = pattern.matcher(result);
                    if(matcher.find()){
                        double extractedValue = Double.parseDouble(matcher.group(1));
                        count = (int) extractedValue;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}