package com.sg.flooringmastery.controller;

import com.sg.flooringmastery.dao.FlooringMasteryPersistenceException;
import com.sg.flooringmastery.model.Order;
import com.sg.flooringmastery.model.Product;
import com.sg.flooringmastery.model.Tax;
import com.sg.flooringmastery.service.FlooringMasteryDataValidationException;
import com.sg.flooringmastery.service.FlooringMasteryServiceLayer;
import com.sg.flooringmastery.ui.FlooringMasteryView;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FlooringMasteryController {
    private FlooringMasteryView view;
    private FlooringMasteryServiceLayer service;

    public FlooringMasteryController(FlooringMasteryView view, FlooringMasteryServiceLayer service) {
        this.view = view;
        this.service = service;
    }

    public void run() {
        boolean keepGoing = true;
        int menuSelection = 0;

        while (keepGoing) {
            menuSelection = getMenuSelection();

            switch (menuSelection) {
                case 1:
                    displayOrders();
                    break;
                case 2:
                    addOrder();
                    break;
                case 3:
                    editOrder();
                    break;
                case 4:
                    removeOrder();
                    break;
                case 5:
                    exportAllData();
                    break;
                case 6:
                    keepGoing = false;
                    break;
                default:
                    unknownCommand();
            }
        }
        exitMessage();
    }

    // Display Orders
    private void displayOrders() {
        LocalDate date = view.getDate("Enter the order date: ");
        try {
            List<Order> orders = service.getOrdersByDate(date);
            if (orders.isEmpty()) {
                view.displayErrorMessage("No orders found for that date.");
            } else {
                view.displayOrders(orders, date);
            }
        } catch (FlooringMasteryPersistenceException e) {
            view.displayErrorMessage(e.getMessage());
        }

    }

    // Add Order
    private void addOrder() {
        LocalDate date = view.getFutureDate();

        try {
            // Get customer name
            String customerName = view.getCustomerName();

            // Stream valid state from Taxes.txt
            Set<String> validStates = service.getAllTaxes().stream()
                    .map(tax -> tax.getStateAbbreviation())
                    .map(s -> s.toUpperCase())
                    .collect(Collectors.toSet());

            // Get state and validate
            String state = view.getState(validStates);

            // State is valid, display product list and crate new order
            List<Product> products = service.getAllProducts();
            Order order = view.getNewOrderInfo(customerName, state, products);

            // Calculate remaining fields
            Order calculatedOrder = service.calculateOrder(date, order);

            // Show order summary and ask for confirmation
            view.displayOrderSummary(calculatedOrder, date);
            if (view.getPlaceOrderConfirmation()) {
                // Yes -> save order to memory
                service.addOrder(date, calculatedOrder);
                view.displaySuccessMessage("Order successfully placed!");
            } else {
                // No -> Display message
                view.displayErrorMessage("Order cancelled. Return to main menu.");
            }
        } catch (FlooringMasteryPersistenceException | FlooringMasteryDataValidationException e) {
            view.displayErrorMessage(e.getMessage());
        }
    }

    // Edit Order
    private void editOrder() {
        LocalDate date = view.getDate("Enter the order date: ");
        int orderNumber = view.getOrderNumber();

        try {
            // Get the existing order from based on date and orderNumber
            Order currentOrder = service.getOrder(date, orderNumber);

            // Stream valid state from Taxes.txt
            Set<String> validStates = service.getAllTaxes().stream()
                    .map(tax -> tax.getStateAbbreviation())
                    .map(s -> s.toUpperCase())
                    .collect(Collectors.toSet());

            // Get Product lists to display
            List<Product> products = service.getAllProducts();

            // Create an edited order
            Order edited = view.getEditOrderInfo(currentOrder, validStates, products);

            // fieldsChanged flag
            boolean fieldsChanged = !currentOrder.getState().equals(edited.getState())
                    || !currentOrder.getProductType().equals(edited.getProductType())
                    || currentOrder.getArea().compareTo(edited.getArea()) != 0;

            // If there are any fields changed, recalculate remaining fields
            if (fieldsChanged){
                edited = service.reCalculateOrder(date, edited);
            }

            view.displayOrderSummary(edited, date);

            if (view.getSaveEditConfirmation()) {
                // Yes -> edit order in file
                service.editOrder(date, edited);
                view.displaySuccessMessage("Order updated.");
            } else {
                view.displayErrorMessage("Edit cancelled. Returning to main menu.");
            }

        } catch (FlooringMasteryPersistenceException | FlooringMasteryDataValidationException e) {
            view.displayErrorMessage(e.getMessage());
        }
    }

    // Remove an order
    private void removeOrder() {
        LocalDate date = view.getDate("Enter the order date: ");
        int orderNumber = view.getOrderNumber();

        try {
            Order order = service.getOrder(date, orderNumber);
            view.displayOrderSummary(order, date);

            if (view.getRemoveOrderConfirmation()) {
                service.removeOrder(date, orderNumber);
                view.displaySuccessMessage("Order #" + orderNumber + " removed.");
            } else {
                view.displayErrorMessage("Removal cancelled. Returning to main menu.");
            }

        } catch (FlooringMasteryPersistenceException | FlooringMasteryDataValidationException e) {
            view.displayErrorMessage(e.getMessage());
        }
    }

    // Export all data
    private void exportAllData() {
        try {
            service.exportAllData();
            view.displaySuccessMessage("All data exported to Backup/ExportAllData.txt");
        } catch (FlooringMasteryPersistenceException e) {
            view.displayErrorMessage(e.getMessage());
        }
    }
    private int getMenuSelection() { return view.printMenuAndGetSelection(); }

    private void unknownCommand() { view.displayUnknownCommandBanner(); }

    private void exitMessage() { view.displayExitBanner(); }
}
