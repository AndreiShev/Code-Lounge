package ru.skillbox.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.skillbox.dto.*;
import ru.skillbox.entity.Dialog;
import ru.skillbox.entity.Message;
import ru.skillbox.entity.Status;
import ru.skillbox.repository.DialogRepository;
import ru.skillbox.repository.MessageRepository;
import ru.skillbox.service.DialogService;
import ru.skillbox.utils.GetCurrentUsername;
import ru.skillbox.utils.impl.MapperDialogToDto;
import ru.skillbox.utils.impl.MapperMessageToDto;


import java.time.Instant;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DialogServiceImpl implements DialogService {

    private final DialogRepository dialogRepository;
    private final MessageRepository messageRepository;


    @Override
    @Transactional
    public SetStatusMessageReadRs setStatusMessageRead(Long dialogId) {
        SetStatusMessageReadRs response = new SetStatusMessageReadRs();
        UUID currentUser = UUID.fromString(GetCurrentUsername.getCurrentUsername());
        try {
            Dialog dialog = dialogRepository
                    .findById(dialogId)
                    .orElseThrow(() -> new EntityNotFoundException("Диалог не найден"));

            List<Message> udatedMessages = dialog.getMessages().stream()
                    .filter(message -> message.getRecipient().equals(currentUser))
                    .filter(message -> message.getStatus() == Status.SENT)
                    .peek(message -> message.setStatus(Status.READ))
                            .toList();
            messageRepository.saveAll(udatedMessages);
            response.setData(SetStatusMessageReadDto.builder().message("OK").build());

        } catch (EntityNotFoundException e) {
            response.setData(SetStatusMessageReadDto.builder().message("NOK").build());
            response.setError("Ошибка");
            response.setErrorDescription(e.getMessage());
        }
        response.setTimestamp(Instant.now().toEpochMilli());
        return response;

    }

    @Override
    public GetDialogsRs getAllDialogs(Pageable pageable) {

        List<DialogDto> dialogDtoList = new ArrayList<>();
        UUID currentUser = UUID.fromString(GetCurrentUsername.getCurrentUsername());
        try {
            List<Dialog> dialogs = getDialogsByUser(currentUser);
            dialogDtoList = dialogs.stream()
                    .map(dialog ->MapperDialogToDto.convertDialogToDto(dialog, currentUser))
                    .toList();
        } catch (EntityNotFoundException e) {
            log.info(e.getMessage());
        }

        return GetDialogsRs.builder()
                .timestamp(Instant.now().toEpochMilli())
                .total(dialogDtoList.size())
                .offset((int) pageable.getOffset())
                .perPage(pageable.getPageSize())
                .content(dialogDtoList)
                .currentUserId(currentUser)
                .build();
    }


    @Override
    public UnreadCountRs getUnreadMessageCount() {
        long unreadCount = messageRepository.countUnreadMessages(Status.SENT);
        return UnreadCountRs.builder()
                .timestamp(Instant.now().toEpochMilli())
                .data(UnreadCountDto.builder().count(unreadCount).build())
                .build();
    }

    @Override
    public GetMessagesRs getAllMessages(UUID companionId, Pageable pageable) {
        GetMessagesRs response = new GetMessagesRs();
        try {
            Dialog dialog = dialogRepository
                    .findByParticipants(UUID.fromString(GetCurrentUsername.getCurrentUsername()),companionId)
                    .orElseThrow(() -> new EntityNotFoundException("Диалог не найден"));

            Page<Message> messagesPage = messageRepository.findByDialog(dialog, pageable);
            List<MessageDto> messageDtos = messagesPage.getContent().stream()
                    .map(MapperMessageToDto::convertMessageToDto)
                    .sorted(Comparator.comparing(MessageDto::getTime).reversed())
                    .toList();

            response.setTotal((int) messagesPage.getTotalElements());
            response.setOffset((int) pageable.getOffset());
            response.setPerPage(pageable.getPageSize());
            response.setContent(messageDtos);
            response.setTimestamp(Instant.now().toEpochMilli());

        } catch (EntityNotFoundException e) {
            response.setError("Ошибка");
            response.setErrorDescription(e.getMessage());
            response.setTimestamp(Instant.now().toEpochMilli());
        }
        return response;
    }

    @Override
    @Transactional
    public DialogDto getDialog(UUID companionId) {
        UUID currentUser = UUID.fromString(GetCurrentUsername.getCurrentUsername());
        Optional<Dialog> dialogOpt = dialogRepository
                .findByParticipants(currentUser, companionId);
        Dialog dialog = dialogOpt.orElseGet(() -> createDialog(currentUser, companionId));

        return MapperDialogToDto.convertDialogToDto(dialog, currentUser);

    }

    protected Dialog createDialog(UUID currentUser, UUID companion) {
        Dialog dialog = new Dialog();
        dialog.setParticipantOne(currentUser);
        dialog.setParticipantTwo(companion);
        return dialogRepository.save(dialog);
    }

    public List<Dialog> getDialogsByUser(UUID userId) {
        return dialogRepository.findByParticipantOneOrParticipantTwo(userId, userId);
    }

}
