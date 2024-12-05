package ru.skillbox.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import ru.skillbox.configuration.RabbitMQConfig;
import ru.skillbox.dto.MessageWebSocketDto;
import ru.skillbox.dto.MessageWebSocketRs;

import static org.mockito.Mockito.*;

class ChatControllerTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private ChatController chatController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);  // Инициализация mock-объектов
    }

    @Test
    void sendMessage_sendsMessageToRabbitMQ() {
        // Arrange
        MessageWebSocketDto messageDto = new MessageWebSocketDto();
        MessageWebSocketRs dataDto = new MessageWebSocketRs();
        dataDto.setId(1L);
        messageDto.setData(dataDto);

        String expectedRoutingKey = "topic." + dataDto.getId();

        chatController.sendMessage(messageDto);

        verify(rabbitTemplate).convertAndSend(RabbitMQConfig.EXCHANGE_NAME, expectedRoutingKey, messageDto);
    }
}
