package repository.database;

import domain.Customer;
import domain.Menu;
import service.MenuRepository;

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

public class DatabaseMenuRepository implements MenuRepository {
    private static final String serverName = "jdbc:mysql://localhost:3306/java103";
    private static String userName;
    private static String passWord;
    private static double nextMenuId = 0;
    public DatabaseMenuRepository(){
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
            ResultSet tables = dbm.getTables(null, null, "menus", null);
            if (!tables.next()) {
                // Table does not exist
                String createTableSQL = "CREATE TABLE menus (" +
                        "code VARCHAR(20) PRIMARY KEY, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "price Double NOT NULL)";
                try (Statement statement = connection.createStatement()) {
                    statement.execute(createTableSQL);
                    System.out.println("Table 'menus' created.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public Menu createMenu(String menuName, double price) {
        if(menuName == null || price <0.0) return null;
        try{Class.forName("com.mysql.cj.jdbc.Driver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        nextMenuId = getMenuCount();
        String menuId = "M" + ++nextMenuId;
        String insertSQL = "INSERT INTO menus (code, name, price) VALUE (?, ?, ?)";
        try(Connection connection = DriverManager.getConnection(serverName,userName,passWord);
            PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)){
            preparedStatement.setString(1, menuId);
            preparedStatement.setString(2,menuName);
            preparedStatement.setDouble(3,price);
            int rowInserted = preparedStatement.executeUpdate();
            if(rowInserted > 0 ){
                return new Menu(menuId,menuName,price);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Menu findMenu(String menuCode) {
        if(menuCode == null) return null;
        try{Class.forName("com.mysql.cj.jdbc.Driver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        String selectSQL = "SELECT * FROM menus WHERE code = ?";

        try (Connection connection = DriverManager.getConnection(serverName,userName,passWord);
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, menuCode);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String id = resultSet.getString("code");
                    String name = resultSet.getString("name");
                    double price = resultSet.getDouble("price");
                    Menu fromDB = new Menu(id,name,price);
                    return fromDB;
                } else {
                    System.out.println("Menus not found.");
                    return null;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Menu updateMenu(Menu menu) {
        if(menu == null) return null;
        try{Class.forName("com.mysql.cj.jdbc.Driver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        String code = menu.getCode();
        String Name = menu.getName();
        double price = menu.getPrice();
        String updateSQL = "UPDATE menus SET name = ?, price = ? WHERE code = ?";
        try(Connection connection = DriverManager.getConnection(serverName,userName,passWord);
            PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)){
            preparedStatement.setString(1,Name);
            preparedStatement.setDouble(2,price);
            preparedStatement.setString(3,code);
            return menu;
        }catch (Exception e){
            e.printStackTrace();

        }
        return null;
    }

    @Override
    public Collection<Menu> allMenu() {
        try{Class.forName("com.mysql.cj.jdbc.Driver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        List<Menu> menuslist = new ArrayList<>();
        String selectSQL = "SELECT * FROM menus";

        try (Connection connection = DriverManager.getConnection(serverName, userName, passWord);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {

            while (resultSet.next()) {
                String id = resultSet.getString("code");
                String name = resultSet.getString("name");
                double price = resultSet.getDouble("price");
                Menu menu = new Menu(id, name, price);
                menuslist.add(menu);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return menuslist;
    }

    @Override
    public boolean removeMenu(String menuCode) {
        if(menuCode == null) return false;
        try{Class.forName("com.mysql.cj.jdbc.Driver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        String name = "Not Available";
        double price = 0;
        String deleteSQL = "UPDATE menus SET name = ?, price = ? WHERE code = ?";
        try(Connection connection = DriverManager.getConnection(serverName,userName,passWord);
            PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)){
            preparedStatement.setString(1,name);
            preparedStatement.setDouble(2,price);
            preparedStatement.setString(3,menuCode);
            int rowDeleted = preparedStatement.executeUpdate();
            if(rowDeleted > 0 ){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public int getMenuCount() {
        int count = 0;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String countSQL = "SELECT COUNT(*) AS count FROM menus";
        try (Connection connection = DriverManager.getConnection(serverName, userName, passWord);
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
