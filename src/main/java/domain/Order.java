package domain;
import java.util.Map;
import java.util.HashMap;


public class Order {
    private final String orderCode;
    private final Customer customer;
    private final Map<Menu, Integer> items = new HashMap<>();
    private double totalAmount;

    public Order(String ordercode, Customer customer) {
        this.orderCode = ordercode;
        this.customer = customer;
    }

    public String getOrderCode() {
        return orderCode;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Map<Menu, Integer> getItems() {
        return items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }


    public boolean addItem(Menu item, int quantity) {
        if (quantity <= 0) return false;
        //จะเอา item ใส่ใน map (รถเข็น)
        //getOrDefault ถ้ามีของอยู่ ใน map (รถเข็น) อยู่แล้ว จะเอาของนั้นมาบวกเพิ่มกับ quantity ที่ต้องการเพิ่มเท่าไร
        items.put(item, items.getOrDefault(item, 0) + quantity);
        totalAmount += item.getPrice() * quantity;
        return true;
    }

    public boolean removeItem(Menu item, int quantity) {
        //nowQuantity ไปดูว่าของที่จะลบใน map (cart) ว่าในนั้นมีจำนวนเท่าไหร่
        int nowQuantity = items.get(item);
        if (!items.containsKey(item) || quantity <= 0 || nowQuantity < quantity) return false;
        if (nowQuantity == quantity) {
            totalAmount -= item.getPrice() * nowQuantity;
            items.remove(item);
        } else{
            totalAmount -= item.getPrice() * quantity;
            items.put(item, nowQuantity - quantity);
        }
        return true;
    }
    public void cancelOrder(){
        items.clear();
        totalAmount = 0.0;
    }
    public void listAllItems() {
        if (items.isEmpty()) {
            System.out.println("No items in this order.");
        } else {
            System.out.println("Items in order " + orderCode + ":");
            for (Map.Entry<Menu, Integer> entry : items.entrySet()) {
                Menu item = entry.getKey();
                int quantity = entry.getValue();
                System.out.println("Item: " + item.getName() + ", Quantity: " + quantity);
            }
        }
    }





    //list all


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n++++++++++++++++++++++\n");
        stringBuilder.append("Order{orderId : ").append(orderCode).append(", customerId : ").append(customer.getId()).append(", items : [");

        boolean isFirst = true;
        for (Map.Entry<Menu, Integer> entry : items.entrySet()) {
            if (!isFirst) {
                stringBuilder.append(", ");
            } else {
                isFirst = false;
            }
            Menu item = entry.getKey();
            int quantity = entry.getValue();
            stringBuilder.append("\n").append(item).append(", quantity: ").append(quantity);
        }

        stringBuilder.append("], totalAmount : ").append(totalAmount).append("}\n++++++++++++++++++++++");

        return stringBuilder.toString();
    }


}
