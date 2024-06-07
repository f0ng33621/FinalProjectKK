package ui;

import domain.Menu;
import repository.database.*;
import repository.file.FileCustomerRepository;
import repository.file.FileMenuRepository;
import repository.file.FileOrderRepository;
import repository.memory.InMemoryCustomerRepository;
import repository.memory.InMemoryMenuRepository;
import repository.memory.InMemoryOrderRepository;
import service.RestaurantService;
import java.util.Scanner;
import domain.Customer;
import domain.Order;
import java.io.Console;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UiRestaurant {

    private final RestaurantService service;

    public UiRestaurant() {
        Scanner chooseKeep = new Scanner(System.in);
        boolean validChoice = false;
        RestaurantService tempService = null;

        while (!validChoice) {
            System.out.println("""
                    Your Select
                    1 : InMemory
                    2 : File
                    3 : Database
                    4 : DatabaseOnline
                    """);
            int choice = chooseKeep.nextInt();

            switch (choice) {
                case 1:
                    tempService = new RestaurantService(new InMemoryCustomerRepository(), new InMemoryMenuRepository(), new InMemoryOrderRepository());
                    validChoice = true;
                    break;
                case 2:
                    tempService = new RestaurantService(new FileCustomerRepository(), new FileMenuRepository(), new FileOrderRepository());
                    validChoice = true;
                    break;
                case 3:
                    tempService = new RestaurantService(new DatabaseCustomerRepository(), new DatabaseMenuRepository(), new DatabaseOrderRepository());
                    validChoice = true;
                    break;
                case 4:
                    tempService = new RestaurantService(new repository.database.databaseonline.DatabaseCustomerRepository(), new repository.database.databaseonline.DatabaseMenuRepository(), new repository.database.databaseonline.DatabaseOrderRepository());
                    validChoice = true;
                    break;
                default:
                    System.out.println("Invalid choice. Please choose again.");
                    break;
            }
        }

        if (tempService == null) {
            throw new RuntimeException("Invalid choice. Service not initialized.");
        }

        this.service = tempService;
    }

    public static boolean isValidPhoneNumber(String phone) {
        String regex = "^\\+?[0-9]{10,15}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    public void start() {
        Console console = System.console();
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        while (running) {

            String description = """
                    Restaurant Management System
                    1. Register Customer
                    2. Login Customer
                    3. List All Menus
                    4. Find Menu
                    5. Exit
                    Choose an option:
                    """;

            System.out.println(description);
            //check if input is int
            int choice = 0;
            boolean validInput = false;
            while (!validInput) {
                System.out.println("Please enter a number between 1 and 5:");
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    scanner.nextLine();
                    if (choice >= 1 && choice <= 5) {
                        validInput = true;
                    } else {
                        System.out.print("Invalid choice. Please enter a number between 1 and 5:");
                    }
                } else {
                    scanner.nextLine();
                }
            }
            switch (choice) {
                case 1 -> {
                    System.out.print("Enter customer name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter phone number: ");
                    String phone = scanner.nextLine();

                    if (isValidPhoneNumber(phone)) {
                        Customer customer = service.registerCustomer(name, phone);
                        System.out.println("Registered Customer: " + customer);
                    } else {
                        System.out.println("Invalid phone number. Please enter a valid phone number.");
                    }
                }
                case 2 -> {
                    System.out.print("Enter customer ID: ");
                    String customerId = scanner.nextLine();
                    if (customerId.equals("C0")) {
                        boolean isAuthenticated = false;
                        int attempts = 3;
                        String correctPassword = "admin";
                        boolean authenticationAttempted = false; // Add this flag

                        while (attempts > 0 && !isAuthenticated) {
                            if (console == null) {
                                // Handle no console case
                                System.out.println("There is no console. Please run on Jar");
                                authenticationAttempted = true; // Mark that authentication was attempted
                                break;
                            } else {
                                char[] regPasswordArray = console.readPassword("Enter password: ");
                                String regPassword = new String(regPasswordArray);
                                if (!regPassword.equals(correctPassword)) {
                                    System.out.println("Incorrect password. Please try again.");
                                    attempts--;
                                } else {
                                    isAuthenticated = true;
                                }
                            }
                        }

                        if (!isAuthenticated && authenticationAttempted) {
                            // Exit if authentication was attempted but failed due to no console
                            break;
                        }

                        if (!isAuthenticated) {
                            System.out.println("Too many failed attempts. Exiting.");
                            break;
                        }

                        boolean adminRunning = true;
                        String adminDescription = """
                                1. Find Customer
                                2. Add Menu
                                3. Rename Menu
                                4. Reprice Menu
                                5. Remove Menu
                                6. List All Orders
                                7. List All Customers
                                0. Logout
                                """;
                        while (adminRunning) {
                            System.out.println(adminDescription);
                            int adminChoice = scanner.nextInt();
                            scanner.nextLine();
                            switch (adminChoice) {
                                case 1 -> {
                                    System.out.print("Enter customer ID: ");
                                    String searchCustomerId = scanner.nextLine();
                                    Customer customer = service.findCustomer(searchCustomerId);
                                    System.out.println("Found Customer: " + customer);
                                }
                                case 2 -> {
                                    System.out.print("Enter menu name: ");
                                    String menuName = scanner.nextLine();
                                    System.out.print("Enter price: ");
                                    double price = scanner.nextDouble();
                                    Menu menu = service.addMenu(menuName, price);
                                    System.out.println("Added Menu: " + menu);
                                }
                                case 3 -> {
                                    System.out.print("Enter menu code: ");
                                    String menuCode = scanner.nextLine();
                                    System.out.print("Enter new menu name: ");
                                    String newName = scanner.nextLine();
                                    Menu menu = service.renameMenu(menuCode, newName);
                                    System.out.println("Updated Menu: " + menu);
                                }
                                case 4 -> {
                                    System.out.print("Enter menu code: ");
                                    String menuCode = scanner.nextLine();
                                    System.out.print("Enter new price: ");
                                    double newPrice = scanner.nextDouble();
                                    Menu menu = service.repriceMenu(menuCode, newPrice);
                                    System.out.println("Updated Menu: " + menu);
                                }
                                case 5 -> {
                                    System.out.print("Enter menu code: ");
                                    String menuCode = scanner.nextLine();
                                    boolean removed = service.removeMenu(menuCode);
                                    if (removed) {
                                        System.out.println("Menu item removed successfully.");
                                    } else {
                                        System.out.println("Menu item not found.");
                                    }
                                }
                                case 6 -> {
                                    Collection<Order> orders = service.listAllOrder();
                                    System.out.println("All Orders:");
                                    for (Order order : orders) {
                                        System.out.println(order);
                                    }
                                }
                                case 7 -> {
                                    Collection<Customer> customers = service.allCustomer();
                                    System.out.println("All customers:");
                                    for (Customer customer : customers) {
                                        System.out.println(customer);
                                    }
                                }

                                case 0 -> {
                                    adminRunning = false;
                                    System.out.println("Logged out.");
                                }
                                default ->
                                    System.out.println("Invalid choice. Please try again.");
                            }
                        }
                    } else {
                        Customer user = service.findCustomer(customerId);
                        if (user == null) {
                            System.out.println("Customer not found.");
                            break;
                        }
                        String loginChoice = """
                        1. Rename
                        2. Change Phone Number
                        3. List All Menus
                        4. Add Order
                        5. List All your Orders
                        0. Logout
                        """;
                        boolean loginRunning = true;
                        while (loginRunning) {
                            System.out.println("\nLogged in as: " + user);
                            System.out.println(loginChoice);
                            System.out.print("Choose an option: ");
                            int choiceLogin = scanner.nextInt();
                            scanner.nextLine();
                            switch (choiceLogin) {
                                case 1 -> {
                                    System.out.print("Enter new customer name: ");
                                    String newName = scanner.nextLine();
                                    Customer updatedCustomer = service.renameCustomer(customerId, newName);
                                    System.out.println("Updated Customer: " + updatedCustomer);
                                }
                                case 2 -> {
                                    System.out.print("Enter new phone number: ");
                                    String newPhone = scanner.nextLine();
                                    if (isValidPhoneNumber(newPhone)) {
                                        Customer updatedCustomer = service.changePhoneNumberCustomer(customerId, newPhone);
                                        System.out.println("Updated Customer: " + updatedCustomer);
                                    } else {
                                        System.out.println("Invalid phone number. Please enter a valid phone number.");
                                    }
                                }
                                case 3 -> {
                                    Collection<Menu> menus = service.allMenu();
                                    System.out.println("All menus:");
                                    for (Menu menu : menus) {
                                        System.out.println(menu);
                                    }
                                }
                                case 4 -> {
                                    Order order = service.addOrder(customerId);
                                    System.out.println("Created Order: " + order);
                                    System.out.println("Your orderCode : " + order.getOrderCode());
                                    String userOrderCode = order.getOrderCode();
                                    boolean orderRunning = true;
                                    String orderDescription = """
                                            1. Add menu to your order
                                            2. Remove menu from your order
                                            3. List all menu you have ordered
                                            4. Submit
                                            5. Cancel
                                            0. Exit Order Menu
                                            """;
                                    while (orderRunning) {
                                        System.out.println(orderDescription);
                                        int orderChoice = scanner.nextInt();
                                        scanner.nextLine();
                                        switch (orderChoice) {
                                            case 1 -> {
                                                System.out.print("Enter menu code: ");
                                                String menuCode = scanner.nextLine();
                                                System.out.print("Enter quantity: ");
                                                int quantity = scanner.nextInt();
                                                scanner.nextLine();
                                                order = service.addItem(userOrderCode, menuCode, quantity);
                                                System.out.println("Updated Order: " + order);
                                            }
                                            case 2 -> {
                                                System.out.print("Enter menu code: ");
                                                String menuCode = scanner.nextLine();
                                                System.out.print("Enter quantity to remove: ");
                                                int quantity = scanner.nextInt();
                                                scanner.nextLine();
                                                order = service.removeItem(userOrderCode, menuCode, quantity);
                                                System.out.println("Updated Order: " + order);
                                            }
                                            case 3 -> {
                                                System.out.println("Items in your order:");
                                                service.listItem(userOrderCode);
                                            }
                                            case 4 -> {
                                                System.out.println("Order submitted: " + order);
                                                orderRunning = false;
                                            }
                                            case 5 -> {
                                                order = service.cancleOrder(userOrderCode);
                                                System.out.println("Order cancelled: " + order);
                                                orderRunning = false;
                                            }
                                            case 0 ->
                                                orderRunning = false;
                                            default ->
                                                System.out.println("Invalid choice. Please try again.");
                                        }
                                    }
                                }
                                case 5 -> {
                                    Collection<Order> orders = service.listAllOrderOwnedBy(customerId);
                                    System.out.println("Orders for customer " + customerId + ":");
                                    for (Order order : orders) {
                                        System.out.println(order);
                                    }
                                }
                                case 6 -> {
                                    Collection<Order> orders = service.listAllOrder();
                                    System.out.println("All Orders:");
                                    for (Order order : orders) {
                                        System.out.println(order);
                                    }
                                }
                                case 0 -> {
                                    loginRunning = false;
                                    System.out.println("Logged out.");
                                }
                                default ->
                                    System.out.println("Invalid choice. Please try again.");
                            }
                        }
                    }
                }
                case 3 -> {
                    Collection<Menu> menus = service.allMenu();
                    System.out.println("All menus:");
                    for (Menu menu : menus) {
                        System.out.println(menu);
                    }
                }
                case 4 -> {
                    System.out.print("Enter menu code: ");
                    String menuCode = scanner.nextLine();
                    Menu menu = service.findMenu(menuCode);
                    System.out.println("Found Menu: " + menu);
                }

                case 5 -> {
                    running = false;
                    System.out.println("Exiting...");
                }
                default ->
                    System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }
}
