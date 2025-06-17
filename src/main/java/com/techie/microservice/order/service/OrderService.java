package com.techie.microservice.order.service;

import com.techie.microservice.order.dto.OrderRequest;
import com.techie.microservice.order.inventoryCl.InventoryClient;
import com.techie.microservice.order.model.Order;
import com.techie.microservice.order.productCl.ProductClient;
import com.techie.microservice.order.repository.OrderRepository;
import org.springframework.stereotype.Service;

/**
 * @author HP
 **/
@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final ProductClient productClient;

    public OrderService(OrderRepository orderRepository, InventoryClient inventoryClient, ProductClient productClient) {
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
        this.productClient = productClient;
    }

    public void placeOrder(OrderRequest request) {

        // verifier si le code est correctement entrer (existe un produit avec ce code )
        boolean isProductIsExist = productClient.isExistBySkuCode(request.skuCode()).getBody();

        if (!isProductIsExist) {
            throw new RuntimeException("Invalid product SKU: " + request.skuCode());
        }

        // verifier si la quantitie demande est  existe
        boolean isProductInStock = inventoryClient.isInStock(request.skuCode(), request.quantity());

       if(isProductInStock){
           // todo : delete number of quantity ordred  from stockage !!

           // confirmer l'ordre
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
           throw new  RuntimeException("Quantity of product with SkuCode : " + request.skuCode() +" is not suffisant !!");
       }
    }



    public void deleteAll(){
        orderRepository.deleteAll();
    }
}
