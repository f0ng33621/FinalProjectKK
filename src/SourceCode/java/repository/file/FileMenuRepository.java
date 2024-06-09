package repository.file;

import domain.Customer;
import domain.Menu;
import domain.Order;
import service.MenuRepository;

import java.io.*;
import java.util.*;

public class FileMenuRepository implements MenuRepository,Serializable {

    private static long nextMenuId = 0;
    private final String filename = "FileMenu.dat";
    private Map<String,Menu> repo;
    public FileMenuRepository(){
        File check = new File(filename);
        if(check.exists()){
            try (FileInputStream fis = new FileInputStream(filename);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                repo = (Map<String, Menu>) ois.readObject();
                nextMenuId = repo.values().stream()
                        .mapToLong(m -> Long.parseLong(m.getCode().substring(1)))
                        .max()
                        .orElse(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            nextMenuId = 0;
            repo = new TreeMap<>();
            try (FileOutputStream fos = new FileOutputStream(filename);
                 BufferedOutputStream bos = new BufferedOutputStream(fos);
                 ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(repo);
                oos.writeLong(nextMenuId);
                oos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public Menu createMenu(String menuName, double price) {
        if(menuName == null || price <0.0) return null;
        String MenuId = "M" + ++nextMenuId;
        Menu menu = new Menu(MenuId,menuName,price);
        if (repo.putIfAbsent(MenuId, menu) == null) {
            try (FileOutputStream fos = new FileOutputStream(filename);
                 BufferedOutputStream bos = new BufferedOutputStream(fos);
                 ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(repo);
                oos.writeLong(nextMenuId);
                oos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return menu;
        }
        return null;
    }

    @Override
    public Menu findMenu(String menuCode) {
        if(menuCode == null) return null;
        return repo.get(menuCode);
    }

    @Override
    public Menu updateMenu(Menu menu) {
        if(menu == null) return null;
        repo.replace(menu.getCode(), menu);
        try (FileOutputStream fos = new FileOutputStream(filename);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(repo);
            oos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return menu;
    }

    @Override
    public Collection<Menu> allMenu() {
        return repo.values();
    }

    @Override
    public boolean removeMenu(String menuCode) {
        if (repo.containsKey(menuCode)){
            repo.remove(menuCode);
            try (FileOutputStream fos = new FileOutputStream(filename);
                 BufferedOutputStream bos = new BufferedOutputStream(fos);
                 ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(repo);
                oos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return false;
    }
}
