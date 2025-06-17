package com.techie.microservice.order.controller;

import com.techie.microservice.order.dto.OrderRequest;
import com.techie.microservice.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author HP
 **/
@RestController
@RequestMapping("/api/order")
public class OrderController {
    private final OrderService orderService;


    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public String placeOrder(@RequestBody OrderRequest request){
        orderService.placeOrder(request);
        return "Order placed Successfully";
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAll(){
        orderService.deleteAll();
        return ResponseEntity.status(HttpStatus.OK).body("All orders are deleted !");
    }

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
}
