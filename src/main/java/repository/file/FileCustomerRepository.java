package repository.file;

import com.sun.source.tree.Tree;
import domain.Customer;
import service.CustomerRepository;

import java.io.*;
import java.util.*;


public class FileCustomerRepository implements CustomerRepository,Serializable{
    private long nextCustomerId;
    private final String filename = "src/main/java/repository/file/FileCustomer.dat";
    private Map<String, Customer> repo;
    public FileCustomerRepository() {
        File check = new File(filename);
        if (check.exists()) {
            try (FileInputStream fis = new FileInputStream(filename);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                repo = (Map<String, Customer>) ois.readObject();
                nextCustomerId = repo.values().stream()
                        .mapToLong(c -> Long.parseLong(c.getId().substring(1)))
                        .max()
                        .orElse(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            nextCustomerId = 0;
            repo = new TreeMap<>();
            try (FileOutputStream fos = new FileOutputStream(filename);
                 BufferedOutputStream bos = new BufferedOutputStream(fos);
                 ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(repo);
                oos.writeLong(nextCustomerId);
                oos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Customer createCustomer(String customerName, String phoneNumber) {
        if (customerName == null || phoneNumber == null) return null;
        String customerId = "C" + ++nextCustomerId;
        Customer c = new Customer(customerId, customerName, phoneNumber);
        if (repo.putIfAbsent(customerId, c) == null) {
            try (FileOutputStream fos = new FileOutputStream(filename);
                 BufferedOutputStream bos = new BufferedOutputStream(fos);
                 ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(repo);
                oos.writeLong(nextCustomerId);
                oos.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return c;
        }
        return null;
    }
    @Override
    public Customer findCustomer(String customerId) {
        if(customerId == null) return null;
        return repo.get(customerId);
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        if(customer == null) return null;
        repo.replace(customer.getId(),customer);
        return customer;
    }

    @Override
    public Collection<Customer> allCustomers() {
        return repo.values();
    }
}
