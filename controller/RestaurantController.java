package Projects.RestaurantManagement.controller;

import Projects.RestaurantManagement.model.MenuItem;
import Projects.RestaurantManagement.model.OrderItem;
import Projects.RestaurantManagement.model.TableOrder;
import Projects.RestaurantManagement.view.RestaurantView;

import java.util.ArrayList;

// Directs the system's execution flow and bridges models and UI views
public class RestaurantController {
    private final ArrayList<MenuItem> masterMenu; // Food menu item catalog
    private final ArrayList<TableOrder> activeOrders; // Ongoing dine-in table transactions
    private final RestaurantView view; // Attached console view
    
    private static final double TAX_RATE = 0.05; // 5% GST

    // Constructor linking view and loading initial menu items
    public RestaurantController(RestaurantView view) {
        this.view = view;
        this.masterMenu = new ArrayList<>();
        this.activeOrders = new ArrayList<>();
        loadInitialMenu(); // Populates food database
    }

    // Command loop that drives the main dashboard
    public void startApplication() {
        boolean running = true;
        while (running) {
            byte choice = view.displayMainMenuAndGetChoice();

            switch (choice) {
                case 1:
                    handleNewOrder();
                    break;
                case 2:
                    viewActiveOrders();
                    break;
                case 3:
                    handleCheckout();
                    break;
                case 4:
                    System.out.println("Exiting system. Have a great day!");
                    running = false;
                    break;
                default:
                    view.displayErrorMessage("Unknown choice tracked.");
            }
        }
    }

    // Handles table selection, ordering menu items, and custom instructions
    public void handleNewOrder() {
        byte tableNum = view.promptForTableNumber();

        TableOrder existingOrder = findActiveOrderByTable(tableNum);
        TableOrder currentOrder;

        if (existingOrder != null) {
            System.out.println("🔄 Table " + tableNum + " has an ongoing session. Appending items...");
            currentOrder = existingOrder;
        } else {
            currentOrder = new TableOrder(tableNum);
            activeOrders.add(currentOrder);
        }

        boolean addingItems = true;
        while (addingItems) {
            view.displayMenu(masterMenu);
            int itemId = view.promptForMenuItemId();
            
            // Allow waiter to stop or cancel order flow
            if (itemId == 0) {
                if (currentOrder.getOrderedItems().isEmpty()) {
                    activeOrders.remove(currentOrder);
                    System.out.println("↩️ Order cancelled. Empty session for Table " + tableNum + " discarded.");
                } else {
                    System.out.println("↩️ Stopped adding items. Table " + tableNum + " current order saved.");
                }
                break;
            }

            MenuItem selectedItem = findMenuItemById(itemId);

            if (selectedItem == null) {
                view.displayErrorMessage("Item ID not found in master menu. Please retry.");
                continue;
            }

            byte quantity = view.promptForQuantity();
            String notes = view.promptForInstructions();

            OrderItem lineItem = new OrderItem(selectedItem, quantity, notes);
            currentOrder.addItem(lineItem);
            System.out.println("✅ " + selectedItem.getName() + " (x" + quantity + ") added to Table " + tableNum);

            System.out.print("\nDo you want to add more items to this table? (1 for Yes, 0 for No): ");
            byte continueChoice = view.promptToContinue();

            if (continueChoice != 1) {
                addingItems = false;
            }
        }
    }

    // Lists all active dining tables
    public void viewActiveOrders() {
        if (activeOrders.isEmpty()) {
            System.out.println("ℹ️ No active tables are dining right now.");
            return;
        }
        System.out.println("\n--- Current Active Tables Tracking ---");
        for (TableOrder order : activeOrders) {
            System.out.println("• Table " + order.getTableNumber() + " | Unbilled Items Count: " + order.getOrderedItems().size());
        }
    }

    // Handles billing, GST calculations, split-bills, and settles payment
    public void handleCheckout() {
        if (activeOrders.isEmpty()) {
            view.displayErrorMessage("No active orders found to check out!");
            return;
        }

        byte tableNum = view.promptForTableNumber();
        TableOrder order = findActiveOrderByTable(tableNum);

        if (order == null) {
            view.displayErrorMessage("Table " + tableNum + " does not have an active order.");
            return;
        }

        if (order.getOrderedItems().isEmpty()) {
            view.displayErrorMessage("Cannot process bill. Table order contains no items.");
            activeOrders.remove(order);
            return;
        }

        byte splitPeople = view.promptForSplitCount();

        double subTotal = order.calculateTotal();
        double totalTax = order.calculateTax(TAX_RATE);
        double grandTotal = subTotal + totalTax;

        double splitPricePerPerson;
        try {
            splitPricePerPerson = order.calculateSplitBill(splitPeople, TAX_RATE);
        } catch (IllegalArgumentException ex) {
            view.displayErrorMessage(ex.getMessage() + " Defaulting split display to 1 person.");
            splitPricePerPerson = grandTotal;
        }

        view.printReceipt(order, TAX_RATE, grandTotal, splitPricePerPerson);

        activeOrders.remove(order);
        System.out.println("💳 Payment processed. Table " + tableNum + " is now free.");
    }

    // Pre-populates catalog with baseline dishes
    private void loadInitialMenu() {
        // Starters
        masterMenu.add(new MenuItem(101, "Paneer Tikka", 240.00, "Starters"));
        masterMenu.add(new MenuItem(102, "Spring Rolls", 180.00, "Starters"));

        // Main Course
        masterMenu.add(new MenuItem(201, "Dal Makhani", 260.00, "Main Course"));
        masterMenu.add(new MenuItem(202, "Butter Roti", 30.00, "Main Course"));
        masterMenu.add(new MenuItem(203, "Kadhai Paneer", 290.00, "Main Course"));

        // Desserts & Beverages
        masterMenu.add(new MenuItem(301, "Choco Lava Cake", 150.00, "Desserts"));
        masterMenu.add(new MenuItem(302, "Iced Americano", 120.00, "Beverages"));
    }

    // Finds food item matching a Menu ID
    private MenuItem findMenuItemById(int id) {
        for (MenuItem item : masterMenu) {
            if (item.getId() == id) {
                return item;
            }
        }
        return null;
    }

    // Finds running dining order matching a Table number
    private TableOrder findActiveOrderByTable(byte tableNumber) {
        for (TableOrder order : activeOrders) {
            if (order.getTableNumber() == tableNumber) {
                return order;
            }
        }
        return null;
    }
}