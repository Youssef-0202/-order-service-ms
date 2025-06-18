package com.techie.microservice.order.repository;

import com.techie.microservice.order.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author HP
 **/
public interface OrderRepository extends JpaRepository<Order,Long> {

    Optional<Order> findByOrderNumber(String aLong);
}
