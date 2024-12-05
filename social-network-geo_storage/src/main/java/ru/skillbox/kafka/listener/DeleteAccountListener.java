package ru.skillbox.kafka.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import ru.skillbox.service.StorageService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeleteAccountListener {
  private final StorageService storageService;

  @KafkaListener(topics = "${app.kafka.kafkaDeleteAccountTopic}", groupId = "${app.kafka.kafkaMessageGroupId}",
        containerFactory = "kafkaMessageConcurrentKafkaListenerContainerFactory")
  public void listen(@Payload UUID accountDeleteEventMessage,
                     @Header(value = KafkaHeaders.RECEIVED_KEY, required = false) UUID key,
                     @Header(value = KafkaHeaders.RECEIVED_TOPIC) String topic,
                     @Header(value = KafkaHeaders.RECEIVED_PARTITION) Integer partition,
                     @Header(value = KafkaHeaders.RECEIVED_TIMESTAMP) Long timestamp) {
    storageService.eraseAllUserImages(accountDeleteEventMessage); }
}
