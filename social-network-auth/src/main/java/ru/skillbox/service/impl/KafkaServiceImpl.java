package ru.skillbox.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.skillbox.dto.kafka.KafkaActionEvent;
import ru.skillbox.dto.kafka.KafkaAuthEvent;
import ru.skillbox.entities.Account;
import ru.skillbox.service.KafkaService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaServiceImpl implements KafkaService {
    @Value("${app.kafka.kafkaAuthTopic}")
    private String authTopicName;

    @Value("${app.kafka.kafkaActionTopic}")
    private String actionEventTopicName;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void sendActionEvent(UUID uuid) {
        kafkaTemplate.send(actionEventTopicName, new KafkaActionEvent(uuid));
    }

    @Override
    public void sendAuthEvent(Account account) {
        kafkaTemplate.send(authTopicName, getKafkaAuthEvent(account));
    }

    private KafkaAuthEvent getKafkaAuthEvent(Account account) {
        return KafkaAuthEvent.builder()
                .uuid(account.getId())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .email(account.getEmail())
                .build();
    }
}
