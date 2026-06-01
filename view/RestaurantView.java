package Projects.RestaurantManagement.view;

import Projects.RestaurantManagement.model.MenuItem;
import Projects.RestaurantManagement.model.OrderItem;
import Projects.RestaurantManagement.model.TableOrder;

import java.util.ArrayList;
import java.util.Scanner;

// Handles console input reading and menu/bill rendering
public class RestaurantView {
    private final Scanner sc = new Scanner(System.in);

    // Displays the main dashboard menu and reads selection (1-4)
    public byte displayMainMenuAndGetChoice() {
        while (true) {
            try {
                System.out.println("\n========== RESTAURANT MANAGEMENT SYSTEM ==========");
                System.out.println("1. Select Table & Order");
                System.out.println("2. View Active Orders");
                System.out.println("3. Checkout / Generate Bill");
                System.out.println("4. Exit");
                System.out.print("👉 Enter your choice (1-4): ");

                String input = sc.nextLine().trim();
                byte choice = Byte.parseByte(input);
                
                if (choice >= 1 && choice <= 4) {
                    return choice;
                }
                System.out.println("❌ Invalid choice! Please select an option between 1 and 4.");
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter a single digit number.");
            }
        }
    }

    // Prompts waiter for table number (1-50)
    public byte promptForTableNumber() {
        while (true) {
            try {
                System.out.print("🔢 Enter Table Number (1-50): ");
                String input = sc.nextLine().trim();
                byte tableNumber = Byte.parseByte(input);
                
                if (tableNumber > 0 && tableNumber <= 50) {
                    return tableNumber;
                }
                System.out.println("❌ Invalid table! We only have tables 1 to 50.");
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input! Please enter a valid small whole number.");
            }
        }
    }

    // Prompts waiter for food item ID (or 0 to cancel/exit)
    public int promptForMenuItemId() {
        while (true) {
            try {
                System.out.print("🍔 Enter Menu Item ID to add (or 0 to finish/cancel): ");
                String input = sc.nextLine().trim();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid ID! Please enter a valid integer value.");
            }
        }
    }

    // Prompts for item quantity
    public byte promptForQuantity() {
        while (true) {
            try {
                System.out.print("📦 Enter Quantity: ");
                String input = sc.nextLine().trim();
                byte qty = Byte.parseByte(input);
                
                if (qty > 0) {
                    return qty;
                }
                System.out.println("❌ Quantity must be at least 1.");
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid Input! Enter a small whole number.");
            }
        }
    }

    // Gathers special preparation details
    public String promptForInstructions() {
        System.out.print("✏️ Special Instructions (Press Enter to skip): ");
        String instructions = sc.nextLine().trim();
        return instructions.isEmpty() ? "None" : instructions;
    }

    // Prompts waiter if they want to append more items
    public byte promptToContinue() {
        while (true) {
            try {
                String input = sc.nextLine().trim();
                byte choice = Byte.parseByte(input);
                
                if (choice == 0 || choice == 1) {
                    return choice;
                }
                System.out.print("❌ Invalid input. Enter 1 for Yes or 0 for No: ");
            } catch (NumberFormatException e) {
                System.out.print("❌ Invalid input. Please enter 1 or 0: ");
            }
        }
    }

    // Prompts for split bill count
    public byte promptForSplitCount() {
        while (true) {
            try {
                System.out.print("👥 Split bill among how many people? (Enter 1 if no split): ");
                String input = sc.nextLine().trim();
                byte splitCount = Byte.parseByte(input);
                
                if (splitCount > 0) {
                    return splitCount;
                }
                System.out.println("❌ Count must be at least 1 person.");
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid Input! Enter a valid small number.");
            }
        }
    }

    // Displays the current menu to the user
    public void displayMenu(ArrayList<MenuItem> menu) {
        if (menu == null || menu.isEmpty()) {
            System.out.println("⚠️ The restaurant menu is currently empty!");
            return;
        }

        System.out.println("\n================= DIGITAL MENU ================= ");
        System.out.printf("%-6s | %-25s | %-10s\n", "ID", "Item Name", "Price");
        System.out.println("-------------------------------------------------");

        String currentCategory = "";
        for (MenuItem item : menu) {
            if (!item.getCategory().equalsIgnoreCase(currentCategory)) {
                currentCategory = item.getCategory();
                System.out.printf("\n--- %s ---\n", currentCategory.toUpperCase());
            }
            System.out.printf("%-6d | %-25s | ₹%-10.2f\n", item.getId(), item.getName(), item.getPrice());
        }
        System.out.println("=================================================");
    }

    // Prints the formatted sales receipt and splitting amounts
    public void printReceipt(TableOrder order, double taxRate, double finalTotal, double splitAmount) {
        System.out.println("\n=================================================");
        System.out.printf("%33s\n", "THE CRISPY FORK RESTAURANT");
        System.out.printf("%28s %d\n", "TABLE NO:", order.getTableNumber());
        System.out.println("=================================================");
        System.out.printf("%-20s %-5s %-10s %-10s\n", "Item Name", "Qty", "Price", "Total");
        System.out.println("-------------------------------------------------");

        for (OrderItem orderedItem : order.getOrderedItems()) {
            System.out.printf("%-20s %-5d ₹%-9.2f ₹%-10.2f\n",
                    orderedItem.getItem().getName(),
                    orderedItem.getQuantity(),
                    orderedItem.getItem().getPrice(),
                    orderedItem.calculateSubTotal()
            );

            if (!orderedItem.getSpecialInstructions().equalsIgnoreCase("None")) {
                System.out.printf("  ↳ *Notes: %s*\n", orderedItem.getSpecialInstructions());
            }
        }

        System.out.println("-------------------------------------------------");
        System.out.printf("%-38s ₹%-10.2f\n", "Sub-Total:", order.calculateTotal());
        System.out.printf("%-38s ₹%-10.2f\n", "Tax (" + (taxRate * 100) + "%):", order.calculateTax(taxRate));
        System.out.println("-------------------------------------------------");
        System.out.printf("**%-36s ₹%-10.2f**\n", "GRAND TOTAL:", finalTotal);
        System.out.println("=================================================");

        if (splitAmount < finalTotal) {
            System.out.printf("Split Amount per Person: ₹%.2f\n", splitAmount);
            System.out.println("=================================================");
        }
        System.out.printf("%31s\n", "THANK YOU! VISIT AGAIN");
        System.out.println("=================================================\n");
    }

    // Prints standard system errors
    public void displayErrorMessage(String message) {
        System.out.println("\n🔥 [SYSTEM ERROR]: " + message);
    }
}
