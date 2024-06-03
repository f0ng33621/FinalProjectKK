package ui;

import domain.Menu;
import repository.memory.InMemoryCustomerRepository;
import repository.memory.InMemoryMenuRepository;
import repository.memory.InMemoryOrderRepository;
import service.RestaurantService;
import java.util.Scanner;
import domain.Customer;
import domain.Order;
import java.util.Collection;
public class UiRestaurant {
    private final RestaurantService service;

    public UiRestaurant(boolean useDatabase) {
        if (!useDatabase){
            service = new RestaurantService(new InMemoryCustomerRepository(),new InMemoryMenuRepository(),new InMemoryOrderRepository());
        } else {
            service = new RestaurantService(new InMemoryCustomerRepository(),new InMemoryMenuRepository(),new InMemoryOrderRepository());
        }
    }

    public void start(){
        Scanner scanner = new Scanner(System.in);
        boolean running = true;


        while (running) {
            System.out.println("\nRestaurant Management System");
            System.out.println("1. Register Customer");
            System.out.println("2. Login Customer");
            System.out.println("4. Find Customer");
            System.out.println("5. Add Menu");
            System.out.println("6. Rename Menu");
            System.out.println("7. Reprice Menu");
            System.out.println("8. Find Menu");
            System.out.println("9. Remove Menu");
            System.out.println("11. Add Item to Order");
            System.out.println("12. Remove Item from Order");
            System.out.println("13. Cancel Order");
            System.out.println("14. List Items in Order");
            System.out.println("15. List All Orders for Customer");
            System.out.println("16. List All Customers");
            System.out.println("17. List All Menus");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter customer name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter phone number: ");
                    String phone = scanner.nextLine();
                    Customer customer = service.registerCustomer(name, phone);
                    System.out.println("Registered Customer: " + customer);
                }
                case 2 -> {
                    System.out.print("Enter customer ID: ");
                    String customerId = scanner.nextLine();
                    Customer user = service.findCustomer(customerId);
                    if (user == null) {
                        System.out.println("Customer not found.");
                        break;
                    }
                    String loginchoice = """
                1.Rename
                2.Change Phone Number
                3.List All Menus
                4.Add Order
                5.List All your Orders
                """;
                    boolean loginRunning = true;
                    while (loginRunning) {
                        System.out.println("\nLogged in as: " + user);
                        System.out.println(loginchoice);
                        System.out.print("Choose an option: ");
                        int choiceLogin = scanner.nextInt();
                        scanner.nextLine();
                        switch (choiceLogin){
                            case 1 -> {
                                System.out.print("Enter new customer name: ");
                                String newName = scanner.nextLine();
                                Customer updatedCustomer = service.renameCustomer(customerId, newName);
                                System.out.println("Updated Customer: " + updatedCustomer);
                            }
                            case 2 -> {
                                System.out.print("Enter new phone number: ");
                                String newPhone = scanner.nextLine();
                                Customer updatedCustomer = service.changePhoneNumberCustomer(customerId, newPhone);
                                System.out.println("Updated Customer: " + updatedCustomer);
                            }
                            case 3 -> {
                                Collection<Menu> menus = service.allAccounts();
                                System.out.println("All menus:");
                                for (Menu menu : menus) {
                                    System.out.println(menu);
                                }
                            }
                            case 4 -> {
                                Order order = service.addOrder(customerId);
                                System.out.println("Created Order: " + order);
                            }
                            case 5 -> {
                                Collection<Order> orders = service.listAllOrderOwnedBy(customerId);
                                System.out.println("Orders for customer " + customerId + ":");
                                for (Order order : orders) {
                                    System.out.println(order);
                                }
                            }
                            case 0 -> {
                                loginRunning = false;
                                System.out.println("Logged out.");
                            }
                            default -> System.out.println("Invalid choice. Please try again.");
                        }


                    }
                }
                case 3 -> {
                    System.out.print("Enter customer ID: ");
                    String customerId = scanner.nextLine();
                    System.out.print("Enter new phone number: ");
                    String newPhone = scanner.nextLine();
                    Customer customer = service.changePhoneNumberCustomer(customerId, newPhone);
                    System.out.println("Updated Customer: " + customer);
                }
                case 4 -> {
                    System.out.print("Enter customer ID: ");
                    String customerId = scanner.nextLine();
                    Customer customer = service.findCustomer(customerId);
                    System.out.println("Found Customer: " + customer);
                }
                case 5 -> {
                    System.out.print("Enter menu name: ");
                    String menuName = scanner.nextLine();
                    System.out.print("Enter price: ");
                    double price = scanner.nextDouble();
                    Menu menu = service.addMenu(menuName, price);
                    System.out.println("Added Menu: " + menu);
                }
                case 6 -> {
                    System.out.print("Enter menu code: ");
                    String menuCode = scanner.nextLine();
                    System.out.print("Enter new menu name: ");
                    String newName = scanner.nextLine();
                    Menu menu = service.renameMenu(menuCode, newName);
                    System.out.println("Updated Menu: " + menu);
                }
                case 7 -> {
                    System.out.print("Enter menu code: ");
                    String menuCode = scanner.nextLine();
                    System.out.print("Enter new price: ");
                    double newPrice = scanner.nextDouble();
                    Menu menu = service.repriceMenu(menuCode, newPrice);
                    System.out.println("Updated Menu: " + menu);
                }
                case 8 -> {
                    System.out.print("Enter menu code: ");
                    String menuCode = scanner.nextLine();
                    Menu menu = service.findMenu(menuCode);
                    System.out.println("Found Menu: " + menu);
                }
                case 9 -> {
                    System.out.print("Enter menu code: ");
                    String menuCode = scanner.nextLine();
                    boolean removed = service.removeMenu(menuCode);
                }
                case 10 -> {
                    System.out.print("Enter customer ID: ");
                    String customerId = scanner.nextLine();
                    Order order = service.addOrder(customerId);
                    System.out.println("Created Order: " + order);
                }
                case 11 -> {
                    System.out.print("Enter order code: ");
                    String orderCode = scanner.nextLine();
                    System.out.print("Enter menu code: ");
                    String menuCode = scanner.nextLine();
                    System.out.print("Enter quantity: ");
                    int quantity = scanner.nextInt();
                    Order order = service.addItem(orderCode, menuCode, quantity);
                    System.out.println("Updated Order: " + order);
                }
                case 12 -> {
                    System.out.print("Enter order code: ");
                    String orderCode = scanner.nextLine();
                    System.out.print("Enter menu code: ");
                    String menuCode = scanner.nextLine();
                    System.out.print("Enter quantity to remove: ");
                    int quantity = scanner.nextInt();
                    Order order = service.removeItem(orderCode, menuCode, quantity);
                    System.out.println("Updated Order: " + order);
                }
                case 13 -> {
                    System.out.print("Enter order code: ");
                    String orderCode = scanner.nextLine();
                    Order order = service.cancleOrder(orderCode);
                    System.out.println("Cancelled Order: " + order);
                }
                case 14 -> {
                    System.out.print("Enter order code: ");
                    String orderCode = scanner.nextLine();
                    service.listItem(orderCode);
                }
                case 15 -> {
                    System.out.print("Enter customer ID: ");
                    String customerId = scanner.nextLine();
                    Collection<Order> orders = service.listAllOrderOwnedBy(customerId);
                    System.out.println("Orders for customer " + customerId + ":");
                    for (Order order : orders) {
                        System.out.println(order);
                    }
                }
                case 16 -> {
                    Collection<Customer> customers = service.allCustomer();
                    System.out.println("All customers:");
                    for (Customer customer : customers) {
                        System.out.println(customer);
                    }
                }
                case 17 -> {
                    Collection<Menu> menus = service.allAccounts();
                    System.out.println("All menus:");
                    for (Menu menu : menus) {
                        System.out.println(menu);
                    }
                }
                case 0 -> {
                    running = false;
                    System.out.println("Exiting...");
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
        scanner.close();
    }
}
