package service;

import domain.Customer;
import domain.Menu;

import java.util.Collection;

public interface MenuRepository {
    public Menu CreateCustomer(String customerName, String phoneNumber);
    public Menu findMenu(String menuCode);
    public Menu updateMenu(Menu menu);
    public Collection<Menu> allMenu();

}
