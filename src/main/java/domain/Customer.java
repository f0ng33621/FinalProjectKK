package domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Customer implements Serializable {
    private final String id;
    private String name;
    private String phoneNumber;

    public Customer(String id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name.isBlank()) throw new IllegalArgumentException();
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        //อาจแก้ split -
        if (phoneNumber.isBlank()) throw new IllegalArgumentException();
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String toString() {
        return "\n++++++++++++++++++++++" +
                "\nCustomer" +
                "\nid : " + id +
                "\nname : " + name +
                "\nphoneNumber : " + phoneNumber +
                "\n++++++++++++++++++++++";
    }
}
