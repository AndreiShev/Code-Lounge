package ru.skillbox.model.notification;

import jakarta.persistence.*;
import lombok.*;
import ru.skillbox.model.setting.NotificationType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "create_time")
    private LocalDateTime time;
    @Column(name = "author_id")
    private UUID authorId;
    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private NotificationType notificationType;
    @Column(name = "content")
    private String content;
    @OneToMany(mappedBy = "notificationId", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<NotificationToUserEntity> notificationToUserEntities = new ArrayList<>();
}
