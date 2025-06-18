package com.techie.microservice.order.exception;

import lombok.Data;

/**
 * @author HP
 **/
    @Data
    public class InsufficientInventoryException extends RuntimeException {


        public InsufficientInventoryException(String message) {
            super(message);
        }
    }
