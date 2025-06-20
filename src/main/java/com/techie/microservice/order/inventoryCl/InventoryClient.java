package com.techie.microservice.order.inventoryCl;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PutExchange;

/**
 * @author HP
 **/
@HttpExchange("/api/inventory/")
public interface InventoryClient {


    @GetExchange("checkInStock")
    boolean isInStock(@RequestParam String skuCode , @RequestParam Integer quantity);


    @PutExchange("updateAfterOrder/{skuCode}/{quantityDemande}")
    void updateBySkuCodeAfterOrder(@PathVariable String skuCode, @PathVariable Integer quantityDemande );


}


