package com.delivery.order_service.client.repository;

import com.delivery.order_service.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
    Optional<Client> findByPhone(String phone);
    boolean existsByPhone(String phone);
}