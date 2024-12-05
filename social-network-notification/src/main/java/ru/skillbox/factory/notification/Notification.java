package ru.skillbox.factory.notification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.skillbox.model.setting.NotificationType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public abstract class Notification {
    @NotBlank(message = "authorId is required")
    protected String authorId;
    @NotNull(message = "notificationType is required")
    protected NotificationType notificationType;
    @NotBlank(message = "content is required")
    protected String content;
}
