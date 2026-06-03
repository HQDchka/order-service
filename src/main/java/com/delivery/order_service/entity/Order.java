package com.delivery.order_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    @Column(nullable = false)
    private String deliveryAddress;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    @JsonBackReference
    private Client client;
    
    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonBackReference
    private Restaurant restaurant;
    
    @ManyToOne
    @JoinColumn(name = "courier_id")
    @JsonBackReference
    private Courier courier;
    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<OrderItem> items = new ArrayList<>();
    
    public enum OrderStatus {
        NEW, COOKING, READY_FOR_PICKUP, IN_DELIVERY, DELIVERED, CANCELLED
    }
}