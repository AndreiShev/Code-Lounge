package ru.skillbox.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.skillbox.enums.NotificationType;


import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class NotificationEvent {
    private UUID userId;
    private UUID authorId;
    private NotificationType notificationType;
    private LocalDateTime sentTime;
    private String content;
    private Boolean isStatusSent;
}
