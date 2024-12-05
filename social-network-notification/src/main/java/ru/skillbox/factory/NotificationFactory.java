package ru.skillbox.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.experimental.UtilityClass;
import ru.skillbox.dto.notification.CountDto;
import ru.skillbox.dto.notification.NotificationCountDto;
import ru.skillbox.dto.notification.NotificationDto;
import ru.skillbox.dto.notification.UserNotificationDto;
import ru.skillbox.exception.KafkaNotificationMessageException;
import ru.skillbox.factory.notification.*;
import ru.skillbox.model.notification.NotificationEntity;
import ru.skillbox.model.notification.NotificationToUserEntity;
import ru.skillbox.model.setting.NotificationType;
import ru.skillbox.util.DeserializerModifierWithValidation;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class NotificationFactory {

    public static NotificationMessageType<Notification> getNotificationMessageInstance(
            String kafkaNotificationMessage) throws JsonProcessingException, KafkaNotificationMessageException {

        SimpleModule validationModule = new SimpleModule();
        validationModule.setDeserializerModifier(new DeserializerModifierWithValidation());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(validationModule);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        String notificationType = objectMapper.readTree(kafkaNotificationMessage).get("notificationType").textValue();

        switch (NotificationType.valueOf(notificationType)) {
            case FRIEND_REQUEST: {

                NotificationFriendRequest notification =
                        objectMapper.readValue(kafkaNotificationMessage, NotificationFriendRequest.class);

                return new NotificationMessageType<>(notification);
            }
            case FRIEND_BIRTHDAY: {

                NotificationFriendBirthday notification =
                        objectMapper.readValue(kafkaNotificationMessage, NotificationFriendBirthday.class);

                return new NotificationMessageType<>(notification);
            }
            case POST_COMMENT: {

                NotificationPostComment notification =
                        objectMapper.readValue(kafkaNotificationMessage, NotificationPostComment.class);

                return new NotificationMessageType<>(notification);
            }
            case COMMENT_COMMENT: {

                NotificationCommentComment notification =
                        objectMapper.readValue(kafkaNotificationMessage, NotificationCommentComment.class);

                return new NotificationMessageType<>(notification);
            }
            case POST: {

                NotificationPost notification =
                        objectMapper.readValue(kafkaNotificationMessage, NotificationPost.class);

                return new NotificationMessageType<>(notification);
            }
            case MESSAGE: {

                NotificationMessage notification =
                        objectMapper.readValue(kafkaNotificationMessage, NotificationMessage.class);

                return new NotificationMessageType<>(notification);
            }
            default: {
                throw new KafkaNotificationMessageException("kafkaNotificationMessage created in invalid format");
            }
        }
    }

    public NotificationEntity getNotificationEntity(
            NotificationMessageType<? extends Notification> notification,
            Long timestamp
    ) {

        return NotificationEntity
                .builder()
                .time(LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()))
                .authorId(UUID.fromString(notification.getNotificationInstance().getAuthorId()))
                .notificationType(notification.getNotificationInstance().getNotificationType())
                .content(notification.getNotificationInstance().getContent())
                .build();
    }

    public List<NotificationToUserEntity> getNotificationToUserEntities(
            NotificationEntity notificationEntity, List<UUID> toUserIds) {

        List<NotificationToUserEntity> notificationToUserEntities = new ArrayList<>();
        for (UUID userId : toUserIds) {
            NotificationToUserEntity notificationToUserEntity = NotificationToUserEntity
                    .builder()
                    .notificationId(notificationEntity)
                    .toUserId(userId)
                    .isStatusSent(Boolean.FALSE)
                    .sentTime(notificationEntity.getTime())
                    .build();

            notificationToUserEntities.add(notificationToUserEntity);
        }

        return notificationToUserEntities;
    }

    public NotificationDto getNotificationDto(
            NotificationEntity notificationEntity) {

        return NotificationDto
                .builder()
                .id(notificationEntity.getId().toString())
                .authorId(notificationEntity.getAuthorId().toString())
                .content(notificationEntity.getContent())
                .notificationType(notificationEntity.getNotificationType())
                .sentTime(notificationEntity.getTime())
                .build();
    }

    public UserNotificationDto getUserNotificationDto(List<NotificationDto> data) {
        return UserNotificationDto.builder()
                .timeStamp(LocalDateTime.now().toString())
                .data(data)
                .build();
    }

    public NotificationCountDto getNotificationCountDto(Integer count) {
        return NotificationCountDto.builder()
                .timestamp(LocalDateTime.now().toString())
                .data(new CountDto(count.toString()))
                .build();
    }


}
