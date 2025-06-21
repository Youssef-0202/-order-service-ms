package com.techie.microservice.order.dto;

import java.math.BigDecimal;

/**
 * @author HP
 **/
public record OrderRequest(
        Long id, String orderNumber, String skuCode ,
        BigDecimal price , Integer quantity , UserDetails userDetails)
{
  public record UserDetails (String email , String firstName, String lastName){}
}
