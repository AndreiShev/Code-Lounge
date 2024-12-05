package ru.skillbox.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.skillbox.service.NotificationService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaNotificationEventListener {
    private final NotificationService notificationService;


    @KafkaListener(
            topics = "${app.integration.kafka.notification.kafkaNotificationTopic}",
            groupId = "${app.integration.kafka.notification.kafkaMessageGroupId}",
            containerFactory = "kafkaNotificationListenerContainerFactory")
    public void listenNotificationMessage(
                                @Payload String kafkaNotificationMessage,
                                @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) UUID key,
                                @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                                @Header(KafkaHeaders.RECEIVED_TIMESTAMP) Long timestamp) {

        notificationService.addNotification(kafkaNotificationMessage, timestamp);
    }


}
