package ru.skillbox.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DialogDto {

    private Long id;
    private Long unreadCount;
    private UUID conversationPartner1;
    private UUID conversationPartner2;
    private List<ShortMessageForDialogDto> lastMessage;
}
