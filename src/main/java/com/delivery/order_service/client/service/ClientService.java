package com.delivery.order_service.client.service;

import com.delivery.order_service.entity.Client;
import com.delivery.order_service.client.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    @Transactional
    public Client addClient(Client client) {
        log.info("Добавление нового клиента с телефоном: {}", client.getPhone());

        if (clientRepository.existsByPhone(client.getPhone())) {
            throw new RuntimeException("Клиент с телефоном " + client.getPhone() + " уже существует");
        }

        return clientRepository.save(client);
    }

    @Transactional(readOnly = true)
    public List<Client> getAllClients() {
        log.info("Получение всех клиентов");
        return clientRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Client getClientById(String id) {
        log.info("Получение клиента по ID: {}", id);
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Клиент с ID " + id + " не найден"));
    }

    @Transactional(readOnly = true)
    public Client getClientByPhone(String phone) {
        log.info("Получение клиента по телефону: {}", phone);
        return clientRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Клиент с телефоном " + phone + " не найден"));
    }

    @Transactional
    public void deleteClient(String id) {
        log.info("Удаление клиента по ID: {}", id);
        if (!clientRepository.existsById(id)) {
            throw new RuntimeException("Клиент с ID " + id + " не найден");
        }
        clientRepository.deleteById(id);
    }

    @Transactional
    public Client updateClient(String id, Client clientData) {
        log.info("Обновление клиента с ID: {}", id);

        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Клиент с ID " + id + " не найден"));

        if (!client.getPhone().equals(clientData.getPhone()) &&
                clientRepository.existsByPhone(clientData.getPhone())) {
            throw new RuntimeException("Телефон " + clientData.getPhone() + " уже используется");
        }

        client.setName(clientData.getName());
        client.setPhone(clientData.getPhone());
        client.setDefaultAddress(clientData.getDefaultAddress());

        return clientRepository.save(client);
    }
}