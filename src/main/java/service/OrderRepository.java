package service;

import domain.Customer;
import domain.Order;

import java.util.Collection;

public interface OrderRepository {
    public Order addOrder(Customer c);
    public Order updateOrder(Order order);
    Order findByCode(String orderCode);
    Collection<Order> listAllOrderOwnedBy(String customerId);
    Collection<Order> listAllCustomerOrder();
    public boolean removeOrder(String orderCode);

}
