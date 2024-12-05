package ru.skillbox.utils.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.skillbox.dto.DialogDto;
import ru.skillbox.dto.ShortMessageForDialogDto;
import ru.skillbox.entity.Dialog;
import ru.skillbox.entity.Message;
import ru.skillbox.entity.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MapperDialogToDtoTest {

    private Dialog dialog;
    private UUID currentUserId;
    private UUID companionId;

    @BeforeEach
    void setUp() {
        currentUserId = UUID.randomUUID();
        companionId = UUID.randomUUID();

        dialog = new Dialog();
        dialog.setId(1L);
        dialog.setParticipantOne(currentUserId);
        dialog.setParticipantTwo(companionId);

        Message message1 = new Message();
        message1.setId(1L);
        message1.setAuthor(companionId);
        message1.setRecipient(currentUserId);
        message1.setStatus(Status.SENT);
        message1.setTime(LocalDateTime.now());

        Message message2 = new Message();
        message2.setId(2L);
        message2.setAuthor(currentUserId);
        message2.setRecipient(companionId);
        message2.setStatus(Status.READ);
        message2.setTime(LocalDateTime.now().minusMinutes(1));

        dialog.setMessages(List.of(message1, message2));
    }

    @Test
    void convertDialogToDto_shouldMapDialogToDtoCorrectly() {
        DialogDto dialogDto = MapperDialogToDto.convertDialogToDto(dialog, currentUserId);

        assertNotNull(dialogDto);
        assertEquals(dialog.getId(), dialogDto.getId());
        assertEquals(dialog.getParticipantOne(), dialogDto.getConversationPartner1());
        assertEquals(dialog.getParticipantTwo(), dialogDto.getConversationPartner2());

        assertEquals(1, dialogDto.getUnreadCount());

        List<ShortMessageForDialogDto> lastMessages = dialogDto.getLastMessage();
        assertNotNull(lastMessages);
        assertEquals(2, lastMessages.size());
        assertTrue(lastMessages.get(0).getTime().isAfter(lastMessages.get(1).getTime()));
    }

    @Test
    void convertDialogToDto_shouldReturnNullForNullDialog() {
        DialogDto dialogDto = MapperDialogToDto.convertDialogToDto(null, currentUserId);

        assertNull(dialogDto);
    }

    @Test
    void convertDialogToDto_shouldReturnZeroUnreadCountWhenAllMessagesAreFromCurrentUser() {
        Message message1 = new Message();
        message1.setId(1L);
        message1.setAuthor(currentUserId);
        message1.setRecipient(companionId);
        message1.setStatus(Status.SENT);
        message1.setTime(LocalDateTime.now());

        dialog.setMessages(List.of(message1));

        DialogDto dialogDto = MapperDialogToDto.convertDialogToDto(dialog, currentUserId);

        assertNotNull(dialogDto);
        assertEquals(0, dialogDto.getUnreadCount());
    }
}
