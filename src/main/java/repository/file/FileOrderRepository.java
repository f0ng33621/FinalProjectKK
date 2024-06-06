package repository.file;

import domain.Customer;
import domain.Menu;
import domain.Order;
import service.OrderRepository;

import java.io.*;
import java.util.*;

public class FileOrderRepository implements OrderRepository {
    private static long nextOrderCode = 0;
    private final Map<String, Order> repo = new HashMap<>();
    private static final String filename = "src/main/java/repository/file/FileOrder.dat";
    public FileOrderRepository(){
        File check = new File(filename);
        if(check.exists()){
            try{
                FileInputStream fis = new FileInputStream(filename);
                int content;
                while((content = fis.read()) != -1) {
                    System.out.println((char) content);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        else {
            nextOrderCode = 1;
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
    public Order addOrder(Customer c) {
        if(c == null) return null;
        String orderCode = "O" + ++nextOrderCode;
        Order order = new Order(orderCode,c);
        if (repo.putIfAbsent(orderCode, order) == null){
            try(FileOutputStream fileOut = new FileOutputStream(filename); ObjectOutputStream out = new ObjectOutputStream(fileOut)){
                out.writeObject(order);
            }
            catch (IOException e){
                e.printStackTrace();
            }
        return order;
        }
        return null;
    }

    @Override
    public Order updateOrder(Order order) {
        if(order == null) return null;
        List<Order> orders = new ArrayList<>();
        try (FileInputStream fileIn = new FileInputStream(filename);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            Order ord;
            while ((ord = (Order) in.readObject()) != null) {
                if (ord.getOrderCode().equals(order.getOrderCode())) {
                    orders.add(order); // add the updated order
                } else {
                    orders.add(ord); // add the existing order
                }
            }
        } catch (EOFException e) {
            // This exception is expected when there are no more objects to read
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        try (FileOutputStream fileOut = new FileOutputStream(filename);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            for (Order ord : orders) {
                out.writeObject(ord);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return order;
    }

    @Override
    public Order findByCode(String orderCode) {
        if(orderCode == null) return null;
        try(FileInputStream filein = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(filein);){
            Order ord;
            while ((ord = (Order) in.readObject()) != null){
                if(ord.getOrderCode().equals(orderCode)){
                    return ord;
                }
            }
        }catch (EOFException e){ // End of file Exception

        } catch (IOException | ClassNotFoundException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Collection<Order> listAllOrderOwnedBy(String customerId) {
        List<Order> orders = new ArrayList<>();
        try (FileInputStream fileIn = new FileInputStream(filename);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            Order ord;
            while ((ord = (Order) in.readObject()) != null) {
                if (ord.getCustomer().getId().equals(customerId)) {
                    orders.add(ord);
                }
            }
        } catch (EOFException e) {
            // This exception is expected when there are no more objects to read
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public Collection<Order> listAllCustomerOrder() {
        List<Order> orders = new ArrayList<>();
        try (FileInputStream fileIn = new FileInputStream(filename);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            Order ord;
            while ((ord = (Order) in.readObject()) != null) {
                    orders.add(ord);
            }
        } catch (EOFException e) {
            // This exception is expected when there are no more objects to read
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public boolean removeOrder(String orderCode) {
        if(orderCode == null) return false;
        List<Order> orders = new ArrayList<>();
        boolean isRemoved = false;

        // Read all orders from the file
        try (FileInputStream fileIn = new FileInputStream(filename);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            Order ord;
            while ((ord = (Order) in.readObject()) != null) {
                if (!ord.getOrderCode().equals(orderCode)) {
                    orders.add(ord); // add the order if it's not the one to be removed
                } else {
                    isRemoved = true; // set the flag to true if the order is removed
                }
            }
        } catch (EOFException e) {
            // This exception is expected when there are no more objects to read
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        // Write the remaining orders back to the file
        try (FileOutputStream fileOut = new FileOutputStream(filename);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            for (Order ord : orders) {
                out.writeObject(ord);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isRemoved;
    }
}
