package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Order;
import com.sg.flooringmastery.service.FlooringMasteryDataValidationException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderDaoStubImpl implements OrderDao {

    public Order onlyOrder;
    public final LocalDate onlyDate = LocalDate.of(2099, 1, 1);

    // Spy fields — allow tests to assert that calls reached the DAO
    public Order lastAdded;
    public Order lastEdited;
    public int lastRemovedNumber = -1;
    public boolean exportCalled = false;

    public OrderDaoStubImpl() {
        onlyOrder = new Order();
        onlyOrder.setOrderNumber(1);
        onlyOrder.setCustomerName("Test Customer");
        onlyOrder.setState("KY");
        onlyOrder.setTaxRate(new BigDecimal("6.00"));
        onlyOrder.setProductType("Tile");
        onlyOrder.setArea(new BigDecimal("100.00"));
        onlyOrder.setCostPerSquareFoot(new BigDecimal("3.50"));
        onlyOrder.setLaborCostPerSquareFoot(new BigDecimal("4.15"));
        onlyOrder.setMaterialCost(new BigDecimal("350.00"));
        onlyOrder.setLaborCost(new BigDecimal("415.00"));
        onlyOrder.setTax(new BigDecimal("45.90"));
        onlyOrder.setTotal(new BigDecimal("810.90"));
    }

    @Override
    public List<Order> getOrdersByDate(LocalDate date) {
        List<Order> result = new ArrayList<>();
        if (date.equals(onlyDate) && onlyOrder != null) {
            result.add(onlyOrder);
        }
        return result;
    }

    @Override
    public Order addOrder(LocalDate date, Order order) {
        this.lastAdded = order;
        return order;
    }

    @Override
    public Order getOrder(LocalDate date, int orderNumber) {
        if (date.equals(onlyDate) && onlyOrder != null && onlyOrder.getOrderNumber() == orderNumber) {
            return onlyOrder;
        }
        return null;
    }

    @Override
    public Order editOrder(LocalDate date, Order order) {
        this.lastEdited = order;
        if (date.equals(onlyDate) && order.getOrderNumber() == onlyOrder.getOrderNumber()) {
            this.onlyOrder = order;
            return order;
        }
        return null;
    }

    @Override
    public Order removeOrder(LocalDate date, int orderNumber) {
        this.lastRemovedNumber = orderNumber;
        if (date.equals(onlyDate) && onlyOrder != null && onlyOrder.getOrderNumber() == orderNumber) {
            Order removed = onlyOrder;
            onlyOrder = null;
            return removed;
        }
        return null;
    }

    @Override
    public void exportAllData() {
        this.exportCalled = true;
    }

    @Override
    public int getMaxOrderNumber() throws FlooringMasteryDataValidationException {
        return onlyOrder == null ? 0 : onlyOrder.getOrderNumber();
    }
}
