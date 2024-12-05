package ru.skillbox.dto.setting;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserNotificationSettingsDto {
    private String time;
    private List<NotificationSettingsDto> data;
    @JsonProperty("user_id")
    private String userId;

}
