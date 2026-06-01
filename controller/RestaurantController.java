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
                    handleManageMenu();
                    break;
                case 5:
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
            System.out.println(
                    "• Table " + order.getTableNumber() + " | Unbilled Items Count: " + order.getOrderedItems().size());
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

    // Submenu control driver for all Menu Management tasks (Add, Edit, Remove)
    public void handleManageMenu() {
        boolean inMenuMgmt = true;
        while (inMenuMgmt) {
            byte choice = view.displayMenuManagementAndGetChoice();
            switch (choice) {
                case 1:
                    handleAddNewMenuItem();
                    break;
                case 2:
                    handleEditMenuItem();
                    break;
                case 3:
                    handleRemoveMenuItem();
                    break;
                case 4:
                    inMenuMgmt = false;
                    break;
                default:
                    view.displayErrorMessage("Unknown choice tracked in Menu Management.");
            }
        }
    }

    // Guides the operator through entering details for a new menu item
    public void handleAddNewMenuItem() {
        System.out.println("\n--- Add New Menu Item ---");
        int id = view.promptForNewMenuItemId();

        // Check if ID is unique
        if (findMenuItemById(id) != null) {
            view.displayErrorMessage("Item ID " + id + " already exists in the menu. All IDs must be unique.");
            return;
        }

        String name = view.promptForMenuItemName();
        double price = view.promptForMenuItemPrice();
        String category = view.promptForMenuItemCategory();

        try {
            MenuItem newItem = new MenuItem(id, name, price, category);
            masterMenu.add(newItem);
            System.out.println("\n✅ Successfully added new menu item!");
            System.out.println("👉 Details: ID: " + newItem.getId() + " | Name: " + newItem.getName() + " | Price: ₹" + String.format("%.2f", newItem.getPrice()) + " | Category: " + newItem.getCategory());
        } catch (IllegalArgumentException e) {
            view.displayErrorMessage("Failed to create menu item: " + e.getMessage());
        }
    }

    // Handles editing a menu item's details (Name, Price, Category)
    public void handleEditMenuItem() {
        view.displayMenu(masterMenu);
        int itemId = view.promptForMenuItemId();

        // 0 can be used to cancel or exit
        if (itemId == 0) {
            System.out.println("↩️ Edit menu action cancelled.");
            return;
        }

        MenuItem selectedItem = findMenuItemById(itemId);

        if (selectedItem == null) {
            view.displayErrorMessage("Item ID not found in master menu. Cancelled editing.");
            return;
        }

        System.out.println("\n✏️ Editing details for: " + selectedItem.getName() + " (ID: " + selectedItem.getId() + ")");

        // 1. Prompt for Name
        String newName = view.promptForNewName(selectedItem.getName());
        if (!newName.isEmpty()) {
            try {
                selectedItem.setName(newName);
            } catch (IllegalArgumentException e) {
                view.displayErrorMessage(e.getMessage());
                return;
            }
        }

        // 2. Prompt for Price
        String newPriceStr = view.promptForNewPrice(selectedItem.getPrice());
        if (!newPriceStr.isEmpty()) {
            try {
                double newPrice = Double.parseDouble(newPriceStr);
                selectedItem.setPrice(newPrice);
            } catch (IllegalArgumentException e) {
                view.displayErrorMessage("Invalid price value entered. Skipping price update.");
            }
        }

        // 3. Prompt for Category
        String newCategory = view.promptForNewCategory(selectedItem.getCategory());
        if (!newCategory.isEmpty()) {
            try {
                selectedItem.setCategory(newCategory);
            } catch (IllegalArgumentException e) {
                view.displayErrorMessage(e.getMessage());
                return;
            }
        }

        System.out.println("\n✅ Menu Item updated successfully!");
        System.out.println("👉 Updated details: ID: " + selectedItem.getId() + " | Name: " + selectedItem.getName() + " | Price: ₹" + String.format("%.2f", selectedItem.getPrice()) + " | Category: " + selectedItem.getCategory());
    }

    // Deletes an item from the master menu after checking active dining tables and confirming
    public void handleRemoveMenuItem() {
        view.displayMenu(masterMenu);
        System.out.println("\n--- Remove Menu Item ---");
        int itemId = view.promptForMenuItemId();

        if (itemId == 0) {
            System.out.println("↩️ Remove menu action cancelled.");
            return;
        }

        MenuItem selectedItem = findMenuItemById(itemId);
        if (selectedItem == null) {
            view.displayErrorMessage("Item ID not found in master menu.");
            return;
        }

        // Check if the item is present in any active/unbilled orders
        ArrayList<Byte> activeTablesWithItem = new ArrayList<>();
        for (TableOrder order : activeOrders) {
            for (OrderItem orderItem : order.getOrderedItems()) {
                if (orderItem.getItem().getId() == itemId) {
                    activeTablesWithItem.add(order.getTableNumber());
                    break;
                }
            }
        }

        // Print warning if found in active tables
        if (!activeTablesWithItem.isEmpty()) {
            System.out.println("\n⚠️ [WARNING]: This item is currently active in ongoing dine-in orders!");
            System.out.print("👉 Active on Table(s): ");
            for (int i = 0; i < activeTablesWithItem.size(); i++) {
                System.out.print(activeTablesWithItem.get(i) + (i < activeTablesWithItem.size() - 1 ? ", " : ""));
            }
            System.out.println("\nDeleting it will NOT affect their currently loaded bills (as they reference the item in memory),");
            System.out.println("but waiters won't be able to add this item to any new or existing tables.");
        }

        boolean confirm = view.promptForDeleteConfirmation(selectedItem.getName());
        if (confirm) {
            masterMenu.remove(selectedItem);
            System.out.println("\n🗑️ '" + selectedItem.getName() + "' (ID: " + selectedItem.getId() + ") has been permanently removed from the master menu.");
        } else {
            System.out.println("↩️ Removal cancelled. Item remains intact.");
        }
    }

    // Pre-populates catalog with baseline dishes
    private void loadInitialMenu() {
        // Starters
        masterMenu.add(new MenuItem(101, "Paneer Tikka", 240.00, "Starters"));
        masterMenu.add(new MenuItem(102, "Spring Rolls", 180.00, "Starters"));
        masterMenu.add(new MenuItem(103, "Chilli Paneer", 220.00, "Starters"));
        masterMenu.add(new MenuItem(104, "French Fries", 120.00, "Starters"));

        // Main Course
        masterMenu.add(new MenuItem(201, "Dal Makhani", 260.00, "Main Course"));
        masterMenu.add(new MenuItem(202, "Butter Roti", 30.00, "Main Course"));
        masterMenu.add(new MenuItem(203, "Kadhai Paneer", 290.00, "Main Course"));
        masterMenu.add(new MenuItem(204, "Garlic Naan", 60.00, "Main Course"));
        masterMenu.add(new MenuItem(205, "Veg Biryani", 240.00, "Main Course"));
        masterMenu.add(new MenuItem(206, "Butter Chicken", 340.00, "Main Course"));

        // Desserts & Beverages
        masterMenu.add(new MenuItem(301, "Choco Lava Cake", 150.00, "Desserts"));
        masterMenu.add(new MenuItem(302, "Gulab Jamun", 80.00, "Desserts"));
        masterMenu.add(new MenuItem(303, "Iced Americano", 120.00, "Beverages"));
        masterMenu.add(new MenuItem(304, "Fresh Lime Soda", 90.00, "Beverages"));
        masterMenu.add(new MenuItem(305, "Masala Chai", 50.00, "Beverages"));
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