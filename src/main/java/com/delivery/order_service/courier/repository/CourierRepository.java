package com.delivery.order_service.courier.repository;

import com.delivery.order_service.entity.Courier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourierRepository extends JpaRepository<Courier, String> {
    Optional<Courier> findFirstByStatus(Courier.CourierStatus status);
    List<Courier> findByStatus(Courier.CourierStatus status);
    boolean existsByPhone(String phone);
}