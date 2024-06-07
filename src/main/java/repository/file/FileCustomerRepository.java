package repository.file;

import com.sun.source.tree.Tree;
import domain.Customer;
import service.CustomerRepository;

import java.io.*;
import java.util.*;


public class FileCustomerRepository implements CustomerRepository {
    private long nextCustomerId;
    private final String filename = "src/main/java/repository/file/FileCustomer.dat";
    private Map<String, Customer> repo;
    public FileCustomerRepository(){
        File check = new File(filename);
        if(check.exists()){
            try{FileInputStream fis = new FileInputStream(filename);
                int content;
                while((content = fis.read()) != -1) {
                    System.out.println((char) content);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            nextCustomerId = 1;
            TreeMap<String, Customer> repo = new TreeMap<>();
            try (FileOutputStream fileOut = new FileOutputStream(filename);
                 ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
                out.writeObject(repo);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Customer createCustomer(String customerName, String phoneNumber) {
        if(customerName == null || phoneNumber == null) return null;
        String customerId = "C" + ++nextCustomerId;
        Customer c = new Customer(customerId,customerName,phoneNumber);
        if (repo.putIfAbsent(customerId, c) == null) {
            try(FileOutputStream fileOut = new FileOutputStream(filename);
                ObjectOutputStream out = new ObjectOutputStream(fileOut)){
                out.writeObject(c);
            }
            catch (IOException e){
                e.printStackTrace();
            }
            return c;
        }
        return null;
    }

    @Override
    public Customer findCustomer(String customerId) {
        if(customerId == null) return null;
        try(FileInputStream filein = new FileInputStream(filename);
        ObjectInputStream in = new ObjectInputStream(filein);){
            Customer cin;
            while ((cin = (Customer) in.readObject()) != null){
                if(cin.getId().equals(customerId)){
                    return cin;
                }
            }
        }catch (EOFException e){ // End of file Exception

        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        if(customer == null) return null;
        repo.replace(customer.getId(),customer);
        try (FileOutputStream fileOut = new FileOutputStream(filename);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(repo);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Collection<Customer> allCustomers() {
        List<Customer> customers = new ArrayList<>();
        try (FileInputStream fileIn = new FileInputStream(filename);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            Customer cin;
            while ((cin = (Customer) in.readObject()) != null) {
                customers.add(cin);
            }
        } catch (EOFException e) {
            // This exception is expected when there are no more objects to read
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return customers;
    }
}
