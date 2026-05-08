package com.sg.flooringmastery.controller;

import com.sg.flooringmastery.dao.FlooringMasteryPersistenceException;
import com.sg.flooringmastery.model.Order;
import com.sg.flooringmastery.service.FlooringMasteryServiceLayer;
import com.sg.flooringmastery.ui.FlooringMasterView;

import java.time.LocalDate;
import java.util.List;

public class FlooringMasteryController {
    private FlooringMasterView view;
    private FlooringMasteryServiceLayer service;

    public FlooringMasteryController(FlooringMasterView view, FlooringMasteryServiceLayer service) {
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
                    System.out.println("Add an Order");
                    break;
                case 3:
                    System.out.println("Edit an Order");
                    break;
                case 4:
                    System.out.println("Remove an Order");
                    break;
                case 5:
                    System.out.println("Export All Data");
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
    private int getMenuSelection() { return view.printMenuAndGetSelection(); }

    private void unknownCommand() { view.displayUnknownCommandBanner(); }

    private void exitMessage() { view.displayExitBanner(); }
}
