package ru.skillbox.model.notification;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notifications_to_users")
public class NotificationToUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "notification_id")
    private NotificationEntity notificationId;
    @Column(name = "to_user_id")
    private UUID toUserId;
    @Column(name = "is_status_sent")
    private Boolean isStatusSent;
    @Column(name = "sent_time")
    private LocalDateTime sentTime;
}
