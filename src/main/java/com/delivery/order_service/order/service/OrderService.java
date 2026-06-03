package com.delivery.order_service.order.service;

import com.delivery.order_service.entity.*;
import com.delivery.order_service.client.repository.ClientRepository;
import com.delivery.order_service.restaurant.repository.RestaurantRepository;
import com.delivery.order_service.restaurant.repository.DishRepository;
import com.delivery.order_service.courier.repository.CourierRepository;
import com.delivery.order_service.order.repository.OrderRepository;
import com.delivery.order_service.order.repository.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ClientRepository clientRepository;
    private final RestaurantRepository restaurantRepository;
    private final DishRepository dishRepository;
    private final CourierRepository courierRepository;

    @Transactional
    public Order createOrder(Order order, List<OrderItemRequest> items) {
        log.info("Создание нового заказа от клиента: {}", order.getClient().getId());

        // 1. Проверка существования клиента
        Client client = clientRepository.findById(order.getClient().getId())
                .orElseThrow(() -> new RuntimeException("Клиент не найден"));
        order.setClient(client);

        // 2. Проверка существования ресторана и его работы
        Restaurant restaurant = restaurantRepository.findById(order.getRestaurant().getId())
                .orElseThrow(() -> new RuntimeException("Ресторан не найден"));

        LocalTime now = LocalTime.now();
        if (now.isBefore(restaurant.getOpeningTime()) || now.isAfter(restaurant.getClosingTime())) {
            throw new RuntimeException("Ресторан не работает в текущее время. Часы работы: "
                    + restaurant.getOpeningTime() + " - " + restaurant.getClosingTime());
        }
        order.setRestaurant(restaurant);

        // 3. Установка начальных значений
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.NEW);

        Order savedOrder = orderRepository.save(order);

        // 4. Добавление позиций заказа
        for (OrderItemRequest itemReq : items) {
            Dish dish = dishRepository.findById(itemReq.getDishId())
                    .orElseThrow(() -> new RuntimeException("Блюдо не найдено: " + itemReq.getDishId()));

            if (!dish.getIsAvailable()) {
                throw new RuntimeException("Блюдо '" + dish.getName() + "' временно недоступно");
            }

            if (!dish.getRestaurant().getId().equals(restaurant.getId())) {
                throw new RuntimeException("Блюдо '" + dish.getName() + "' не принадлежит выбранному ресторану");
            }

            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setDish(dish);
            item.setQuantity(itemReq.getQuantity());

            orderItemRepository.save(item);
        }

        log.info("Заказ создан с ID: {}", savedOrder.getId());
        return savedOrder;
    }

    @Transactional
    public Order updateOrderStatus(String orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));

        validateStatusTransition(order.getStatus(), status);
        order.setStatus(status);

        // Если заказ готов к выдаче — ищем свободного курьера
        if (status == Order.OrderStatus.READY_FOR_PICKUP) {
            assignCourierIfAvailable(order);
        }

        return orderRepository.save(order);
    }

    private void validateStatusTransition(Order.OrderStatus current, Order.OrderStatus next) {
        if (current == Order.OrderStatus.CANCELLED || current == Order.OrderStatus.DELIVERED) {
            throw new RuntimeException("Нельзя изменить статус завершённого или отменённого заказа");
        }

        switch (current) {
            case NEW:
                if (next != Order.OrderStatus.COOKING && next != Order.OrderStatus.CANCELLED) {
                    throw new RuntimeException("Из статуса NEW можно перейти только в COOKING или CANCELLED");
                }
                break;
            case COOKING:
                if (next != Order.OrderStatus.READY_FOR_PICKUP && next != Order.OrderStatus.CANCELLED) {
                    throw new RuntimeException("Из статуса COOKING можно перейти только в READY_FOR_PICKUP или CANCELLED");
                }
                break;
            case READY_FOR_PICKUP:
                if (next != Order.OrderStatus.IN_DELIVERY) {
                    throw new RuntimeException("Из статуса READY_FOR_PICKUP можно перейти только в IN_DELIVERY");
                }
                break;
            case IN_DELIVERY:
                if (next != Order.OrderStatus.DELIVERED) {
                    throw new RuntimeException("Из статуса IN_DELIVERY можно перейти только в DELIVERED");
                }
                break;
            default:
                break;
        }
    }

    private void assignCourierIfAvailable(Order order) {
        courierRepository.findFirstByStatus(Courier.CourierStatus.FREE)
                .ifPresent(courier -> {
                    courier.setStatus(Courier.CourierStatus.BUSY);
                    order.setCourier(courier);
                    order.setStatus(Order.OrderStatus.IN_DELIVERY);
                    log.info("Назначен курьер {} на заказ {}", courier.getName(), order.getId());
                });
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByClient(String clientId) {
        return orderRepository.findByClientId(clientId);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByRestaurant(String restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Order getOrderById(String id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Заказ не найден"));
    }

    @Transactional
    public void cancelOrder(String orderId) {
        Order order = getOrderById(orderId);

        if (order.getStatus() == Order.OrderStatus.DELIVERED) {
            throw new RuntimeException("Нельзя отменить доставленный заказ");
        }

        if (order.getStatus() == Order.OrderStatus.IN_DELIVERY) {
            Courier courier = order.getCourier();
            if (courier != null) {
                courier.setStatus(Courier.CourierStatus.FREE);
            }
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        orderRepository.save(order);
        log.info("Заказ {} отменён", orderId);
    }

    // Вспомогательный класс для запроса
    public static class OrderItemRequest {
        private String dishId;
        private Integer quantity;

        public String getDishId() { return dishId; }
        public void setDishId(String dishId) { this.dishId = dishId; }
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}