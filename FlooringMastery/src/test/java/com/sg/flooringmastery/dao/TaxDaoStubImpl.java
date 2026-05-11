package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Tax;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TaxDaoStubImpl implements TaxDao {

    public Tax onlyTax;

    public TaxDaoStubImpl() {
        onlyTax = new Tax();
        onlyTax.setStateAbbreviation("KY");
        onlyTax.setStateName("Kentucky");
        onlyTax.setTaxRate(new BigDecimal("6.00"));
    }

    public TaxDaoStubImpl(Tax tax) {
        this.onlyTax = tax;
    }

    @Override
    public List<Tax> getAllTaxes() {
        List<Tax> taxes = new ArrayList<>();
        taxes.add(onlyTax);
        return taxes;
    }

    @Override
    public Tax getTaxByState(String stateAbbreviation) {
        if (stateAbbreviation.equalsIgnoreCase(onlyTax.getStateAbbreviation())) {
            return onlyTax;
        }
        return null;
    }
}
