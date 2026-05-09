package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Product;
import com.sg.flooringmastery.model.Tax;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.*;

public class TaxDaoFileImpl implements TaxDao {

    private static final String TAXES_FILE = "Data/Taxes.txt";
    private static final String DELIMITER = ",";
    private Map<String, Tax> taxes = new HashMap<>();
    @Override
    public List<Tax> getAllTaxes() throws FlooringMasteryPersistenceException {
        loadTaxes();
        return new ArrayList<>(taxes.values());
    }

    @Override
    public Tax getTaxByState(String stateAbbreviation) throws FlooringMasteryPersistenceException {
        loadTaxes();
        return taxes.get(stateAbbreviation.toUpperCase());
    }

    private void loadTaxes() throws FlooringMasteryPersistenceException {
        Scanner sc;

        try {
            sc = new Scanner(
                    new BufferedReader(
                            new FileReader(TAXES_FILE)
                    )
            );
        } catch (FileNotFoundException e) {
            throw new FlooringMasteryPersistenceException("Could not load taxes data.");
        }
        sc.nextLine(); // skip the header
        String currentLine;
        Tax currentTax;
        while (sc.hasNextLine()) {
            currentLine = sc.nextLine();
            currentTax = unmarshallTax(currentLine);
            taxes.put(currentTax.getStateAbbreviation().toUpperCase(), currentTax);
        }
    }

    private Tax unmarshallTax(String taxAsLine) {
        // Format: StateAbbreviation,StateName,TaxRate
        // Example: TX,Texas,4.45
        String[] taxToken = taxAsLine.split(DELIMITER);
        Tax tax = new Tax();

        // Index 0: stateAbbreviation
        tax.setStateAbbreviation(taxToken[0].trim());
        // Index 1: stateName
        tax.setStateName(taxToken[1].trim());
        // Index 2: taxRate
        tax.setTaxRate(new BigDecimal(taxToken[2].trim()));

        return tax;
    }
}
