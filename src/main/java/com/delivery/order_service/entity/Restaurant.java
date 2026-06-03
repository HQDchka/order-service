package com.delivery.order_service.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "restaurants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Restaurant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String address;
    
    @Column(nullable = false)
    private LocalTime openingTime;
    
    @Column(nullable = false)
    private LocalTime closingTime;
    
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Dish> dishes = new ArrayList<>();
    
    @OneToMany(mappedBy = "restaurant")
    @JsonManagedReference
    private List<Order> orders = new ArrayList<>();
}