package repository.file;

import domain.Customer;
import domain.Order;
import service.OrderRepository;

import java.io.*;
import java.util.*;

public class FileOrderRepository implements OrderRepository,Serializable {
    private static long nextOrderCode = 0;
    private Map<String, Order> repo;
    private static final String filename = "FileOrder.dat";
    public FileOrderRepository(){
        File check = new File(filename);
        if(check.exists()){
            try (FileInputStream fis = new FileInputStream(filename);
                 BufferedInputStream bis = new BufferedInputStream(fis);
                 ObjectInputStream ois = new ObjectInputStream(bis)) {
                repo = (Map<String, Order>) ois.readObject();
                nextOrderCode = repo.values().stream()
                        .mapToLong(o -> Long.parseLong(o.getOrderCode().substring(1)))
                        .max()
                        .orElse(0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            nextOrderCode = 0;
            repo = new TreeMap<>();
            try (FileOutputStream fos = new FileOutputStream(filename);
                 BufferedOutputStream bos = new BufferedOutputStream(fos);
                 ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(repo);
                oos.writeLong(nextOrderCode);
                oos.flush();
            } catch (Exception e) {
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
            try(FileOutputStream fileOut = new FileOutputStream(filename);
                BufferedOutputStream bos = new BufferedOutputStream(fileOut);
                ObjectOutputStream out = new ObjectOutputStream(bos)){
                out.writeObject(order);
                out.writeLong(nextOrderCode);
                out.flush();
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
        repo.replace(order.getOrderCode(),order);
        try (FileOutputStream fos = new FileOutputStream(filename);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(repo);
            oos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return order;
    }

    @Override
    public Order findByCode(String orderCode) {
        if(orderCode == null) return null;
        return repo.get(orderCode);
    }

    @Override
    public Collection<Order> listAllOrderOwnedBy(String customerId) {
        return repo.values()
                .stream()
                .filter(a -> a.getCustomer().getId().equals(customerId)).toList();
    }

    @Override
    public Collection<Order> listAllCustomerOrder() {
        return repo.values();
    }

    @Override
    public boolean removeOrder(String orderCode) {
        if (repo.containsKey(orderCode)){
            repo.remove(orderCode);
            return true;
        }
        return false;
    }
}
