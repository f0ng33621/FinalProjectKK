package repository.database.databaseonline;


import domain.Menu;
import service.MenuRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DatabaseMenuRepository implements MenuRepository {
    private static final String hostName = "javadatabase.database.windows.net";
    private static final String dbName = "javaproject";
    private static final String user = "Fong";
    private static final String password = "Darkkiller_204";
    private static final String url = String.format("jdbc:sqlserver://%s:1433;database=%s;user=%s;password=%s;encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;", hostName, dbName, user, password);

    private static double nextMenuId = 0;
    public DatabaseMenuRepository(){
        try{Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        try (Connection connection = DriverManager.getConnection(url)) {
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "menus", null);
            if (!tables.next()) {
                // Table does not exist
                String createTableSQL = "CREATE TABLE menus (" +
                        "code VARCHAR(20) PRIMARY KEY, " +
                        "name VARCHAR(255) NOT NULL, " +
                        "price FLOAT NOT NULL)";
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
        try{Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        nextMenuId = getMenuCount();
        String menuId = "M" + ++nextMenuId;
        String insertSQL = "INSERT INTO menus (code, name, price) VALUES (?, ?, ?)";
        try(Connection connection = DriverManager.getConnection(url);
            PreparedStatement preparedStatement = connection.prepareStatement(insertSQL)){
            preparedStatement.setString(1, menuId);
            preparedStatement.setString(2,menuName);
            preparedStatement.setFloat(3,(float) price);
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
        try{Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        String selectSQL = "SELECT * FROM menus WHERE code = ?";

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(selectSQL)) {
            preparedStatement.setString(1, menuCode);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    String id = resultSet.getString("id");
                    String name = resultSet.getString("name");
                    float price = resultSet.getFloat("price");
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
        try{Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        String code = menu.getCode();
        String Name = menu.getName();
        float price = (float) menu.getPrice();
        String updateSQL = "UPDATE menus SET name = ?, price = ? WHERE code = ?";
        try(Connection connection = DriverManager.getConnection(url);
            PreparedStatement preparedStatement = connection.prepareStatement(updateSQL)){
            preparedStatement.setString(1,Name);
            preparedStatement.setFloat(2,price);
            preparedStatement.setString(3,code);
            return new Menu(code,Name,price);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Collection<Menu> allMenu() {
        try{Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        List<Menu> menuslist = new ArrayList<>();
        String selectSQL = "SELECT * FROM menus";

        try (Connection connection = DriverManager.getConnection(url);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectSQL)) {

            while (resultSet.next()) {
                String id = resultSet.getString("code");
                String name = resultSet.getString("name");
                float price = resultSet.getFloat("price");
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
        try{Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");}
        catch (ClassNotFoundException e){
            e.printStackTrace();
        }
        String deleteSQL = "DELETE FROM menus WHERE code = ?";
        try(Connection connection = DriverManager.getConnection(url);
            PreparedStatement preparedStatement = connection.prepareStatement(deleteSQL)){
            preparedStatement.setString(1,menuCode);
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
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String countSQL = "SELECT COUNT(*) AS count FROM menus";
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
