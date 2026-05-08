package com.sg.flooringmastery.service;

import com.sg.flooringmastery.dao.FlooringMasteryPersistenceException;
import com.sg.flooringmastery.model.Order;
import com.sg.flooringmastery.model.Product;
import com.sg.flooringmastery.model.Tax;

import java.time.LocalDate;
import java.util.*;

public interface FlooringMasteryServiceLayer {

    List<Order> getOrdersByDate(LocalDate date) throws FlooringMasteryPersistenceException;

    Order addOrder(LocalDate date, Order order) throws FlooringMasteryPersistenceException,
            FlooringMasteryDataValidationException;

    Order editOrder(LocalDate date, Order order) throws FlooringMasteryPersistenceException;

    Order removeOrder(LocalDate date, int orderNumber) throws FlooringMasteryPersistenceException;

    void exportAllData() throws FlooringMasteryPersistenceException;

    List<Product> getAllProducts() throws FlooringMasteryPersistenceException;

    Tax getTaxByState(String stateAbbreviation) throws FlooringMasteryPersistenceException;
}
