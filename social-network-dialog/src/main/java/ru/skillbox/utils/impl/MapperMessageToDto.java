package ru.skillbox.utils.impl;

import lombok.experimental.UtilityClass;
import ru.skillbox.dto.MessageDto;
import ru.skillbox.entity.Message;


@UtilityClass
public class MapperMessageToDto {


    public static MessageDto convertMessageToDto(Message message) {
        if (message == null) {
            return null;
        }

        return MessageDto.builder()
                .id(message.getId())
                .messageText(message.getMessageText())
                .time(message.getTime())
                .conversationPartner1(message.getAuthor())
                .conversationPartner2(message.getRecipient())
                .readStatus(message.getStatus())
                .build();
    }
}
