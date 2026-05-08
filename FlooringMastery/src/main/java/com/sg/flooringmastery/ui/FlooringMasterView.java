package com.sg.flooringmastery.ui;

import com.sg.flooringmastery.model.Order;

import java.sql.Date;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FlooringMasterView {
    private UserIO io;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");

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

    // Display orders
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

    public void displayExitBanner() {io.print("Good Bye!!!");}

    public void displayUnknownCommandBanner() { io.print("Unknown Command!!!"); }

    public void displayErrorMessage(String errMsg) {
        io.print("=== ERROR ====");
        io.print(errMsg);
    }
}
