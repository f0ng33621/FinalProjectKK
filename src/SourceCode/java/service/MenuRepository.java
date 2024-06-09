package service;

import domain.Customer;
import domain.Menu;

import java.util.Collection;

public interface MenuRepository {
    public Menu createMenu(String menuName,double price);
    public Menu findMenu(String menuCode);
    public Menu updateMenu(Menu menu);
    public Collection<Menu> allMenu();
    public boolean removeMenu(String menuCode);



}
