package domain;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import domain.Exception.CheckException;
import domain.Exception.UnCheckException;

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

    public void setName(String name) throws CheckException {
        if (name.isBlank()) throw new CheckException("Name cannot be blank.");
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) throws CheckException {
        if (phoneNumber.isBlank()) throw new CheckException("Phone number cannot be blank.");
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
