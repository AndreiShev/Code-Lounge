package ru.skillbox.utils.impl;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.skillbox.dto.DialogDto;
import ru.skillbox.dto.ShortMessageForDialogDto;
import ru.skillbox.entity.Dialog;
import ru.skillbox.entity.Message;
import ru.skillbox.entity.Status;

import java.util.*;


@UtilityClass
@Slf4j
public class MapperDialogToDto {

    public static DialogDto convertDialogToDto(Dialog dialog, UUID currentUserId) {
        if (dialog == null) {
            return null;
        }
        List<Message> messages = Optional.ofNullable(dialog.getMessages()).orElse(Collections.emptyList());

        Long unreadCount = messages.stream()
                .filter(message -> message.getStatus().equals(Status.SENT))
                .filter(message -> !message.getAuthor().equals(currentUserId))
                .count();

        List<ShortMessageForDialogDto> lastMessage = messages.stream()
                .map(MapperMessageToDialogMessageDTO::convertMessageToDto)
                .sorted(Comparator.comparing(ShortMessageForDialogDto::getTime).reversed())
                .toList();

        return DialogDto.builder()
                .id(dialog.getId())
                .lastMessage(lastMessage)
                .unreadCount(unreadCount)
                .conversationPartner1(dialog.getParticipantOne())
                .conversationPartner2(dialog.getParticipantTwo())
                .build();
    }
}
