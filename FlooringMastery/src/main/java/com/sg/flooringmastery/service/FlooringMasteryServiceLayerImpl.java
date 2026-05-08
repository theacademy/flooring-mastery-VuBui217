package com.sg.flooringmastery.service;

import com.sg.flooringmastery.dao.FlooringMasteryPersistenceException;
import com.sg.flooringmastery.dao.OrderDao;
import com.sg.flooringmastery.dao.ProductDao;
import com.sg.flooringmastery.dao.TaxDao;
import com.sg.flooringmastery.model.Order;
import com.sg.flooringmastery.model.Product;
import com.sg.flooringmastery.model.Tax;

import java.time.LocalDate;
import java.util.List;

public class FlooringMasteryServiceLayerImpl implements FlooringMasteryServiceLayer {

    private OrderDao orderDao;
//    private ProductDao productDao;
//    private TaxDao taxDao;

    public FlooringMasteryServiceLayerImpl(OrderDao orderDao) {
        this.orderDao = orderDao;
//        this.productDao = productDao;
//        this.taxDao = taxDao;
    }
    // Display Orders
    @Override
    public List<Order> getOrdersByDate(LocalDate date) throws FlooringMasteryPersistenceException {
        return orderDao.getOrdersByDate(date);
    }

    @Override
    public Order addOrder(LocalDate date, Order order) throws FlooringMasteryPersistenceException, FlooringMasteryDataValidationException {
        return null;
    }

    @Override
    public Order editOrder(LocalDate date, Order order) throws FlooringMasteryPersistenceException {
        return null;
    }

    @Override
    public Order removeOrder(LocalDate date, int orderNumber) throws FlooringMasteryPersistenceException {
        return null;
    }

    @Override
    public void exportAllData() throws FlooringMasteryPersistenceException {

    }

    @Override
    public List<Product> getAllProducts() throws FlooringMasteryPersistenceException {
        return null;
    }

    @Override
    public Tax getTaxByState(String stateAbbreviation) throws FlooringMasteryPersistenceException {
        return null;
    }
}
