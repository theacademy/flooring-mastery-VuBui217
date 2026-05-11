package com.sg.flooringmastery.service;

import com.sg.flooringmastery.dao.OrderDaoStubImpl;
import com.sg.flooringmastery.dao.ProductDaoStubImpl;
import com.sg.flooringmastery.dao.TaxDaoStubImpl;
import com.sg.flooringmastery.model.Order;
import com.sg.flooringmastery.model.Product;
import com.sg.flooringmastery.model.Tax;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FlooringMasteryServiceLayerImplTest {

    private FlooringMasteryServiceLayer service;
    private OrderDaoStubImpl orderDao;
    private ProductDaoStubImpl productDao;
    private TaxDaoStubImpl taxDao;

    private final LocalDate testDate = LocalDate.of(2099, 1, 1);

    @BeforeEach
    void setUp() {
        orderDao = new OrderDaoStubImpl();
        productDao = new ProductDaoStubImpl();
        taxDao = new TaxDaoStubImpl();
        service = new FlooringMasteryServiceLayerImpl(orderDao, productDao, taxDao);
    }

    // Calculate Order

    @Test
    void calculateOrder_correctMath() throws Exception {
        // ARRANGE
        Order order = newOrder("Test", "KY", "Tile", "100.00");

        // ACT
        Order calc = service.calculateOrder(testDate, order);

        // ASSERT
        // material = 100 * 3.50 = 350.00
        assertEquals(0, new BigDecimal("350.00").compareTo(calc.getMaterialCost()));
        // labor = 100 * 4.15 = 415.00
        assertEquals(0, new BigDecimal("415.00").compareTo(calc.getLaborCost()));
        // tax = (350 + 415) * 6/100 = 45.90
        assertEquals(0, new BigDecimal("45.90").compareTo(calc.getTax()));
        // total = 350 + 415 + 45.90 = 810.90
        assertEquals(0, new BigDecimal("810.90").compareTo(calc.getTotal()));
    }

    @Test
    void calculateOrder_assignsNextOrderNumber() throws Exception {
        // ARRANGE
        Order order = newOrder("Test", "KY", "Tile", "100.00");

        // ACT
        Order calc = service.calculateOrder(testDate, order);

        // ASSERT — stub's max is 1, so next is 2
        assertEquals(2, calc.getOrderNumber());
    }

    @Test
    void calculateOrder_blankName_throws() {
        // ARRANGE
        Order order = newOrder("", "KY", "Tile", "100.00");

        // ACT & ASSERT
        assertThrows(FlooringMasteryDataValidationException.class,
                () -> service.calculateOrder(testDate, order));
    }

    @Test
    void calculateOrder_invalidNameChars_throws() {
        // ARRANGE
        Order order = newOrder("Test!@#", "KY", "Tile", "100.00");

        // ACT & ASSERT
        assertThrows(FlooringMasteryDataValidationException.class,
                () -> service.calculateOrder(testDate, order));
    }

    @Test
    void calculateOrder_areaUnder100_throws() {
        // ARRANGE
        Order order = newOrder("Test", "KY", "Tile", "99.99");

        // ACT & ASSERT
        assertThrows(FlooringMasteryDataValidationException.class,
                () -> service.calculateOrder(testDate, order));
    }

    @Test
    void calculateOrder_unknownState_throws() {
        // ARRANGE
        Order order = newOrder("Test", "ZZ", "Tile", "100.00");

        // ACT & ASSERT
        assertThrows(FlooringMasteryDataValidationException.class,
                () -> service.calculateOrder(testDate, order));
    }

    @Test
    void calculateOrder_validNameWithComma_isAccepted() throws Exception {
        // ARRANGE
        Order order = newOrder("Acme, Inc.", "KY", "Tile", "100.00");

        // ACT
        Order calc = service.calculateOrder(testDate, order);

        // ASSERT
        assertEquals("Acme, Inc.", calc.getCustomerName());
    }

    // Recalculate order

    @Test
    void reCalculateOrder_doesNotChangeOrderNumber() throws Exception {
        // ARRANGE
        Order order = newOrder("Test", "KY", "Tile", "100.00");
        order.setOrderNumber(42);

        // ACT
        Order recalc = service.reCalculateOrder(testDate, order);

        // ASSERT
        assertEquals(42, recalc.getOrderNumber());
    }

    @Test
    void reCalculateOrder_recomputesPricing() throws Exception {
        // ARRANGE
        Order order = newOrder("Test", "KY", "Tile", "200.00");
        order.setOrderNumber(7);

        // ACT
        Order recalc = service.reCalculateOrder(testDate, order);

        // ASSERT — material = 200 * 3.50 = 700.00
        assertEquals(0, new BigDecimal("700.00").compareTo(recalc.getMaterialCost()));
    }

    // getOrder

    @Test
    void getOrder_existing_returnsOrder() throws Exception {
        // ACT (arrange is in @BeforeEach via stub)
        Order o = service.getOrder(testDate, 1);

        // ASSERT
        assertNotNull(o);
        assertEquals("Test Customer", o.getCustomerName());
    }

    @Test
    void getOrder_notFound_throws() {
        // ACT & ASSERT (arrange is in @BeforeEach via stub)
        assertThrows(FlooringMasteryDataValidationException.class,
                () -> service.getOrder(testDate, 999));
    }

    // getTaxBysState

    @Test
    void getTaxByState_known_returnsTax() throws Exception {
        // ACT (arrange is in @BeforeEach via stub)
        Tax tax = service.getTaxByState("KY");

        // ASSERT
        assertEquals("Kentucky", tax.getStateName());
    }

    @Test
    void getTaxByState_unknown_throws() {
        // ACT & ASSERT (arrange is in @BeforeEach via stub)
        assertThrows(FlooringMasteryDataValidationException.class,
                () -> service.getTaxByState("ZZ"));
    }

    @Test
    void getAllProducts_returnsFromDao() throws Exception {
        // ACT (arrange is in @BeforeEach via stub)
        List<Product> products = service.getAllProducts();

        // ASSERT
        assertEquals(1, products.size());
        assertEquals("Tile", products.get(0).getProductType());
    }

    @Test
    void getAllTaxes_returnsFromDao() throws Exception {
        // ACT (arrange is in @BeforeEach via stub)
        List<Tax> taxes = service.getAllTaxes();

        // ASSERT
        assertEquals(1, taxes.size());
        assertEquals("KY", taxes.get(0).getStateAbbreviation());
    }


    private Order newOrder(String name, String state, String productType, String area) {
        Order o = new Order();
        o.setCustomerName(name);
        o.setState(state);
        o.setProductType(productType);
        o.setArea(new BigDecimal(area));
        return o;
    }
}
