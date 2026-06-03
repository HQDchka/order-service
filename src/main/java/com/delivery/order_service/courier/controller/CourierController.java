package com.delivery.order_service.courier.controller;

import com.delivery.order_service.entity.Courier;
import com.delivery.order_service.courier.service.CourierService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/couriers")
@RequiredArgsConstructor
public class CourierController {

    private final CourierService courierService;

    @PostMapping
    public ResponseEntity<Courier> addCourier(@RequestBody Courier courier) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courierService.addCourier(courier));
    }

    @GetMapping
    public ResponseEntity<List<Courier>> getAllCouriers() {
        return ResponseEntity.ok(courierService.getAllCouriers());
    }

    @GetMapping("/free")
    public ResponseEntity<List<Courier>> getFreeCouriers() {
        return ResponseEntity.ok(courierService.getFreeCouriers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Courier> getCourierById(@PathVariable String id) {
        return ResponseEntity.ok(courierService.getCourierById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Courier> updateStatus(
            @PathVariable String id,
            @RequestParam Courier.CourierStatus status) {
        return ResponseEntity.ok(courierService.updateCourierStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourier(@PathVariable String id) {
        courierService.deleteCourier(id);
        return ResponseEntity.noContent().build();
    }
}