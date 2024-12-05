package ru.skillbox.dto.setting;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import ru.skillbox.model.setting.NotificationType;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationSettingsDto {
    @NotNull(message = "enable is required field")
    private Boolean enable;
    @NotNull(message = "notificationType is required field")
    private NotificationType notificationType;
}
