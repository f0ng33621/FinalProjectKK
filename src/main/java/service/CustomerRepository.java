package service;

import domain.Customer;

import java.util.Collection;

public interface CustomerRepository {
    public Customer CreateCustomer(String customerName,String phoneNumber);
    public Customer findCustomer(String customerId);
    public Customer updateCustomer(Customer customer);
    public Collection<Customer> allCustomers();

}
