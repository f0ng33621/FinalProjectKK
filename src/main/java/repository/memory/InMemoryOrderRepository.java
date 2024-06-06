package repository.memory;

import domain.Customer;
import domain.Menu;
import domain.Order;
import service.OrderRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InMemoryOrderRepository implements OrderRepository {
    private static long nextOrderCode = 0;
    private final Map<String, Order> repo = new HashMap<>();

    @Override
    public Order addOrder(Customer c) {
        String orderCode = "O" + ++nextOrderCode;
        Order order = new Order(orderCode,c);
        if (repo.putIfAbsent(orderCode, order) == null) return order;
        return null;
    }

    @Override
    public Order updateOrder(Order order) {
        repo.replace(order.getOrderCode(),order);
        return order;

    }

    @Override
    public Order findByCode(String orderCode) {
        return repo.get(orderCode);
    }
    public double getTotalAmountOfAllOrders(){
        return repo.values().stream().mapToDouble(Order::getTotalAmount).sum();
    }
    @Override
    public boolean removeOrder(String orderCode) {
        if (repo.containsKey(orderCode)){
            repo.remove(orderCode);
            return true;
        }
        return false;
    }


    @Override
    public Collection<Order> listAllOrderOwnedBy(String customerId) {
        return repo.values()
                .stream()
                .filter(a -> a.getCustomer().getId().equals(customerId)).toList();
    }
    public Collection<Order> listAllCustomerOrder(){
        return repo.values();
    }
}
