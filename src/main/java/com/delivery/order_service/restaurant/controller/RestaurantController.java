package com.delivery.order_service.restaurant.controller;

import com.delivery.order_service.entity.Restaurant;
import com.delivery.order_service.entity.Dish;
import com.delivery.order_service.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<Restaurant> addRestaurant(@RequestBody Restaurant restaurant) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(restaurantService.addRestaurant(restaurant));
    }

    @GetMapping
    public ResponseEntity<List<Restaurant>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @GetMapping("/open")
    public ResponseEntity<List<Restaurant>> getOpenRestaurants() {
        return ResponseEntity.ok(restaurantService.getOpenRestaurants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getRestaurantById(@PathVariable String id) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Restaurant> updateRestaurant(@PathVariable String id, @RequestBody Restaurant restaurant) {
        return ResponseEntity.ok(restaurantService.updateRestaurant(id, restaurant));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable String id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{restaurantId}/dishes")
    public ResponseEntity<Dish> addDish(@PathVariable String restaurantId, @RequestBody Dish dish) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(restaurantService.addDish(restaurantId, dish));
    }

    @GetMapping("/{restaurantId}/dishes")
    public ResponseEntity<List<Dish>> getDishes(@PathVariable String restaurantId) {
        return ResponseEntity.ok(restaurantService.getDishesByRestaurant(restaurantId));
    }

    @PatchMapping("/dishes/{dishId}/availability")
    public ResponseEntity<Void> updateDishAvailability(
            @PathVariable String dishId,
            @RequestParam Boolean available) {
        restaurantService.updateDishAvailability(dishId, available);
        return ResponseEntity.ok().build();
    }
}