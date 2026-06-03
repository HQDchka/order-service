package com.delivery.order_service.order.repository;

import com.delivery.order_service.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByClientId(String clientId);
    List<Order> findByRestaurantId(String restaurantId);
    List<Order> findByCourierId(String courierId);
    List<Order> findByStatus(Order.OrderStatus status);
}