package com.sg.flooringmastery.dao;

import com.sg.flooringmastery.model.Product;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.util.*;

public class ProductDaoFileImpl implements ProductDao {

    private final String PRODUCTS_FILE;
    private static final String DELIMETER = ",";
    private Map<String, Product> products = new HashMap<>();

    public ProductDaoFileImpl() {
        PRODUCTS_FILE = "Data/Products.txt";
    }

    public ProductDaoFileImpl(String productsTextFile) {
        PRODUCTS_FILE = productsTextFile;
    }

    @Override
    public List<Product> getAllProducts() throws FlooringMasteryPersistenceException {
        loadProducts();
        return new ArrayList<>(products.values());
    }

    @Override
    public Product getProductByType(String productType) throws FlooringMasteryPersistenceException {
        loadProducts();
        return products.get(productType);
    }

    private Product unmarshallProduct(String productAsText) {
        // Format: ProductType,CostPerSquareFoot,LaborCostPerSquareFoot
        // Example: Tile,3.50,4.15
        String[] productToken = productAsText.split(DELIMETER);
        Product product = new Product();

        // Index 0: productType
        product.setProductType(productToken[0].trim());
        // Index 1 : costPerSquareFoot
        product.setCostPerSquareFoot(new BigDecimal(productToken[1].trim()));
        // Index 2: laborCostPerSquareFoot
        product.setLaborCostPerSquareFoot(new BigDecimal(productToken[2].trim()));
        return product;
    }

    private void loadProducts() throws FlooringMasteryPersistenceException {
        Scanner sc;

        try {
            sc = new Scanner(
                    new BufferedReader(
                            new FileReader(PRODUCTS_FILE)
                    )
            );
        } catch (FileNotFoundException e) {
            throw new FlooringMasteryPersistenceException("Could not load products data.");
        }
        // Skip the header
        sc.nextLine();
        String currentLine;
        Product currentProduct;
        while (sc.hasNextLine()) {
            currentLine = sc.nextLine();
            currentProduct = unmarshallProduct(currentLine);
            products.put(currentProduct.getProductType(), currentProduct);
        }
    }
}
