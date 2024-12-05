package ru.skillbox.service;

import ru.skillbox.entities.Account;

import java.util.UUID;

public interface KafkaService {

    void sendActionEvent(UUID uuid);

    void sendAuthEvent(Account account);
}
