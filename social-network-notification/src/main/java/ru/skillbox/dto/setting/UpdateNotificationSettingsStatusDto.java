package ru.skillbox.dto.setting;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateNotificationSettingsStatusDto {
    private String time;
    private List<NotificationSettingsStatus> status;
}
