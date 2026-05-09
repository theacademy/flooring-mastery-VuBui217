package com.sg.flooringmastery.service;

import com.sg.flooringmastery.dao.FlooringMasteryPersistenceException;
import com.sg.flooringmastery.dao.OrderDao;
import com.sg.flooringmastery.dao.ProductDao;
import com.sg.flooringmastery.dao.TaxDao;
import com.sg.flooringmastery.model.Order;
import com.sg.flooringmastery.model.Product;
import com.sg.flooringmastery.model.Tax;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

public class FlooringMasteryServiceLayerImpl implements FlooringMasteryServiceLayer {

    private OrderDao orderDao;
    private ProductDao productDao;
    private TaxDao taxDao;

    public FlooringMasteryServiceLayerImpl(OrderDao orderDao, ProductDao productDao, TaxDao taxDao) {
        this.orderDao = orderDao;
        this.productDao = productDao;
        this.taxDao = taxDao;
    }
    // Display Orders
    @Override
    public List<Order> getOrdersByDate(LocalDate date) throws FlooringMasteryPersistenceException {
        return orderDao.getOrdersByDate(date);
    }


    @Override
    public Order calculateOrder(LocalDate date, Order order) throws FlooringMasteryDataValidationException, FlooringMasteryPersistenceException {
        // Validate user entered fields
        validateOrder(order);

        // Get tax and product form files
        Tax tax = getTaxByState(order.getState());
        Product product = productDao.getProductByType(order.getProductType());

        // Ser pricing fields for calculations
        order.setTaxRate(tax.getTaxRate());
        order.setCostPerSquareFoot(product.getCostPerSquareFoot());
        order.setLaborCostPerSquareFoot(product.getLaborCostPerSquareFoot());

        // Calculate other fields
        calculateOrderCosts(order);

        // Assign order number
        order.setOrderNumber(GetNextOrderNumber(date));

        return order;
    }

    private int GetNextOrderNumber(LocalDate date) throws FlooringMasteryDataValidationException, FlooringMasteryPersistenceException {
        return orderDao.getMaxOrderNumber() + 1;
    }

    private void calculateOrderCosts(Order order) {
        // materialCost = area * costPerSquareFoot
        BigDecimal materialCost = order.getArea().multiply(order.getCostPerSquareFoot())
                .setScale(2, RoundingMode.HALF_UP);

        // laborCost = area * laborCostPerSquareFoot
        BigDecimal laborCost = order.getArea().multiply(order.getLaborCostPerSquareFoot())
                .setScale(2, RoundingMode.HALF_UP);

        // tax = (materialCost + laborCost) * (taxRate / 100)
        BigDecimal tax = materialCost.add(laborCost)
                .multiply(order.getTaxRate()
                        .divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP))
                .setScale(2, RoundingMode.HALF_UP);

        // total = materialCost + laborCost + tax
        BigDecimal total = materialCost.add(laborCost).add(tax)
                .setScale(2, RoundingMode.HALF_UP);

        order.setMaterialCost(materialCost);
        order.setLaborCost(laborCost);
        order.setTax(tax);
        order.setTotal(total);
    }

    @Override
    public void addOrder(LocalDate date, Order order) throws FlooringMasteryPersistenceException, FlooringMasteryDataValidationException {
        orderDao.addOrder(date, order);
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
        return productDao.getAllProducts();
    }

    @Override
    public List<Tax> getAllTaxes() throws FlooringMasteryPersistenceException {
        return taxDao.getAllTaxes();
    }

    @Override
    public Tax getTaxByState(String stateAbbreviation) throws FlooringMasteryPersistenceException, FlooringMasteryDataValidationException {
        Tax tax = taxDao.getTaxByState(stateAbbreviation);
        if (tax == null) {
            throw new FlooringMasteryDataValidationException(
                    "Sorry, we do not sell flooring in " + stateAbbreviation + ".");
        }
        return tax;
    }


    // Private helper methods
    private void validateOrder(Order order) throws
            FlooringMasteryDataValidationException {

        if (order.getCustomerName() == null || order.getCustomerName().isBlank()) {
            throw new FlooringMasteryDataValidationException(
                    "Customer name may not be blank.");
        }
        if (!order.getCustomerName().matches("[a-zA-Z0-9., ]+")) {
            throw new FlooringMasteryDataValidationException(
                    "Customer name may only contain letters, numbers, periods, and commas.");
        }
        if (order.getState() == null || order.getState().isBlank()) {
            throw new FlooringMasteryDataValidationException(
                    "State may not be blank.");
        }
        if (order.getProductType() == null || order.getProductType().isBlank()) {
            throw new FlooringMasteryDataValidationException(
                    "Product type may not be blank.");
        }
        if (order.getArea() == null) {
            throw new FlooringMasteryDataValidationException(
                    "Area may not be blank.");
        }
        if (order.getArea().compareTo(new BigDecimal("100.00")) < 0) {
            throw new FlooringMasteryDataValidationException(
                    "Minimum order area is 100 sq ft.");
        }

    }
}
