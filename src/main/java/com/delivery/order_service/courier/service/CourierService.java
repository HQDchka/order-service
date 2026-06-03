package com.delivery.order_service.courier.service;

import com.delivery.order_service.entity.Courier;
import com.delivery.order_service.courier.repository.CourierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CourierService {

    private final CourierRepository courierRepository;

    @Transactional
    public Courier addCourier(Courier courier) {
        log.info("Добавление курьера: {}", courier.getName());

        if (courierRepository.existsByPhone(courier.getPhone())) {
            throw new RuntimeException("Курьер с телефоном " + courier.getPhone() + " уже существует");
        }

        if (courier.getStatus() == null) {
            courier.setStatus(Courier.CourierStatus.FREE);
        }

        return courierRepository.save(courier);
    }

    @Transactional(readOnly = true)
    public List<Courier> getAllCouriers() {
        return courierRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Courier getCourierById(String id) {
        return courierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Курьер с ID " + id + " не найден"));
    }

    @Transactional(readOnly = true)
    public List<Courier> getFreeCouriers() {
        return courierRepository.findByStatus(Courier.CourierStatus.FREE);
    }

    @Transactional
    public Courier updateCourierStatus(String id, Courier.CourierStatus status) {
        Courier courier = getCourierById(id);
        courier.setStatus(status);
        return courierRepository.save(courier);
    }

    @Transactional
    public void deleteCourier(String id) {
        Courier courier = getCourierById(id);
        courierRepository.delete(courier);
    }
}