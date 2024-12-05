package ru.skillbox.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.skillbox.event.NotificationEvent;


@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaTemplate<String, NotificationEvent> template;
    @Value("${app.kafka.notificationTopic}")
    private String topic;

    public void sendNotification(NotificationEvent notificationEvent) {
        template.send(topic, notificationEvent);
    }
}
