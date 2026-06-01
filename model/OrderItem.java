package Projects.RestaurantManagement.model;

// Represents a single item line within a table's order
public class OrderItem {
    private MenuItem item;
    private int quantity;
    private String specialInstructions; // e.g., "No onions"

    // Constructor with validations to prevent invalid orders
    public OrderItem(MenuItem item, int quantity, String specialInstructions) {
        if (item == null) {
            throw new IllegalArgumentException("Menu Item in OrderItem cannot be null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Order quantity must be at least 1.");
        }
        
        this.item = item;
        this.quantity = quantity;
        this.specialInstructions = (specialInstructions == null || specialInstructions.trim().isEmpty()) 
                                   ? "None" 
                                   : specialInstructions.trim();
    }

    // Calculates the subtotal for this item line
    public double calculateSubTotal() {
        return item.getPrice() * quantity;
    }

    // Getters and Setters
    public MenuItem getItem() {
        return item;
    }

    public void setItem(MenuItem item) {
        if (item == null) {
            throw new IllegalArgumentException("MenuItem cannot be null.");
        }
        this.item = item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        }
        this.quantity = quantity;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = (specialInstructions == null || specialInstructions.trim().isEmpty()) 
                                   ? "None" 
                                   : specialInstructions.trim();
    }
}
