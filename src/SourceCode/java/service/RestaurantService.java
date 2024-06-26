package service;

import domain.Customer;
import domain.Exception.CheckException;
import domain.Menu;
import domain.Order;

import java.util.Collection;

public class RestaurantService {
    private final CustomerRepository customer;
    private final MenuRepository menu;
    private final OrderRepository order;


    public RestaurantService(CustomerRepository customer, MenuRepository menu, OrderRepository order) {
        this.customer = customer;
        this.menu = menu;
        this.order = order;
    }
    //แก้ return null นะ
    public Customer registerCustomer(String customerName, String phoneNumber){
        if (customerName.isBlank()) return null;
        return customer.createCustomer(customerName,phoneNumber);
    }
    public Customer renameCustomer(String customerId,String customerName) throws CheckException{
        if (customerName.isBlank() || customerId.isBlank())return null;
        Customer c = customer.findCustomer(customerId);
        if (c == null) return null;
        c.setName(customerName);
        return customer.updateCustomer(c);
    }
    public Customer changePhoneNumberCustomer(String customerId,String phoneNumber) throws CheckException{
        if (phoneNumber.isBlank() || customerId.isBlank())return null;
        Customer c = customer.findCustomer(customerId);
        if (c == null) return null;
        c.setPhoneNumber(phoneNumber);
        return customer.updateCustomer(c);
    }
    public Customer findCustomer(String customerId){
        if (customerId.isBlank()) return null;
        return customer.findCustomer(customerId);
    }
    public Collection<Customer> allCustomer(){
        return customer.allCustomers();
    };

    public Menu addMenu(String menuName,double price){
        if (menuName.isBlank() || price < 0) return null;
        return menu.createMenu(menuName,price);
    }
    public Menu renameMenu(String code,String name) throws CheckException{
        if (code.isBlank() || name.isBlank())return null;
        Menu m = menu.findMenu(code);
        if (m == null) return null;
        m.setName(name);
        return menu.updateMenu(m);
    }
    public Menu repriceMenu(String code,double price) throws CheckException{
        if (code.isBlank() || price < 0)return null;
        Menu m = menu.findMenu(code);
        if (m == null) return null;
        m.setPrice(price);
        return menu.updateMenu(m);
    }
    public Menu findMenu(String menuCode){
        if (menuCode.isBlank()) return null;
        return menu.findMenu(menuCode);
    }
    public Collection<Menu> allMenu() {
        return menu.allMenu();
    }
    public boolean removeMenu(String menuCode){
        boolean removed = menu.removeMenu(menuCode);
        if (removed) {
            System.out.println("Menu item " + menuCode + " removed successfully.");
        } else {
            System.out.println("Menu item " + menuCode + " not found.");
        }
        return removed;
    }
    public Order addOrder(String customerId){
        Customer c = customer.findCustomer(customerId);
        if (c == null) return null;
        return order.addOrder(c);
    }
    public Order addItem(String orderCode,String menuCode, int quantity){
        Order o = order.findByCode(orderCode);
        Menu m = menu.findMenu(menuCode);
        if (m == null || o == null || !o.addItem(m,quantity)) return null;
        return order.updateOrder(o);
    }
    public Order removeItem(String orderCode,String menuCode, int quantity){
        Order o = order.findByCode(orderCode);
        Menu m = menu.findMenu(menuCode);
        if (m == null || o == null || !o.removeItem(m,quantity)) return null;
        return order.updateOrder(o);
    }
    public Order cancleOrder(String orderCode){
        boolean removed = order.removeOrder(orderCode);
        Order cancle = order.findByCode(orderCode);
        if (removed) {
            System.out.println("Order " + orderCode + " removed successfully.");
        } else {
            System.out.println("Order " + orderCode + " not found.");
        }
        return cancle;
    }
    public void listItem(String orderCode){
        Order o = order.findByCode(orderCode);
        o.listAllItems();
    }
    public Collection<Order> listAllOrderOwnedBy(String customerId){
        if (customerId == null || findCustomer(customerId) == null) return null;
        return order.listAllOrderOwnedBy(customerId);
    }
    public Customer adminAcc(){
        Customer admin = new Customer("C0","ADMIN","000");
        return admin;
    }
    public Collection<Order> listAllOrder() {
      return order.listAllCustomerOrder();
    }


}
