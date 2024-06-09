package repository.database.databaseonline;

import domain.Customer;
import domain.Menu;
import domain.Order;
import service.OrderRepository;

import java.sql.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatabaseOrderRepository implements OrderRepository {
    private static final String hostName = "javadatabase.database.windows.net";
    private static final String dbName = "javaproject";
    private static final String user = "Fong";
    private static final String password = "Darkkiller_204";
    private static final String url = String.format("jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;", hostName, dbName, user, password);

    private static double nextOrderCode = 0;
    public DatabaseOrderRepository(){
        try{Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        try (Connection connection = DriverManager.getConnection(url)) {
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "orders", null);
            if (!tables.next()) {
                // Table does not exist
                String createTableSQL = "CREATE TABLE orders (" +
                        "ordercode VARCHAR(20) PRIMARY KEY, " +
                        "customerid VARCHAR(255) NOT NULL, " +
                        "FOREIGN KEY (customerid) REFERENCE Customers(id)," +
                        "totalamount DOUBLE NOT NULL, menuid VARCHAR(255) NOT NULL";
                try (Statement statement = connection.createStatement()) {
                    statement.execute(createTableSQL);
                    System.out.println("Table 'orders' created.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public Order addOrder(Customer c) {
        if(c == null) return null;
        try{Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        nextOrderCode = getOrderCount();
        String orderCode = "O" + ++nextOrderCode;
        Order order = new Order(orderCode,c);
        String insertSQL = "INSERT INTO orders (ordercode, customerid, totalamount, menuid) VALUES (?, ?, ?, ?)";
        try(Connection connection = DriverManager.getConnection(url);
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
        try{Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }

        String updateSQL = "UPDATE orders SET customerid = ?, totalamount = ?, menuid = ? WHERE ordercode = ?";
        try(Connection connection = DriverManager.getConnection(url);
            PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)){
            preparedStatement.setString(1,order.getCustomer().getId());
            preparedStatement.setDouble(2,order.getTotalAmount());
            preparedStatement.setString(3,order.getItems().toString());
            preparedStatement.setString(4,order.getOrderCode());
            int rowsUpdated = preparedStatement.executeUpdate();
            if(rowsUpdated > 0){
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
        try{Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        String selectSQL = "SELECT * FROM orders WHERE ordercode = ?";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, orderCode);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String orderCodeDb = resultSet.getString("ordercode");
                    String customerId = resultSet.getString("customerid");
                    repository.database.databaseonline.DatabaseCustomerRepository dcr = new DatabaseCustomerRepository();
                    Customer customer = dcr.findCustomer(customerId);
                    Order fromDB = new Order(orderCodeDb,customer);
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
                        fromDB.addItem(item, quantity);
                    }
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
        String selectSQL = "SELECT * FROM orders WHERE customerid = ?";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, customerId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String orderCode = resultSet.getString("ordercode");
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
        try{Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        List<Order> orders = new ArrayList<>();
        String selectSQL = "SELECT * FROM orders";

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {

            while (resultSet.next()) {
                String orderCode = resultSet.getString("ordercode");
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
        String deleteSQL = "UPDATE orders SET customerid = ?, totalamount = ?, menuid = ? WHERE ordercode = ?";
        String update = "Not Available";
        double amount = 0.0;
        try {
            // Load the JDBC driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)) {

            // Set the order code in the prepared statement
            preparedStatement.setString(1, update);
            preparedStatement.setDouble(2, amount);
            preparedStatement.setString(3, update);
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
    public double getOrderCount() {
        double count = 0;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String countSQL = "SELECT MAX(ordercode) FROM orders";
        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(countSQL)) {
            if (resultSet.next()) {
                String result = resultSet.getString(1);
                if(result != null) {
                    Pattern pattern = Pattern.compile("(\\d+\\.\\d+)$");
                    Matcher matcher = pattern.matcher(result);
                    if(matcher.find()){
                        double extractedValue = Double.parseDouble(matcher.group(1));
                        count = extractedValue;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}
