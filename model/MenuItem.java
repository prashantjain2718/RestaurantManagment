package Projects.RestaurantManagement.model;

// Holds details of a menu item in the restaurant catalog
public class MenuItem {
    private final int id;
    private String name;
    private double price;
    private String category; // e.g., Starters, Main Course, Desserts, Beverages

    // Constructor with basic input validation
    public MenuItem(int id, String name, double price, String category) {
        if (id <= 0) {
            throw new IllegalArgumentException("Menu Item ID must be a positive integer.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Menu Item Name cannot be empty or null.");
        }
        if (price < 0.0) {
            throw new IllegalArgumentException("Menu Item Price cannot be negative.");
        }
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Menu Item Category cannot be empty or null.");
        }
        
        this.id = id;
        this.name = name.trim();
        this.price = price;
        this.category = category.trim();
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public void setPrice(double price) {
        if (price < 0.0) {
            throw new IllegalArgumentException("Menu Item Price cannot be negative.");
        }
        this.price = price;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Menu Item Name cannot be empty or null.");
        }
        this.name = name.trim();
    }

    public void setCategory(String category) {
        if (category == null || category.trim().isEmpty()) {
            throw new IllegalArgumentException("Menu Item Category cannot be empty or null.");
        }
        this.category = category.trim();
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=₹" + String.format("%.2f", price) +
                ", category='" + category + '\'' +
                '}';
    }
}
