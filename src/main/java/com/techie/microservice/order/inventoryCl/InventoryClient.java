package com.techie.microservice.order.inventoryCl;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author HP
 **/
@FeignClient(value = "inventory",url = "${inventory.url}")
public interface InventoryClient {

    @RequestMapping(method = RequestMethod.GET , value = "/api/inventory")
    boolean isInStock(@RequestParam String skuCode , @RequestParam Integer quantity);
}
