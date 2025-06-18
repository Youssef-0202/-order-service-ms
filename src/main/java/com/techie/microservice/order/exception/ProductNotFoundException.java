package com.techie.microservice.order.exception;

/**
 * @author HP
 **/
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String skuCode){
        super("Product with skuCode: " + skuCode + " not found!");
    }
}
