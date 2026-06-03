package com.delivery.order_service.restaurant.repository;

import com.delivery.order_service.entity.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, String> {
    List<Dish> findByRestaurantId(String restaurantId);
    List<Dish> findByRestaurantIdAndIsAvailableTrue(String restaurantId);
}