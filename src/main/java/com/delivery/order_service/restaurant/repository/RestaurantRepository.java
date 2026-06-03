package com.delivery.order_service.restaurant.repository;

import com.delivery.order_service.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, String> {
    boolean existsByName(String name);

    @Query("SELECT r FROM Restaurant r WHERE r.openingTime <= :time AND r.closingTime >= :time")
    List<Restaurant> findOpenRestaurantsAt(@Param("time") LocalTime time);
}