package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Order;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderDaoFileImplTest {

    @TempDir
    Path tempDir;

    private Path ordersDir;
    private Path backupDir;
    private String ordersDirPath;
    private String backupDirPath;
    private OrderDaoFileImpl dao;
    private final LocalDate testDate = LocalDate.of(2013, 6, 1);

    @BeforeEach
    void setUp() throws IOException {
        ordersDir = tempDir.resolve("Orders");
        backupDir = tempDir.resolve("Backup");
        Files.createDirectories(ordersDir);
        Files.createDirectories(backupDir);

        ordersDirPath = ordersDir.toString() + "/";
        backupDirPath = backupDir.toString() + "/";

        // Seed an orders file for 2013-06-01
        Path ordersFile = ordersDir.resolve("Orders_06012013.txt");
        Files.writeString(ordersFile,
                "OrderNumber,CustomerName,State,TaxRate,ProductType,Area,CostPerSquareFoot,LaborCostPerSquareFoot,MaterialCost,LaborCost,Tax,Total\n" +
                "1,Ada Lovelace,CA,25.00,Tile,249.00,3.50,4.15,871.50,1033.35,476.21,2381.06\n" +
                "2,Doctor Who,WA,9.25,Wood,243.00,5.15,4.75,1251.45,1154.25,222.32,2628.02\n");

        dao = new OrderDaoFileImpl(ordersDirPath, backupDirPath);
    }

    @Test
    void getOrdersByDate_existingFile_returnsOrders() throws Exception {
        List<Order> orders = dao.getOrdersByDate(testDate);
        assertEquals(2, orders.size());
        assertTrue(orders.stream().anyMatch(o -> o.getCustomerName().equals("Ada Lovelace")));
    }

    @Test
    void getOrdersByDate_noFile_returnsEmpty() throws Exception {
        List<Order> orders = dao.getOrdersByDate(LocalDate.of(2099, 1, 1));
        assertTrue(orders.isEmpty());
    }

    @Test
    void addOrder_storesInMemory() throws Exception {
        Order order = makeOrder(3, "New Customer", "TX");
        dao.addOrder(testDate, order);

        List<Order> orders = dao.getOrdersByDate(testDate);
        assertEquals(3, orders.size());
        assertTrue(orders.stream().anyMatch(o -> o.getOrderNumber() == 3));
    }

    @Test
    void getOrder_found_returnsOrder() throws Exception {
        Order order = dao.getOrder(testDate, 1);
        assertNotNull(order);
        assertEquals("Ada Lovelace", order.getCustomerName());
    }

    @Test
    void getOrder_notFound_returnsNull() throws Exception {
        Order order = dao.getOrder(testDate, 999);
        assertNull(order);
    }

    @Test
    void editOrder_replacesInMemoryAndPersists() throws Exception {
        Order edited = makeOrder(1, "Edited Customer", "TX");
        dao.editOrder(testDate, edited);

        // Verify in memory
        Order fromMemory = dao.getOrder(testDate, 1);
        assertEquals("Edited Customer", fromMemory.getCustomerName());

        // Verify on disk via fresh DAO
        OrderDaoFileImpl freshDao = new OrderDaoFileImpl(ordersDirPath, backupDirPath);
        Order fromDisk = freshDao.getOrder(testDate, 1);
        assertEquals("Edited Customer", fromDisk.getCustomerName());
    }

    @Test
    void removeOrder_removesFromMemoryAndPersists() throws Exception {
        dao.removeOrder(testDate, 1);

        // Verify in memory
        assertNull(dao.getOrder(testDate, 1));

        // Verify on disk via fresh DAO
        OrderDaoFileImpl freshDao = new OrderDaoFileImpl(ordersDirPath, backupDirPath);
        assertNull(freshDao.getOrder(testDate, 1));
        assertEquals(1, freshDao.getOrdersByDate(testDate).size());
    }

    @Test
    void testCustomerNameWithCommas() throws Exception {
        Order edited = makeOrder(1, "Acme, Inc.", "TX");
        dao.editOrder(testDate, edited);

        OrderDaoFileImpl freshDao = new OrderDaoFileImpl(ordersDirPath, backupDirPath);
        Order loaded = freshDao.getOrder(testDate, 1);
        assertEquals("Acme, Inc.", loaded.getCustomerName());
    }

    @Test
    void getMaxOrderNumber_returnsMaxAcrossLoadedAndOnDisk() throws Exception {
        int max = dao.getMaxOrderNumber();
        assertEquals(2, max);
    }

    @Test
    void exportAllData_createsBackupFileWithAllOrders() throws Exception {
        dao.exportAllData();

        Path exportFile = backupDir.resolve("ExportAllData.txt");
        assertTrue(Files.exists(exportFile), "Export file should exist");

        String content = Files.readString(exportFile);
        assertTrue(content.contains("OrderNumber"), "Header should be present");
        assertTrue(content.contains("Ada Lovelace"), "Should contain seeded order #1");
        assertTrue(content.contains("Doctor Who"), "Should contain seeded order #2");
    }

    private Order makeOrder(int number, String name, String state) {
        Order o = new Order();
        o.setOrderNumber(number);
        o.setCustomerName(name);
        o.setState(state);
        o.setTaxRate(new BigDecimal("4.45"));
        o.setProductType("Tile");
        o.setArea(new BigDecimal("100.00"));
        o.setCostPerSquareFoot(new BigDecimal("3.50"));
        o.setLaborCostPerSquareFoot(new BigDecimal("4.15"));
        o.setMaterialCost(new BigDecimal("350.00"));
        o.setLaborCost(new BigDecimal("415.00"));
        o.setTax(new BigDecimal("34.04"));
        o.setTotal(new BigDecimal("799.04"));
        return o;
    }
}
