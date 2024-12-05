package ru.skillbox.dto.notification;

import lombok.*;
import ru.skillbox.model.setting.NotificationType;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDto {
    private String id;
    private String authorId;
    private String content;
    private NotificationType notificationType;
    private LocalDateTime sentTime;
}
