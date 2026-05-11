package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Product;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductDaoStubImpl implements ProductDao {

    public Product onlyProduct;

    public ProductDaoStubImpl() {
        onlyProduct = new Product();
        onlyProduct.setProductType("Tile");
        onlyProduct.setCostPerSquareFoot(new BigDecimal("3.50"));
        onlyProduct.setLaborCostPerSquareFoot(new BigDecimal("4.15"));
    }

    public ProductDaoStubImpl(Product product) {
        this.onlyProduct = product;
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        products.add(onlyProduct);
        return products;
    }

    @Override
    public Product getProductByType(String productType) {
        if (productType.equalsIgnoreCase(onlyProduct.getProductType())) {
            return onlyProduct;
        }
        return null;
    }
}
