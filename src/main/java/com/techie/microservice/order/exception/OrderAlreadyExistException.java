package com.techie.microservice.order.exception;

/**
 * @author HP
 **/
public class OrderAlreadyExistException extends RuntimeException{
    public OrderAlreadyExistException(String orderNumer){
        super("Order with reference :"+orderNumer+" is Already exists !" );
    }
}
