package com.delivery.order_service.restaurant.service;

import com.delivery.order_service.entity.Restaurant;
import com.delivery.order_service.entity.Dish;
import com.delivery.order_service.restaurant.repository.RestaurantRepository;
import com.delivery.order_service.restaurant.repository.DishRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final DishRepository dishRepository;

    @Transactional
    public Restaurant addRestaurant(Restaurant restaurant) {
        log.info("Добавление ресторана: {}", restaurant.getName());

        if (restaurantRepository.existsByName(restaurant.getName())) {
            throw new RuntimeException("Ресторан с именем " + restaurant.getName() + " уже существует");
        }

        if (restaurant.getOpeningTime().isAfter(restaurant.getClosingTime())) {
            throw new RuntimeException("Время открытия не может быть позже времени закрытия");
        }

        return restaurantRepository.save(restaurant);
    }

    @Transactional(readOnly = true)
    public List<Restaurant> getAllRestaurants() {
        return restaurantRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Restaurant getRestaurantById(String id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ресторан с ID " + id + " не найден"));
    }

    @Transactional(readOnly = true)
    public List<Restaurant> getOpenRestaurants() {
        return restaurantRepository.findOpenRestaurantsAt(LocalTime.now());
    }

    @Transactional
    public Restaurant updateRestaurant(String id, Restaurant restaurantData) {
        Restaurant restaurant = getRestaurantById(id);

        restaurant.setName(restaurantData.getName());
        restaurant.setAddress(restaurantData.getAddress());
        restaurant.setOpeningTime(restaurantData.getOpeningTime());
        restaurant.setClosingTime(restaurantData.getClosingTime());

        return restaurantRepository.save(restaurant);
    }

    @Transactional
    public void deleteRestaurant(String id) {
        Restaurant restaurant = getRestaurantById(id);
        restaurantRepository.delete(restaurant);
    }

    @Transactional
    public Dish addDish(String restaurantId, Dish dish) {
        Restaurant restaurant = getRestaurantById(restaurantId);
        dish.setRestaurant(restaurant);
        return dishRepository.save(dish);
    }

    @Transactional(readOnly = true)
    public List<Dish> getDishesByRestaurant(String restaurantId) {
        return dishRepository.findByRestaurantId(restaurantId);
    }

    @Transactional
    public void updateDishAvailability(String dishId, Boolean isAvailable) {
        Dish dish = dishRepository.findById(dishId)
                .orElseThrow(() -> new RuntimeException("Блюдо не найдено"));
        dish.setIsAvailable(isAvailable);
        dishRepository.save(dish);
    }
}