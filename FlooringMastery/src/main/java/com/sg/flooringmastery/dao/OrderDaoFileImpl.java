package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Order;
import com.sg.flooringmastery.service.FlooringMasteryDataValidationException;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.*;


public class OrderDaoFileImpl implements OrderDao {

    private final String ORDERS_DIR;
    private final String EXPORT_DIR;
    private final String EXPORT_FILE;
    private static final String DELIMITER = ",";
    private static final String HEADER = "OrderNumber,CustomerName,State,TaxRate,ProductType,Area,CostPerSquareFoot," +
            "LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total";
    private static final DateTimeFormatter FILE_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMddyyyy");
    // In memory storage: date -> list of orders for that dates
    private HashMap<LocalDate, List<Order>> orders = new HashMap<>();

    public OrderDaoFileImpl() {
        ORDERS_DIR = "Orders/";
        EXPORT_DIR = "Backup/";
        EXPORT_FILE = EXPORT_DIR + "ExportAllData.txt";
    }

    public OrderDaoFileImpl(String ordersDir, String exportDir) {
        ORDERS_DIR = ordersDir;
        EXPORT_DIR = exportDir;
        EXPORT_FILE = EXPORT_DIR + "ExportAllData.txt";
    }

    @Override
    public List<Order> getOrdersByDate(LocalDate date) throws FlooringMasteryPersistenceException {
        if (!orders.containsKey(date)) {
            loadOrdersForDate(date);
        }
        return new ArrayList<>(orders.get(date));
    }

    @Override
    public Order addOrder(LocalDate date, Order order) throws FlooringMasteryPersistenceException {
        if (!orders.containsKey(date)) {
            loadOrdersForDate(date);
        }
        // Add order to memory
        orders.get(date).add(order);
        return order;
    }

    @Override
    public Order getOrder(LocalDate date, int orderNumber) throws FlooringMasteryPersistenceException {
        if (!orders.containsKey(date)) {
            loadOrdersForDate(date);
        }

        for (Order order : orders.get(date)) {
            if (order.getOrderNumber() == orderNumber) {
                return order;
            }
        }

        return null; // order not found
    }

    @Override
    public Order editOrder(LocalDate date, Order order) throws FlooringMasteryPersistenceException {
        if (!orders.containsKey(date)) {
            loadOrdersForDate(date);
        }

        List<Order> ordersForDate = orders.get(date);
        for (int i = 0; i < ordersForDate.size(); i++) {
            if (ordersForDate.get(i).getOrderNumber() == order.getOrderNumber()) {
                ordersForDate.set(i, order); // replace order in memory
                writeOrdersForDate(date);
                return order;
            }
        }
        return null;
    }

    @Override
    public Order removeOrder(LocalDate date, int orderNumber) throws FlooringMasteryPersistenceException {
        if (!orders.containsKey(date)) {
            loadOrdersForDate(date);
        }

        List<Order> ordersForDate = orders.get(date);
        for (int i = 0; i < ordersForDate.size(); i++) {
            if (ordersForDate.get(i).getOrderNumber() == orderNumber) {
                Order removed = ordersForDate.remove(i);
                writeOrdersForDate(date); // Update the file
                return removed;
            }
        }
        return null;
    }

    @Override
    public void exportAllData() throws FlooringMasteryPersistenceException {
        // Load all oder files to memory
        File ordersDir = new File(ORDERS_DIR);
        File[] orderFiles = ordersDir.listFiles();
        if (orderFiles != null) {
            for (File file : orderFiles) {
                String fileName = file.getName();
                if (!fileName.startsWith("Orders_") || !fileName.endsWith(".txt")) continue;
                String dateAsText = fileName.replace("Orders_", "").replace(".txt", "");
                try {
                    LocalDate date = LocalDate.parse(dateAsText, FILE_DATE_FORMATTER);
                    if (!orders.containsKey(date)) {
                        loadOrdersForDate(date);
                    }
                } catch (DateTimeException e) {
                    // Skip files with malformed dates
                }
            }
        }

        // Write all data to file
        File backupDir = new File(EXPORT_DIR);
        // if backupDir not exist, create one
        if (!backupDir.exists()) {
            backupDir.mkdirs();
        }

        PrintWriter out;
        try {
            out = new PrintWriter(new FileWriter(EXPORT_FILE));
        } catch (IOException e) {
            throw new FlooringMasteryPersistenceException("Could not create export file.");
        }

        out.println(HEADER);
        for (LocalDate date : orders.keySet()) {
            for (Order order : orders.get(date)) {
                out.println(marshallOrder(order));
                out.flush();
            }
        }
        out.close();
    }

    @Override
    public int getMaxOrderNumber() throws FlooringMasteryDataValidationException, FlooringMasteryPersistenceException {
        int maxOrderNumber = 0;

        // First check all already loaded in memory
        for (List<Order> orderList : orders.values()) {
            for (Order order : orderList) {
                if (order.getOrderNumber() > maxOrderNumber) {
                    maxOrderNumber = order.getOrderNumber();
                }
            }
        }

        // Then scan any files not yet loaded into memory
        File ordersDir = new File(ORDERS_DIR);
        File[] orderFiles = ordersDir.listFiles();

        if (orderFiles != null) {
            for (File file : orderFiles) {
                String fileName = file.getName();
                // extract date from fileName
                String dateAsText = fileName.replace("Orders_", "").replace(".txt", "");
                // Parse dateAsText to LocalDate
                LocalDate date = LocalDate.parse(dateAsText, FILE_DATE_FORMATTER);

                // Only add new date to memory
                if (!orders.containsKey(date)) {
                    List<Order> loaded = getOrdersByDate(date);
                    for (Order o : loaded) {
                        if (o.getOrderNumber() > maxOrderNumber) {
                            maxOrderNumber = o.getOrderNumber();
                        }
                    }
                }
            }
        }
        return maxOrderNumber;
    }

    // Helper methods
    private Order unmarshallOrder(String orderAsText) {
        // Format: OrderNumber,CustomerName,State,TaxRate,ProductType,Area,
        // CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total
        // Example: 1,Ada Lovelace,CA,25.00,Tile,249.00,3.50,4.15,871.50,1033.35,476.21,2381.06

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
            name.append(orderTokens[i]);
        }
        order.setCustomerName(name.toString());

        return order;
    }

    private String marshallOrder(Order order) {
        // Format: OrderNumber,CustomerName,State,TaxRate,ProductType,Area,
        // CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total
        // Example: 1,Ada Lovelace,CA,25.00,Tile,249.00,3.50,4.15,871.50,1033.35,476.21,2381.06

        // orderNumber
        String orderAsText = order.getOrderNumber() + DELIMITER;
        // customerName
        orderAsText += order.getCustomerName() + DELIMITER;
        // state
        orderAsText += order.getState() + DELIMITER;
        // taxRate
        orderAsText += order.getTaxRate() + DELIMITER;
        // productType
        orderAsText += order.getProductType() + DELIMITER;
        // area
        orderAsText += order.getArea() + DELIMITER;
        // costPerSquareFoot
        orderAsText += order.getCostPerSquareFoot() + DELIMITER;
        // laborCostPerSquareFoot
        orderAsText += order.getLaborCostPerSquareFoot() + DELIMITER;
        // materialCost
        orderAsText += order.getMaterialCost() + DELIMITER;
        // laborCost
        orderAsText += order.getLaborCost() + DELIMITER;
        // tax
        orderAsText += order.getTax() + DELIMITER;
        // total
        orderAsText += order.getTotal();

        return orderAsText;
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
        // Skip the header
        sc.nextLine();
        String currentLine;
        Order currentOrder;
        while (sc.hasNextLine()) {
            currentLine = sc.nextLine();
            currentOrder = unmarshallOrder(currentLine);
            loaded.add(currentOrder);
        }
        orders.put(date, loaded);
    }

    private void writeOrdersForDate(LocalDate date) throws FlooringMasteryPersistenceException {
        String fileName = getFileNameForDate(date);
        PrintWriter out;
        try {
            out = new PrintWriter(new FileWriter(fileName));
        } catch (IOException e) {
            throw new FlooringMasteryPersistenceException("Could not save orders for " + date + ".", e);
        }
        out.println(HEADER);
        for (Order order : orders.get(date)) {
            out.println(marshallOrder(order));
            out.flush();
        }
        out.close();
    }

    private String getFileNameForDate(LocalDate date) {
        return ORDERS_DIR + "Orders_" + date.format(FILE_DATE_FORMATTER) + ".txt";
    }
}
