package com.techie.microservice.order.repository;

import com.techie.microservice.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author HP
 **/
public interface OrderRepository extends JpaRepository<Order,Long> {

}
