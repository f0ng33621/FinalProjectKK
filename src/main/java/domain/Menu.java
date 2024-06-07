package domain;

import java.io.Serializable;

public class Menu implements Serializable {
    private final String code;
    private String name;
    private double price;

    public Menu(String code, String name, double price) {
        this.code = code;
        this.name = name;
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name.isBlank()) throw new IllegalArgumentException();
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price < 0) throw new IllegalArgumentException();
        this.price = price;
    }

    @Override
    public String toString() {
        return "\nMenu{" + "Code : " + code + ", Name : " + name  + ", price : " + price + "}";
    }
}
