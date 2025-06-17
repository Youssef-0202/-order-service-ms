package com.techie.microservice.order.service;

import com.techie.microservice.order.dto.OrderRequest;
import com.techie.microservice.order.model.Order;
import com.techie.microservice.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

/**
 * @author HP
 **/
@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void placeOrder(OrderRequest request) {
        Order order = Order.builder()
                .id(request.id())
                .orderNumber(request.orderNumber())
                .price(request.price())
                .quantity(request.quantity())
                .skuCode(request.skuCode())
                .build();
       orderRepository.save(order);
    }
}
