package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Tax;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaxDaoFileImplTest {

    @TempDir
    Path tempDir;

    private TaxDaoFileImpl dao;

    @BeforeEach
    void setUp() throws IOException {
        Path taxesFile = tempDir.resolve("Taxes.txt");
        Files.writeString(taxesFile,
                "State,StateName,TaxRate\n" +
                "TX,Texas,4.45\n" +
                "KY,Kentucky,6.00\n" +
                "CA,California,25.00\n");
        dao = new TaxDaoFileImpl(taxesFile.toString());
    }

    @Test
    void getAllTaxes_returnsAllEntries() throws Exception {
        List<Tax> taxes = dao.getAllTaxes();
        assertEquals(3, taxes.size());
    }

    @Test
    void getTaxByState_existing_returnsTax() throws Exception {
        Tax tax = dao.getTaxByState("KY");
        assertNotNull(tax);
        assertEquals("Kentucky", tax.getStateName());
        assertEquals(0, new BigDecimal("6.00").compareTo(tax.getTaxRate()));
    }

    @Test
    void getTaxByState_lowercase_isCaseInsensitive() throws Exception {
        Tax tax = dao.getTaxByState("ky");
        assertNotNull(tax);
        assertEquals("KY", tax.getStateAbbreviation());
    }

    @Test
    void getTaxByState_unknown_returnsNull() throws Exception {
        Tax tax = dao.getTaxByState("ZZ");
        assertNull(tax);
    }
}
