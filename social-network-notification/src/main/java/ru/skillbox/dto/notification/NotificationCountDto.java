package ru.skillbox.dto.notification;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationCountDto {
    private String timestamp;
    private CountDto data;

}
