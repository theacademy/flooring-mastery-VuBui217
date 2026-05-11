package com.sg.flooringmastery.service;

import com.sg.flooringmastery.dao.FlooringMasteryPersistenceException;
import com.sg.flooringmastery.model.Order;
import com.sg.flooringmastery.model.Product;
import com.sg.flooringmastery.model.Tax;

import java.time.LocalDate;
import java.util.*;

public interface FlooringMasteryServiceLayer {

    List<Order> getOrdersByDate(LocalDate date) throws FlooringMasteryPersistenceException;

    Order calculateOrder(LocalDate date, Order order) throws FlooringMasteryDataValidationException, FlooringMasteryPersistenceException;

    void addOrder(LocalDate date, Order order) throws FlooringMasteryPersistenceException,
            FlooringMasteryDataValidationException;

    Order reCalculateOrder(LocalDate date, Order order) throws FlooringMasteryDataValidationException, FlooringMasteryPersistenceException;

    Order getOrder(LocalDate date, int orderNumber) throws FlooringMasteryDataValidationException, FlooringMasteryPersistenceException;

    Order editOrder(LocalDate date, Order order) throws FlooringMasteryPersistenceException;

    Order removeOrder(LocalDate date, int orderNumber) throws FlooringMasteryPersistenceException;

    void exportAllData() throws FlooringMasteryPersistenceException;

    List<Product> getAllProducts() throws FlooringMasteryPersistenceException;

    List<Tax> getAllTaxes() throws FlooringMasteryPersistenceException;

    Tax getTaxByState(String stateAbbreviation) throws FlooringMasteryPersistenceException, FlooringMasteryDataValidationException;
}
