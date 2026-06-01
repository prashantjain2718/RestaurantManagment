package Projects.RestaurantManagement;

import Projects.RestaurantManagement.view.RestaurantView;
import Projects.RestaurantManagement.controller.RestaurantController;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 Initializing Digital Menu & Billing System...");

        // Step 1: Initialize the View (Handles all console sc/Scanner UI operations)
        RestaurantView view = new RestaurantView();

        // Step 2: Initialize the Controller and inject the View into it
        // This bridges the UI flow and your underlying TableOrder/MenuItem Models
        RestaurantController controller = new RestaurantController(view);

        System.out.println("✅ System Bootstrapped Successfully.");

        // Step 3: Launch the primary operational loop application driver
        controller.startApplication();
    }
}