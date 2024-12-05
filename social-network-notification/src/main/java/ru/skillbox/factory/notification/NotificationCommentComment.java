package ru.skillbox.factory.notification;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.skillbox.model.setting.NotificationType;

@Getter
@Setter
@NoArgsConstructor
public class NotificationCommentComment extends Notification {
    @NotBlank(message = "toUserId is required")
    private String toUserId;

    public NotificationCommentComment(String authorId, NotificationType notificationType, String content, String toUserId) {
        this.authorId = authorId;
        this.notificationType = notificationType;
        this.content = content;
        this.toUserId = toUserId;
    }
}
