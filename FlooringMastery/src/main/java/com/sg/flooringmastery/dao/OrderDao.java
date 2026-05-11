package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Order;
import com.sg.flooringmastery.service.FlooringMasteryDataValidationException;

import java.time.LocalDate;
import java.util.List;

public interface OrderDao {

    List<Order> getOrdersByDate(LocalDate date) throws  FlooringMasteryPersistenceException;

    Order addOrder(LocalDate date, Order order) throws FlooringMasteryPersistenceException;

    Order getOrder(LocalDate date, int orderNumber) throws FlooringMasteryPersistenceException;

    Order editOrder(LocalDate date, Order order) throws FlooringMasteryPersistenceException;

    Order removeOrder(LocalDate date, int orderNumber) throws FlooringMasteryPersistenceException;

    void exportAllData() throws FlooringMasteryPersistenceException;

    int getMaxOrderNumber() throws FlooringMasteryDataValidationException, FlooringMasteryPersistenceException;
}
