package domain;

import java.io.Serial;
import java.io.Serializable;
import domain.Exception.CheckException;
import domain.Exception.UnCheckException;

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

    public void setName(String name) throws CheckException {
        if (name == null || name.isBlank()) {
            throw new CheckException("Name cannot be null or blank.");
        }
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) throws CheckException {
        if (price < 0) {
            throw new CheckException("Price cannot be negative.");
        }
        this.price = price;
    }
    @Override
    public String toString() {
        return "\nMenu{" + "Code : " + code + ", Name : " + name  + ", price : " + price + "}";
    }
}
