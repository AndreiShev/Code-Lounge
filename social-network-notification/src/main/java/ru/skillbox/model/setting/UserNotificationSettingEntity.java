package ru.skillbox.model.setting;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "notification_settings")
@EntityListeners(AuditingEntityListener.class)
public class UserNotificationSettingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "user_id")
    private UUID userId;
    @Column(name = "friend_request")
    private Boolean friendRequest;
    @Column(name = "friend_birthday")
    private Boolean friendBirthday;
    @Column(name = "post_comment")
    private Boolean postComment;
    @Column(name = "comment_comment")
    private Boolean commentComment;
    @Column(name = "post")
    private Boolean post;
    @Column(name = "message")
    private Boolean message;
    @Column(name = "send_phone_message")
    private Boolean sendPhoneMessage;
    @Column(name = "send_email_message")
    private Boolean sendEmailMessage;
    @CreatedDate
    @Column(name = "create_date", nullable = false, updatable = false)
    private LocalDateTime createDate;
    @LastModifiedDate
    @Column(name = "change_date", nullable = false)
    private LocalDateTime changeDate;
}
