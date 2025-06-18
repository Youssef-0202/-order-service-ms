package com.techie.microservice.order.inventoryCl;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author HP
 **/
@FeignClient(value = "inventory",url = "${inventory.url}")
public interface InventoryClient {

    @RequestMapping(method = RequestMethod.GET , value = "/api/inventory/checkInStock")
    boolean isInStock(@RequestParam String skuCode , @RequestParam Integer quantity);

    @RequestMapping(method = RequestMethod.PUT,value = "/api/inventory/updateAfterOrder/{skuCode}/{quantityDemande}")
    public ResponseEntity<?> updateBySkuCodeAfterOrder(@PathVariable String skuCode, @PathVariable Integer quantityDemande );


}
