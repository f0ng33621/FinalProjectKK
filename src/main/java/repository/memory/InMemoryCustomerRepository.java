package repository.memory;

import domain.Customer;
import service.CustomerRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InMemoryCustomerRepository implements CustomerRepository {
    private static long nextCustomerId = 0;
    private final Map<String,Customer> repo = new HashMap<>();
    @Override
    public Customer createCustomer(String customerName, String phoneNumber) {
        String customerId = "C" + ++nextCustomerId;
        Customer c = new Customer(customerId,customerName,phoneNumber);
        if (repo.putIfAbsent(customerId, c) == null) return c;
        return null;
    }

    @Override
    public Customer findCustomer(String customerId) {
        return repo.get(customerId);
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        repo.replace(customer.getId(),customer);
        return customer;
    }

    @Override
    public Collection<Customer> allCustomers() {
        return repo.values();
    }
}
