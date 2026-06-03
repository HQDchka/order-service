package com.delivery.order_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonBackReference;
import java.math.BigDecimal;

@Entity
@Table(name = "dishes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dish {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    @Column(nullable = false)
    private Boolean isAvailable = true;
    
    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    @JsonBackReference
    private Restaurant restaurant;
}