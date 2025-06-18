package com.techie.microservice.order.exception;

import lombok.Data;

/**
 * @author HP
 **/
@Data
public class InventoryNotFoundException extends RuntimeException {
    public InventoryNotFoundException(String message) {
        super(message);
    }

}
