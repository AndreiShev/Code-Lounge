package ru.skillbox.dto.notification;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserNotificationDto {

    private String timeStamp;
    private List<NotificationDto> data;
}
