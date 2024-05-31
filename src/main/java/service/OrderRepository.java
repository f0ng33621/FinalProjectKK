package service;

import domain.Order;

import java.util.Collection;

public interface OrderRepository {
    public Order addOrder(String customerId, Order order);

    public Order updateOrder(Order order);
    Order findByCode(String orderCoder);
    Collection<Order> listAllOrder();


}
