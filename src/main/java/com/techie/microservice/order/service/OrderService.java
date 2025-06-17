package com.techie.microservice.order.service;

import com.techie.microservice.order.dto.OrderRequest;
import com.techie.microservice.order.inventoryCl.InventoryClient;
import com.techie.microservice.order.model.Order;
import com.techie.microservice.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

/**
 * @author HP
 **/
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;

    public OrderService(OrderRepository orderRepository, InventoryClient inventoryClient) {
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
    }

    public void placeOrder(OrderRequest request) {

        boolean isProductInStock = inventoryClient.isInStock(request.skuCode(), request.quantity());

       if(isProductInStock){
           Order order = Order.builder()
                   .id(request.id())
                   .orderNumber(request.orderNumber())
                   .price(request.price())
                   .quantity(request.quantity())
                   .skuCode(request.skuCode())
                   .build();
           orderRepository.save(order);
       }
       else {
           throw new  RuntimeException("Product with SkuCode : " + request.skuCode() +" is not in stock");
       }
    }
}
