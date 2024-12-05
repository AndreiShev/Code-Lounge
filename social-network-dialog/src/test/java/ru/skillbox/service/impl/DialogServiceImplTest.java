package ru.skillbox.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.skillbox.dto.*;
import ru.skillbox.entity.Dialog;
import ru.skillbox.entity.Message;
import ru.skillbox.entity.Status;
import ru.skillbox.repository.DialogRepository;
import ru.skillbox.repository.MessageRepository;
import ru.skillbox.utils.GetCurrentUsername;


import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class DialogServiceImplTest {

    private MockedStatic<GetCurrentUsername> getCurrentUsernameMock;

    @Mock
    private DialogRepository dialogRepository;

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private DialogServiceImpl dialogServiceImpl;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        getCurrentUsernameMock = Mockito.mockStatic(GetCurrentUsername.class);
        getCurrentUsernameMock.when(GetCurrentUsername::getCurrentUsername).thenReturn(UUID.randomUUID().toString());

    }

    @AfterEach
    public void tearDown() {
        getCurrentUsernameMock.close();
    }

    @Test
    void setStatusMessageRead_shouldUpdateMessagesAndReturnOK() {
        UUID currentUserId = UUID.randomUUID();
        Long dialogId = 1L;
        Dialog dialog = new Dialog();
        dialog.setId(dialogId);
        Message message = new Message();
        message.setRecipient(currentUserId);
        message.setStatus(Status.SENT);
        List<Message> messages = List.of(message);
        dialog.setMessages(messages);

        when(GetCurrentUsername.getCurrentUsername()).thenReturn(currentUserId.toString());
        when(dialogRepository.findById(dialogId)).thenReturn(Optional.of(dialog));

        SetStatusMessageReadRs result = dialogServiceImpl.setStatusMessageRead(dialogId);

        assertNotNull(result);
        assertEquals("OK", result.getData().getMessage());
        verify(messageRepository, times(1)).saveAll(anyList());
    }

    @Test
    void setStatusMessageRead_shouldReturnNokWhenDialogNotFound() {
        UUID currentUserId = UUID.randomUUID();
        Long dialogId = 1L;

        when(GetCurrentUsername.getCurrentUsername()).thenReturn(currentUserId.toString());
        when(dialogRepository.findById(dialogId)).thenReturn(Optional.empty());

        SetStatusMessageReadRs result = dialogServiceImpl.setStatusMessageRead(dialogId);

        assertNotNull(result);
        assertEquals("NOK", result.getData().getMessage());
        verify(messageRepository, never()).saveAll(anyList());
    }

    @Test
    void getAllDialogs_shouldReturnDialogsForCurrentUser() {
        UUID currentUserId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);
        List<Dialog> dialogs = List.of(new Dialog());

        when(GetCurrentUsername.getCurrentUsername()).thenReturn(currentUserId.toString());
        when(dialogRepository.findByParticipantOneOrParticipantTwo(currentUserId, currentUserId))
                .thenReturn(dialogs);

        GetDialogsRs result = dialogServiceImpl.getAllDialogs(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(currentUserId, result.getCurrentUserId());
        verify(dialogRepository, times(1)).findByParticipantOneOrParticipantTwo(currentUserId, currentUserId);
    }

    @Test
    void getUnreadMessageCount_shouldReturnUnreadMessageCount() {
        long unreadCount = 5;
        when(messageRepository.countUnreadMessages(Status.SENT)).thenReturn(unreadCount);

        UnreadCountRs result = dialogServiceImpl.getUnreadMessageCount();

        assertNotNull(result);
        assertEquals(unreadCount, result.getData().getCount());
        verify(messageRepository, times(1)).countUnreadMessages(Status.SENT);
    }

    @Test
    void getAllMessages_shouldReturnMessagesForDialog() {
        UUID currentUserId = UUID.randomUUID();
        UUID companionId = UUID.randomUUID();
        Dialog dialog = new Dialog();
        dialog.setId(1L);
        Pageable pageable = PageRequest.of(0, 10);
        List<Message> messages = List.of(new Message());
        Page<Message> messagePage = new PageImpl<>(messages);

        when(GetCurrentUsername.getCurrentUsername()).thenReturn(currentUserId.toString());
        when(dialogRepository.findByParticipants(currentUserId, companionId)).thenReturn(Optional.of(dialog));
        when(messageRepository.findByDialog(dialog, pageable)).thenReturn(messagePage);

        GetMessagesRs result = dialogServiceImpl.getAllMessages(companionId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(Instant.now().toEpochMilli(), result.getTimestamp(), 1000);
        verify(dialogRepository, times(1)).findByParticipants(currentUserId, companionId);
        verify(messageRepository, times(1)).findByDialog(dialog, pageable);
    }

    @Test
    void getDialog_shouldReturnExistingDialog() {
        UUID currentUserId = UUID.randomUUID();
        UUID companionId = UUID.randomUUID();
        Dialog dialog = new Dialog();
        dialog.setId(1L);

        when(GetCurrentUsername.getCurrentUsername()).thenReturn(currentUserId.toString());
        when(dialogRepository.findByParticipants(currentUserId, companionId)).thenReturn(Optional.of(dialog));

        DialogDto result = dialogServiceImpl.getDialog(companionId);

        assertNotNull(result);
        assertEquals(dialog.getId(), result.getId());
        verify(dialogRepository, times(1)).findByParticipants(currentUserId, companionId);
    }

    @Test
    void getDialog_shouldCreateNewDialogIfNotExists() {
        UUID currentUserId = UUID.randomUUID();
        UUID companionId = UUID.randomUUID();
        Dialog newDialog = new Dialog();
        newDialog.setId(1L);

        when(GetCurrentUsername.getCurrentUsername()).thenReturn(currentUserId.toString());
        when(dialogRepository.findByParticipants(currentUserId, companionId)).thenReturn(Optional.empty());
        when(dialogRepository.save(any(Dialog.class))).thenReturn(newDialog);

        DialogDto result = dialogServiceImpl.getDialog(companionId);

        assertNotNull(result);
        assertEquals(newDialog.getId(), result.getId());
        verify(dialogRepository, times(1)).save(any(Dialog.class));
    }
}
