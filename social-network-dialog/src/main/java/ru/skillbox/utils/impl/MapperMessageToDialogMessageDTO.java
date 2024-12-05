package ru.skillbox.utils.impl;

import lombok.experimental.UtilityClass;
import ru.skillbox.dto.ShortMessageForDialogDto;
import ru.skillbox.entity.Message;

@UtilityClass
public class MapperMessageToDialogMessageDTO {

    public static ShortMessageForDialogDto convertMessageToDto(Message message) {
        if (message == null) {
            return null;
        }

        return ShortMessageForDialogDto.builder()
                .time(message.getTime())
                .authorId(message.getAuthor())
                .messageText(message.getMessageText())
                .readStatus(message.getStatus())
                .build();
    }

}
