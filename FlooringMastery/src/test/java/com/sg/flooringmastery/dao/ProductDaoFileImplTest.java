package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductDaoFileImplTest {

    @TempDir
    Path tempDir;

    private ProductDaoFileImpl dao;

    @BeforeEach
    void setUp() throws IOException {
        Path productsFile = tempDir.resolve("Products.txt");
        Files.writeString(productsFile,
                "ProductType,CostPerSquareFoot,LaborCostPerSquareFoot\n" +
                "Carpet,2.25,2.10\n" +
                "Tile,3.50,4.15\n" +
                "Wood,5.15,4.75\n");
        dao = new ProductDaoFileImpl(productsFile.toString());
    }

    @Test
    void getAllProducts_returnsAllEntries() throws Exception {
        List<Product> products = dao.getAllProducts();
        assertEquals(3, products.size());
    }

    @Test
    void getProductByType_existing_returnsProduct() throws Exception {
        Product p = dao.getProductByType("Tile");
        assertNotNull(p);
        assertEquals(0, new BigDecimal("3.50").compareTo(p.getCostPerSquareFoot()));
        assertEquals(0, new BigDecimal("4.15").compareTo(p.getLaborCostPerSquareFoot()));
    }

    @Test
    void getProductByType_unknown_returnsNull() throws Exception {
        Product p = dao.getProductByType("Marble");
        assertNull(p);
    }
}
