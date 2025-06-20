package com.techie.microservice.order.productCl;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

/**
 * @author HP
 **/
@HttpExchange("/api/product")
public interface ProductClient {

    @GetExchange("/exist/{skuCode}")
    ResponseEntity<Boolean> isExistBySkuCode(@PathVariable String skuCode);

}
