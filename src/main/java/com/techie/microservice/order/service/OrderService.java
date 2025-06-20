package com.techie.microservice.order.service;

import com.techie.microservice.order.dto.OrderRequest;
import com.techie.microservice.order.exception.InsufficientInventoryException;
import com.techie.microservice.order.exception.InventoryNotFoundException;
import com.techie.microservice.order.exception.OrderAlreadyExistException;
import com.techie.microservice.order.exception.ProductNotFoundException;
import com.techie.microservice.order.inventoryCl.InventoryClient;
import com.techie.microservice.order.model.Order;
import com.techie.microservice.order.productCl.ProductClient;
import com.techie.microservice.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;

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
       }catch (HttpClientErrorException.NotFound ex){
           throw new InventoryNotFoundException(ex.getMessage());
       }catch (HttpClientErrorException.BadRequest ex) {
           String message = ex.getResponseBodyAsString();
           throw new InsufficientInventoryException(message);
       } catch (RestClientResponseException ex) {
           // Other HTTP status errors (4xx, 5xx)
           throw new RuntimeException("Inventory update failed: " + ex.getStatusText(), ex);
       }

        // 3. Créer et enregistrer la commande

      if(!this.orderExistsByOrderNumber(request.orderNumber())){
          Order order = Order.builder()
                  .id(request.id())
                  .orderNumber(request.orderNumber())
                  .price(request.price())
                  .quantity(request.quantity())
                  .skuCode(request.skuCode())
                  .build();
          orderRepository.save(order);
      }else {
          throw new OrderAlreadyExistException(request.orderNumber());
      }

    }

    public boolean orderExistsByOrderNumber(String orderNumber){
        return   orderRepository.findByOrderNumber(orderNumber).isPresent();

    }



    public void deleteAll(){
        orderRepository.deleteAll();
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }
}
