package repository.file;

import domain.Customer;
import domain.Menu;
import domain.Order;
import service.MenuRepository;

import java.io.*;
import java.util.*;

public class FileMenuRepository implements MenuRepository {

    private static long nextMenuId = 0;
    private final String filename = "src/main/java/repository/file/FileMenu.txt";
    private final Map<String,Menu> repo = new HashMap<>();
    public FileMenuRepository(){
        File check = new File(filename);
        if(check.exists()){
            try{
                FileInputStream fis = new FileInputStream(filename);
                int content;
                while((content = fis.read()) != -1) {
                    System.out.println((char) content);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            nextMenuId = 1;
            TreeMap<String, Customer> repo = new TreeMap<>();
            try (FileOutputStream fileOut = new FileOutputStream(filename);
                 ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                out.writeObject(repo);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public Menu createMenu(String menuName, double price) {
        if(menuName == null || price == null) return null;
        String MenuId = "M" + ++nextMenuId;
        Menu menu = new Menu(MenuId,menuName,price);
        if (repo.putIfAbsent(MenuId, menu) == null) {
            try(FileOutputStream fileOut = new FileOutputStream(filename);
                ObjectOutputStream out = new ObjectOutputStream(fileOut)){
                out.writeObject(menu);
            }
            catch (IOException e){
                e.printStackTrace();
            }
            return menu;
        }
        return null;
    }

    @Override
    public Menu findMenu(String menuCode) {
        if(menuCode == null) return null;
        try(FileInputStream filein = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(filein);){
            Menu min;
            while ((min = (Menu) in.readObject()) != null){
                if(min.getCode().equals(menuCode)){
                    return min;
                }
            }
        }catch (EOFException e){ // End of file Exception

        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Menu updateMenu(Menu menu) {
        if(menu == null) return null;
        List<Menu> menus = new ArrayList<>();
        try (FileInputStream fileIn = new FileInputStream(filename);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            Menu men;
            while ((men = (Menu) in.readObject()) != null) {
                if (men.getCode().equals(menu.getCode())) {
                    menus.add(menu); // add the updated order
                } else {
                    menus.add(men); // add the existing order
                }
            }
        } catch (EOFException e) {
            // This exception is expected when there are no more objects to read
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (FileOutputStream fileOut = new FileOutputStream(filename);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            for (Menu men : menus) {
                out.writeObject(men);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return menu;
    }

    @Override
    public Collection<Menu> allMenu() {
        List<Menu> menus = new ArrayList<>();
        try (FileInputStream fileIn = new FileInputStream(filename);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            Menu min;
            while ((min = (Menu) in.readObject()) != null) {
                menus.add(min);
            }
        } catch (EOFException e) {
            // This exception is expected when there are no more objects to read
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return menus;
    }

    @Override
    public boolean removeMenu(String menuCode) {
        if(menuCode == null) return false;
        List<Menu> menus = new ArrayList<>(allMenu());
        Menu menuToRemove = null;
        for (Menu menu : menus) {
            if (menu.getCode().equals(menuCode)) {
                menuToRemove = menu;
                break;
            }
        }
        if (menuToRemove != null) {
            menus.remove(menuToRemove);
            try (FileOutputStream fileOut = new FileOutputStream(filename);
                 ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                for (Menu menu : menus) {
                    out.writeObject(menu);
                }
                return true;
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
