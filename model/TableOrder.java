package Projects.RestaurantManagement.model;

import java.util.ArrayList;

// Represents the active dining session and order status for a restaurant table
public class TableOrder {
    private final byte tableNumber; // Table ID (1 to 50)
    private final ArrayList<OrderItem> orderedItems; // List of items ordered so far
    private boolean isPaid; // Settlement status

    // Constructor with table number validation
    public TableOrder(byte tableNumber) {
        if (tableNumber <= 0 || tableNumber > 50) {
            throw new IllegalArgumentException("Table number must be between 1 and 50.");
        }
        this.tableNumber = tableNumber;
        this.orderedItems = new ArrayList<>(); // Prevent null pointer issues
        this.isPaid = false;
    }

    // Appends a new item to this table's order
    public void addItem(OrderItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot add a null OrderItem to the order.");
        }
        orderedItems.add(item);
    }

    // Calculates the total cost of all items (before tax)
    public double calculateTotal() {
        double total = 0.0;
        for (OrderItem orderedItem : orderedItems) {
            total += orderedItem.calculateSubTotal();
        }
        return total;
    }

    // Computes tax on the order subtotal
    public double calculateTax(double taxRate) {
        if (taxRate < 0.0) {
            throw new IllegalArgumentException("Tax rate cannot be negative.");
        }
        return calculateTotal() * taxRate;
    }

    // Splits the bill (subtotal + tax) among the given number of people
    public double calculateSplitBill(int people, double taxRate) {
        if (people <= 0) {
            throw new IllegalArgumentException("Number of people for splitting must be 1 or more.");
        }
        if (taxRate < 0.0) {
            throw new IllegalArgumentException("Tax rate cannot be negative.");
        }

        double baseTotal = calculateTotal();
        double taxAmount = baseTotal * taxRate;

        return (baseTotal + taxAmount) / people;
    }

    // Getters and Setters
    public byte getTableNumber() {
        return tableNumber;
    }

    public ArrayList<OrderItem> getOrderedItems() {
        return orderedItems;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        this.isPaid = paid;
    }
}