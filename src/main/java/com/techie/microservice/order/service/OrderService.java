package com.techie.microservice.order.service;

import com.techie.microservice.order.dto.OrderRequest;
import com.techie.microservice.order.event.OrderPlacedEvent;
import com.techie.microservice.order.exception.*;
import com.techie.microservice.order.inventoryCl.InventoryClient;
import com.techie.microservice.order.model.Order;
import com.techie.microservice.order.productCl.ProductClient;
import com.techie.microservice.order.repository.OrderRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

/**
 * @author HP
 **/
@Service
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final ProductClient productClient;
    private final KafkaTemplate<String,OrderPlacedEvent> kafkaTemplate;

    public OrderService(OrderRepository orderRepository, InventoryClient inventoryClient, ProductClient productClient, KafkaTemplate<String, OrderPlacedEvent> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.inventoryClient = inventoryClient;
        this.productClient = productClient;
        this.kafkaTemplate = kafkaTemplate;
    }


    public void placeOrder(OrderRequest request) {

        // 1. verifier si le code est correctement entrer (existe un produit avec ce code )
        boolean isProductIsExist = productClient.isExistBySkuCode(request.skuCode()).getBody();

        if (!isProductIsExist) {
           throw  new ProductNotFoundException(request.skuCode());
        }

        // 2. Déduire directement la quantité (inventory-service gère tout)
       try {
           callInventoryUpdate(request);
       }catch (HttpClientErrorException.NotFound ex){
           throw new InventoryNotFoundException(ex.getMessage());
       }catch (HttpClientErrorException.BadRequest ex) {
           String message = ex.getResponseBodyAsString();
           throw new InsufficientInventoryException(message);
       } catch (InventoryServiceUnavailableException ex) {
           throw ex;
       } catch (Exception ex) {
           throw new RuntimeException("Erreur inconnue lors de la mise à jour de l’inventaire", ex);
       }

        // 3. Créer et enregistrer la commande

      if(!this.orderExistsByOrderNumber(request.orderNumber())){
          Order order = Order.builder()
                  .id(request.id())
                  .orderNumber(request.orderNumber())
                  .price(request.price())
                  .quantity(request.quantity())
                  .skuCode(request.skuCode())
                  .userEmail(request.userDetails().email())
                  .build();
          orderRepository.save(order);

          // 4. Send a message to kafka Topic (orderNumber - Email)
          OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent();
          orderPlacedEvent.setOrderNumber(request.orderNumber());
          orderPlacedEvent.setEmail(request.userDetails().email());
          orderPlacedEvent.setFirstName(request.userDetails().firstName());
          orderPlacedEvent.setLastName(request.userDetails().lastName());
          log.info("Start - Sending OrderPlacedEvent {} to kafka topic" +
                  " order-placed",orderPlacedEvent);
          kafkaTemplate.send("order-placed",orderPlacedEvent);
          log.info("End - Sending OrderPlacedEvent {} to kafka topic" +
                  " order-placed",orderPlacedEvent);

      }else {
          throw new OrderAlreadyExistException(request.orderNumber());
      }

    }
    // todo : CircuitBreaker for InventoryService rest pas configurer !! (pas de monitoring)
    // todo : Add CircuitBreaker Configuration to ProductService
    @CircuitBreaker(name = "inventory", fallbackMethod = "fallbackInventoryUpdate")
    @Retry(name = "inventory")
    public void callInventoryUpdate(OrderRequest request) {
        inventoryClient.updateBySkuCodeAfterOrder(request.skuCode(), request.quantity());
    }


    public void fallbackInventoryUpdate(OrderRequest request, Throwable throwable) {
        log.warn("⚠️ Fallback triggered for SKU: {}, qty: {}. Cause: {}",
                request.skuCode(), request.quantity(), throwable.toString());
        throw new InventoryServiceUnavailableException(
                "Inventory Service temporairement indisponible", throwable);
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
