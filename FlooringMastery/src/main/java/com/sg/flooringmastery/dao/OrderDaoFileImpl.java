package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.*;


public class OrderDaoFileImpl implements OrderDao {

    private static final String ORDERS_DIR = "Orders/";
    private static final String DELIMITER = ",";
    private static final String HEADER = "OrderNumber,CustomerName,State,TaxRate,ProductType,Area,CostPerSquareFoot," +
            "LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total";
    private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMddyyyy");
    // In memory storage: date -> list of orders for that dates
    private HashMap<LocalDate, List<Order>> orders = new HashMap<>();
    @Override
    public List<Order> getOrdersByDate(LocalDate date) throws FlooringMasteryPersistenceException {
        if (!orders.containsKey(date)) {
            loadOrdersForDate(date);
        }
        return new ArrayList<>(orders.get(date));
    }

    @Override
    public Order addOrder(LocalDate date, Order order) throws FlooringMasteryPersistenceException {
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

    // Helper methods
    private Order unmarshallOrder(String orderAsText) {
        String[] orderTokens = orderAsText.split(DELIMITER);
        int len = orderTokens.length;

        Order order = new Order();

        // Parse from both end to handle customer names that contains commas

        // Index 0: orderNumber
        order.setOrderNumber(Integer.parseInt(orderTokens[0].trim()));

        // Last 10 fields are always fixed
        // Index len-1: total
        order.setTotal(new BigDecimal(orderTokens[len-1].trim()));
        // Index len-2: tax
        order.setTax(new BigDecimal(orderTokens[len-2].trim()));
        // Index len-3: laborCost
        order.setLaborCost(new BigDecimal(orderTokens[len-3].trim()));
        // Index len-4: materialCost
        order.setMaterialCost(new BigDecimal(orderTokens[len-4].trim()));
        // Index len-5: laborCostPerSquareFoot
        order.setLaborCostPerSquareFoot(new BigDecimal(orderTokens[len-5].trim()));
        // Index len-6: costPerSquareFoot
        order.setCostPerSquareFoot(new BigDecimal(orderTokens[len-6].trim()));
        // Index len-7: area
        order.setArea(new BigDecimal(orderTokens[len-7].trim()));
        // Index len-8: productType
        order.setProductType(orderTokens[len-8].trim());
        // Index len-9: taxRate
        order.setTaxRate(new BigDecimal(orderTokens[len-9].trim()));
        // Index len-10: state
        order.setState(orderTokens[len-10].trim());
        // Index 1 -> len-11: customerName
        StringBuilder name = new StringBuilder();
        for (int i = 1; i <= len-11; i++) {
            if (i > 1) {
                name.append(",");
            }
            name.append(orderTokens[i].trim());
        }
        order.setCustomerName(name.toString());

        return order;
    }

    private String getFileNameForDate(LocalDate date) {
        return ORDERS_DIR + "Orders_" + date.format(FILE_DATE_FORMATTER) + ".txt";
    }
    private void loadOrdersForDate(LocalDate date) throws FlooringMasteryPersistenceException {
        String fileName = getFileNameForDate(date);
        File file = new File(fileName);

        // File name doesn't exist, create an empty list
        if (!file.exists()) {
            orders.put(date, new ArrayList<>());
            return;
        }

        Scanner sc;

        try {
            sc = new Scanner(
                    new BufferedReader(
                            new FileReader(file)
                    )
            );
        } catch (FileNotFoundException e) {
            throw new FlooringMasteryPersistenceException("Could not load orders data to memory.", e);
        }

        List<Order> loaded = new ArrayList<>();
        String currentLine = sc.nextLine(); // skip the header
        Order currentOrder;
        while (sc.hasNextLine()) {
            currentLine = sc.nextLine();
            currentOrder = unmarshallOrder(currentLine);
            loaded.add(currentOrder);
        }
        orders.put(date, loaded);
    }
}
