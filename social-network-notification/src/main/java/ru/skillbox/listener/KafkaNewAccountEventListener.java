package ru.skillbox.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.skillbox.service.NotificationSettingService;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaNewAccountEventListener {
    private final NotificationSettingService notificationSettingService;

    @KafkaListener(
            topics = "${app.integration.kafka.account.kafkaNewAccountTopic}",
            groupId = "${app.integration.kafka.account.kafkaMessageGroupId}",
            containerFactory = "kafkaNotificationListenerContainerFactory")
    public void listenNewAccountEvent(
            @Payload String newAccountEvent,
            @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) UUID key,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
            @Header(KafkaHeaders.RECEIVED_TIMESTAMP) Long timestamp) {

        notificationSettingService.createUserNotificationSettings(newAccountEvent);
    }

}
