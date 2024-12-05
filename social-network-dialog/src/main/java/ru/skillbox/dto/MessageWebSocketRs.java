package ru.skillbox.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.entity.Status;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageWebSocketRs {

    private LocalDateTime time;
    private UUID conversationPartner1;
    private UUID  conversationPartner2;
    private String messageText;
    private Status readStatus;
    private Long id;
    private Long dialogId;

}
