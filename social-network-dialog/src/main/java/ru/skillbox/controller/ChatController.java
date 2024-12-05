package ru.skillbox.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import ru.skillbox.annotation.Loggable;
import ru.skillbox.configuration.RabbitMQConfig;
import ru.skillbox.dto.MessageWebSocketDto;
import ru.skillbox.entity.Message;
import ru.skillbox.service.MessageConsumerService;

@Controller
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final RabbitTemplate rabbitTemplate;

    @MessageMapping("/chat.sendMessage")
    @Loggable
    public void sendMessage(MessageWebSocketDto messageWebSocketDTO) {

        String routingKey = "topic." + messageWebSocketDTO.getData().getId();
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, routingKey, messageWebSocketDTO);
    }

}
