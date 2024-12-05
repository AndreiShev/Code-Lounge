package ru.skillbox.utils.impl;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.skillbox.dto.MessageDto;
import ru.skillbox.entity.Message;
import ru.skillbox.entity.Status;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MapperMessageToDtoTest {

    @Test
    void convertMessageToDto_shouldReturnCorrectDto() {
        Message message = Mockito.mock(Message.class);

        Long id = 1L;
        LocalDateTime time = LocalDateTime.of(2024, 10, 6, 12, 0);
        UUID authorId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();
        String messageText = "Hello, this is a test message";
        Status status = Status.SENT;

        Mockito.when(message.getId()).thenReturn(id);
        Mockito.when(message.getTime()).thenReturn(time);
        Mockito.when(message.getAuthor()).thenReturn(authorId);
        Mockito.when(message.getRecipient()).thenReturn(recipientId);
        Mockito.when(message.getMessageText()).thenReturn(messageText);
        Mockito.when(message.getStatus()).thenReturn(status);

        MessageDto dto = MapperMessageToDto.convertMessageToDto(message);

        assertNotNull(dto);

        assertEquals(id, dto.getId());
        assertEquals(time, dto.getTime());
        assertEquals(authorId, dto.getConversationPartner1());
        assertEquals(recipientId, dto.getConversationPartner2());
        assertEquals(messageText, dto.getMessageText());
        assertEquals(status, dto.getReadStatus());
    }

    @Test
    void convertMessageToDto_shouldReturnNullWhenMessageIsNull() {
        MessageDto dto = MapperMessageToDto.convertMessageToDto(null);

        assertNull(dto);
    }
}
