package com.techie.microservice.order.service;

import com.techie.microservice.order.dto.OrderRequest;
import com.techie.microservice.order.exception.InsufficientInventoryException;
import com.techie.microservice.order.exception.InventoryNotFoundException;
import com.techie.microservice.order.exception.ProductNotFoundException;
import com.techie.microservice.order.inventoryCl.InventoryClient;
import com.techie.microservice.order.model.Order;
import com.techie.microservice.order.productCl.ProductClient;
import com.techie.microservice.order.repository.OrderRepository;
import feign.FeignException;
import org.springframework.stereotype.Service;

import java.util.List;

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
           throw  new ProductNotFoundException(request.skuCode());
        }

        // 2. Déduire directement la quantité (inventory-service gère tout)
       try {
           inventoryClient.updateBySkuCodeAfterOrder(request.skuCode(), request.quantity());
       }catch (FeignException.NotFound ex){
           throw new InventoryNotFoundException(ex.getMessage());
       }catch (FeignException.BadRequest ex) {
           String message = ex.contentUTF8();
           throw new InsufficientInventoryException(message);
       }

        // 3. Créer et enregistrer la commande
        Order order = Order.builder()
                   .id(request.id())
                   .orderNumber(request.orderNumber())
                   .price(request.price())
                   .quantity(request.quantity())
                   .skuCode(request.skuCode())
                   .build();
           orderRepository.save(order);

    }



    public void deleteAll(){
        orderRepository.deleteAll();
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }
}
