package com.sg.flooringmastery.ui;

import com.sg.flooringmastery.model.Order;
import com.sg.flooringmastery.model.Product;
import com.sg.flooringmastery.model.Tax;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FlooringMasterView {
    private UserIO io;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    private static final Set<String> VALID_STATES = new HashSet<>(Arrays.asList(
            "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
            "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
            "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
            "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
            "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY"));

    public FlooringMasterView(UserIO io) { this.io = io; }

    // Menu
    public int printMenuAndGetSelection() {
        io.print("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");
        io.print("* <<Flooring Program>>");
        io.print("* 1. Display Orders");
        io.print("* 2. Add an Order");
        io.print("* 3. Edit an Order");
        io.print("* 4. Remove an Order");
        io.print("* 5. Export All Data");
        io.print("* 6. Quit");
        io.print("*");
        io.print("* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *");

        return io.readInt("Please select from the above choices.", 1, 6);
    }

    // Get Date
    public LocalDate getDate(String prompt) {
        String userInput;
        while (true) {
            userInput = io.readString(prompt + " (MM-DD-YYYY): ");
            try {
                return LocalDate.parse(userInput.trim(), FORMATTER);
            } catch (DateTimeException e) {
                io.print("Invalid date format. Please use MM-DD-YYYY.");
            }
        }
    }

    public LocalDate getFutureDate() {
        LocalDate userInput;
        while (true) {
            userInput = getDate("Please enter the order date");
            if (userInput.isAfter(LocalDate.now())) {
                return userInput;
            }
            io.print("Invalid date. Order date must be in the future.");
        }
    }

    // Display orders for date
    public void displayOrders(List<Order> orders, LocalDate date) {
        io.print("\n=== Orders for " + date.format(FORMATTER) + " ===");

        for (Order currentOrder : orders) {
            String orderInfo = String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s",
                    currentOrder.getOrderNumber(),
                    currentOrder.getCustomerName(),
                    currentOrder.getState(),
                    currentOrder.getTaxRate(),
                    currentOrder.getProductType(),
                    currentOrder.getArea(),
                    currentOrder.getCostPerSquareFoot(),
                    currentOrder.getLaborCostPerSquareFoot(),
                    currentOrder.getMaterialCost(),
                    currentOrder.getLaborCost(),
                    currentOrder.getTax(),
                    currentOrder.getTotal());
            io.print(orderInfo);
        }
        io.readString("Please hit enter to continue.");
    }

    // Add Order
    public String getCustomerName() {
        while (true) {
            String name = io.readString("Enter customer name: ");
            if (isValidCustomerName(name)) {
                return name;
            }
            io.print("Invalid name. Name may not be blank and is limited to characters [a-z][0-9] " +
                    "as well as periods and comma characters.");
        }
    }

    private boolean isValidCustomerName(String name) {
        return (name != null) && (!name.isBlank()) && (name.matches("[a-zA-Z0-9., ]+"));
    }

    public String getState() {
        while (true) {
            String state = io.readString("Enter state abbreviation (e.g. NJ, VA, TX): ").trim().toUpperCase();
            if (VALID_STATES.contains(state)) {
                return state;
            }
            io.print("Invalid state abbreviation. Please enter a valid US state (e.g. CA, TX, NY).");
        }

    }

    private void displayProductList(List<Product> products) {
        io.print("\nAvailable products:");
        io.print(String.format("%-15s %-20s %-25s","Product Type", "Cost/sq ft", "Labor Cost/sq ft"));
        for (Product p : products) {
            io.print(String.format("%-15s %-20s %-25s", p.getProductType(), p.getCostPerSquareFoot(),
                    p.getLaborCostPerSquareFoot()));
        }
    }
    private String getProductType(List<Product> products) {
        displayProductList(products);
        while (true) {
            String input = io.readString("Enter product type: ").trim();
            for (Product p : products) {
                if (p.getProductType().equalsIgnoreCase(input)) {
                    return p.getProductType();
                }
            }
            io.print("Invalid product type. Please choose from the list above.");
        }
    }

    private BigDecimal getArea() {
        return io.readBigDecimal("Enter area (minimum 100 sq ft):", new BigDecimal("100.00"));
    }
    public Order getNewOrderInfo(String customerName, String state, List<Product> products) {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setState(state);
        order.setProductType(getProductType(products));
        order.setArea(getArea());
        return order;
    }

    // Display order summary
    public void displayOrderSummary(Order order, LocalDate date) {
        io.print("\n=== Order Summary ===");
        io.print("Order Date:           " + date.format(FORMATTER));
        io.print("Order #:              " + order.getOrderNumber());
        io.print("Customer Name:        " + order.getCustomerName());
        io.print("State:                " + order.getState());
        io.print("Tax Rate:             " + order.getTaxRate() + "%");
        io.print("Product Type:         " + order.getProductType());
        io.print("Area:                 " + order.getArea() + " sq ft");
        io.print("Cost/sq ft:           $" + order.getCostPerSquareFoot());
        io.print("Labor Cost/sq ft:     $" + order.getLaborCostPerSquareFoot());
        io.print("Material Cost:        $" + order.getMaterialCost());
        io.print("Labor Cost:           $" + order.getLaborCost());
        io.print("Tax:                  $" + order.getTax());
        io.print("Total:                $" + order.getTotal());
    }

    private boolean getYesNoResponse(String prompt) {
        while (true) {
            String input = io.readString(prompt).trim().toUpperCase();
            if (input.equals("Y")) return true;
            if (input.equals("N")) return false;
            io.print("Invalid Input. Please enter Y or N.");
        }
    }
    // Confirmation prompts
    public boolean getPlaceOrderConfirmation() {
        return getYesNoResponse("Would you like to place this order? (Y/N): ");
    }

    public boolean getSaveEditConfirmation() {
        return getYesNoResponse("Would you like to save these changes? (Y/N): ");
    }

    public boolean getRemoveOrderConfirmation() {
        return getYesNoResponse("Are you sure you want to remove this order? (Y/N): ");
    }
    public void displayExitBanner() {io.print("Good Bye!!!");}

    public void displayUnknownCommandBanner() { io.print("Unknown Command!!!"); }

    public void displayErrorMessage(String errMsg) {
        io.print("=== ERROR ====");
        io.print(errMsg);
    }
    public void displaySuccessMessage(String successMsg) {
        io.print("=== SUCCESS ===");
        io.print(successMsg);
    }
}
