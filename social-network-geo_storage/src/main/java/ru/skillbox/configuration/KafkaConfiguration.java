package ru.skillbox.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
public class KafkaConfiguration {
  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Value("${app.kafka.kafkaMessageGroupId}")
  private String kafkaMessageGroupId;

  @Bean
  public ConsumerFactory<String, UUID> kafkaMessageConsumerFactory(ObjectMapper objectMapper) {
    Map<String, Object> config = new HashMap<>();

    config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaMessageGroupId);
    config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
    config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

    return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(), new JsonDeserializer<>(objectMapper));
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, UUID> kafkaMessageConcurrentKafkaListenerContainerFactory (
        ConsumerFactory<String, UUID> kafkaMessageConsumerFactory) {
    ConcurrentKafkaListenerContainerFactory<String, UUID> factory = new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(kafkaMessageConsumerFactory);

    return factory;
  }
}
